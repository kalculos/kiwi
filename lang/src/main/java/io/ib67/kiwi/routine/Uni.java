/*
 * MIT License
 *
 * Copyright (c) 2025 InlinedLambdas and Contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package io.ib67.kiwi.routine;

import io.ib67.kiwi.closure.AnyConsumer;
import io.ib67.kiwi.closure.AnySupplier;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.BitSet;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.*;
import java.util.stream.Collector;
import java.util.stream.Collectors;

public interface Uni<T> {
    static <T> Uni<T> of(T value) {
        return t -> t.onValue(value);
    }

    static <T> Uni<T> of(AnySupplier<T> supplier) {
        return c -> {
            try {
                c.onValue(supplier.get());
            } catch (Interruption ignored) {
            } catch (RuntimeException e) {
                throw e;
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        };
    }

    static <T> Uni<T> from(AnyConsumer<InterruptibleConsumer<T>> consumer) {
        return c -> {
            try {
                consumer.consume(c);
            } catch (Interruption ignored) {
            } catch (RuntimeException e) {
                throw e;
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        };
    }

    /**
     * This method is meant to be implemented by Uni instances.
     * For uni's consumers, use {@link Uni#onItem(InterruptibleConsumer)} instead.
     */
    @ApiStatus.Internal
    void accept(InterruptibleConsumer<T> consumer) throws Interruption;

    default void onItem(InterruptibleConsumer<T> consumer) {
        try {
            accept(consumer);
        } catch (Interruption ignored) {
        }
    }

    default <M> Uni<M> map(Function<T, M> mapper) {
        return c -> accept(t -> c.onValue(mapper.apply(t)));
    }

    default <M> Uni<M> flatMap(Function<T, Uni<M>> mapper) {
        return c -> accept(t -> mapper.apply(t).accept(c));
    }

    default Uni<T> filter(Predicate<T> predicate) {
        return c -> accept(t -> {
            if (predicate.test(t)) {
                c.onValue(t);
            }
        });
    }

    default Uni<T> unique() {
        var set = new HashSet<>();
        return filter(set::add);
    }

    default <M> Uni<M> multiMap(InterruptibleBiConsumer<T, InterruptibleConsumer<M>> mapper) {
        return c -> accept(t -> mapper.onValue(t, c));
    }

    default Uni<T> limit(int limit) {
        return c -> {
            int[] limitArr = new int[1];
            accept(t -> {
                if (limitArr[0] < limit) {
                    limitArr[0]++;
                    c.onValue(t);
                } else {
                    throw Interruption.INTERRUPTION;
                }
            });
        };
    }

    default Uni<T> peek(InterruptibleConsumer<T> consumer) {
        return c -> accept(t -> {
            consumer.onValue(t);
            c.onValue(t);
        });
    }

    default boolean any(Predicate<T> predicate) {
        var hasAny = new boolean[]{false};
        try {
            accept(it -> {
                if (predicate.test(it)) {
                    hasAny[0] = true;
                    throw Interruption.INTERRUPTION;
                }
            });
        } catch (Interruption ignored) {
        }
        return hasAny[0];
    }

    default boolean all(Predicate<T> predicate) {
        var hasAll = new boolean[]{true};
        try {
            accept(it -> {
                if (!predicate.test(it)) {
                    hasAll[0] = false;
                    throw Interruption.INTERRUPTION;
                }
            });
        } catch (Interruption ignored) {
        }
        return hasAll[0];
    }

    default boolean none(Predicate<T> predicate) {
        return !all(predicate);
    }

    default Uni<T> reduce(BinaryOperator<T> reducer) {
        return c -> {
            var buffer = new Object[1];
            try {
                accept(t -> {
                    if (buffer[0] == null) {
                        buffer[0] = t;
                    } else {
                        buffer[0] = reducer.apply((T) buffer[0], t);
                    }
                });
            } catch (Interruption ignored) {
            }
            c.onValue((T) buffer[0]);
        };
    }

    @Nullable
    default T takeOne() {
        var objs = new Object[1];
        onItem(c -> {
            objs[0] = c;
            throw Interruption.INTERRUPTION;
        });
        return (T) objs[0];
    }

    default <A, R> R collect(Collector<T, A, R> collector) {
        A container = collector.supplier().get();
        onItem(t -> collector.accumulator().accept(container, t));
        return collector.finisher().apply(container);
    }

    default List<T> toList() {
        return collect(Collectors.toUnmodifiableList());
    }

    @FunctionalInterface
    interface InterruptibleConsumer<T> extends Consumer<T> {
        /**
         * You should not call this method since it blocks the Interruption.
         * For Uni providers, always call {@link InterruptibleConsumer#onValue(Object)} instead.
         * This method is only meant to be compatible with Consumer
         *
         * @param t the input argument
         */
        @Deprecated
        default void accept(T t) {
            try {
                onValue(t);
            } catch (Interruption ignored) {
            }
        }

        void onValue(T t) throws Interruption;
    }

    @FunctionalInterface
    interface InterruptibleBiConsumer<A,B> extends BiConsumer<A,B> {

        /**
         * Also see {@link InterruptibleConsumer#accept(InterruptibleConsumer)}
         * @param a the first input argument
         * @param b the second input argument
         */
        @Override
        @Deprecated
        default void accept(A a, B b){
            try{
                onValue(a,b);
            }catch (Interruption ignored){}
        }

        void onValue(A a, B b) throws Interruption;
    }
}
