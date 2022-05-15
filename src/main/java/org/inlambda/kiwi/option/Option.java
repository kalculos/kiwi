/*
 * MIT License
 *
 * Copyright (c) 2022 InlinedLambdas and Contributors
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

package org.inlambda.kiwi.option;

import org.inlambda.kiwi.Result;
import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Stream;

/**
 * An immutable data structure that represents a single value or a missing value.
 * Can be serializable,but you'll have to use {@link Present} if you dont want to make a serializer
 * If any value present: {@link org.inlambda.kiwi.option.Some<T>}
 * If no values present: {@link org.inlambda.kiwi.option.None<T>}
 * <p>
 * Method with Uppder-Camel-Case names always re-throw exception from your consumer. You can use this feature easily throwing exceptions outside.
 *
 * @param <T> type of value
 */

public interface Option<T> extends Iterable<T>, Serializable {

    static <T> Option<T> of(T value) {
        return value == null ? none() : of(value);
    }

    @SuppressWarnings("unchecked")
    static <T> Option<T> none() {
        return (Option<T>) None.NONE;
    }

    @NotNull Option<T> If(Predicate<T> condition, Consumer<T> action);

    @NotNull Option<T> IfNull(Runnable action);

    @NotNull Option<T> IfNotNull(Consumer<T> action);

    @SuppressWarnings("unchecked")
    <R> Option<T> IfCast(Class<R> type, Consumer<Option<R>> consumer);

    @NotNull <R> Option<T> Case(Function<T, R> condition, Consumer<R> action);

    boolean is(Class<?> type);

    boolean contains(T t);

    @NotNull Stream<T> stream();

    boolean isEmpty();

    Option<T> filter(Predicate<T> predicate);

    <R> Option<R> map(Function<T, R> mapper);

    <R> Option<R> flatMap(Function<T, Option<R>> mapper);

    T or(T other);

    T orElseThrow(Supplier<RuntimeException> exceptionSupplier);

    T orElseThrow();

    T get();

    /**
     * This returns {@link Result#err()} if the option is empty, otherwise {@link Result#ok(T)}
     *
     * @return
     */
    Result<T, ?> asResult();

    Optional<T> asOptional();

    Present<T> toPresent();
}
