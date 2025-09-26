/*
 * MIT License
 *
 * Copyright (c) 2025 Kalculos and Contributors
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

import io.ib67.kiwi.closure.AnyFunction;
import io.ib67.kiwi.closure.AnyRunnable;
import io.ib67.kiwi.closure.AnySupplier;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * A Result represents the result of some operation, which can be success or failure.
 * For a success operation, this result is a {@link Some}. For the failed one, the result is a {@link Fail}<br />
 * You can also capture a checked exception as a Result by using {@link Result#fromAny(AnySupplier)} method.
 *
 * @param <T> the type of success value
 */
@ApiStatus.AvailableSince("1.0.0")
public sealed interface Result<T> extends Uni<T> permits Fail, Some {
    /**
     * Applies a closeable to mapper then close the closeable.
     *
     * @return some result from mapper, otherwise fail.
     */
    static <T, C extends AutoCloseable> Result<T> fromCloseable(C closeable, AnyFunction<C, T> mapper) {
        try (var _c = closeable) {
            return new Some<>(mapper.apply(closeable));
        } catch (Exception e) {
            return Fail.of(e);
        }
    }

    /**
     * Captures an exception to make Fail, otherwise make {@link Some}
     *
     * @param supplier operation
     * @param <T>      type of result from operation
     * @return result
     */
    static <T> Result<T> fromAny(AnySupplier<T> supplier) {
        try {
            return new Some<>(supplier.get());
        } catch (Exception e) {
            return Fail.of(e);
        }
    }

    /**
     * Simliar to {@link #fromAny(AnySupplier)} but treats null as failure. When null is given, it returns {@link Fail#none()}
     *
     * @param supplier operation
     * @param <T>      type of result from operation
     * @return result
     */
    static <T> Result<T> fromNotNull(AnySupplier<T> supplier) {
        try {
            var r = supplier.get();
            if (r == null) return Fail.none();
            return new Some<>(r);
        } catch (Exception e) {
            return Fail.of(e);
        }
    }

    /**
     * It captures an exception from the given runnable, otherwise {@link Some} null
     *
     * @param runnable operation
     * @return result
     */
    static Result<? extends @Nullable Object> runAny(AnyRunnable runnable) {
        try {
            runnable.run();
            return new Some<>(null);
        } catch (Exception e) {
            return Fail.of(e);
        }
    }

    default T orElse(T defaultValue) {
        return switch (this) {
            case Fail f -> defaultValue;
            case Some(T v) -> v;
        };
    }

    default T orElseGet(Supplier<T> supplier) {
        return switch (this) {
            case Fail f -> supplier.get();
            case Some(T value) -> value;
        };
    }

    default T orElseThrow(Supplier<? extends RuntimeException> supplier) {
        return switch (this) {
            case Fail f -> throw supplier.get();
            case Some(T t) -> t;
        };
    }

    default T orElseThrow() {
        return switch (this) {
            case Fail(RuntimeException e) -> {
                throw e;
            }
            case Fail(Object o) -> {
                throw new IllegalStateException("Failure: " + o);
            }
            case Some(T value) -> value;
        };
    }

    default Result<T> onFail(Consumer<Fail<?>> failConsumer) {
        if (this instanceof Fail) {
            failConsumer.accept((Fail) this);
        }
        return this;
    }

    default Result<T> onSuccess(InterruptibleConsumer<T> consumer) {
        if (this instanceof Some(T t)) {
            consumer.accept(t);
        }
        return this;
    }

    default <M> Result<M> flatMapResult(Function<T, Result<M>> resultMapper) {
        if (this instanceof Some(T t)) {
            return resultMapper.apply(t);
        }
        return (Result<M>) this;
    }

    default Optional<T> toOptional() {
        return Optional.ofNullable(result());
    }

    @Override
    default Result<T> filter(Predicate<? super T> predicate) {
        if (this instanceof Fail) {
            return this;
        } else if (this instanceof Some(T t)) {
            if (predicate.test(t)) {
                return this;
            }
        }
        return Fail.none();
    }

    @Override
    default <M> Result<M> map(Function<? super T, M> mapper) {
        if (this instanceof Fail f) {
            return f;
        } else if (this instanceof Some(T t)) {
            return new Some<>(mapper.apply(t));
        }
        throw new IllegalStateException("Impossible");
    }

    /**
     * @return the returned value of the successful operation. For {@link Fail}, it must be null.
     */
    @Nullable
    T result();
}
