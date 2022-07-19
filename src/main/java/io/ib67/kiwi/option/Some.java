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

package io.ib67.kiwi.option;

import io.ib67.kiwi.Result;
import io.ib67.kiwi.SingleIterator;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Iterator;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Stream;

@RequiredArgsConstructor()
public class Some<T> implements Option<T> {
    private final T value;

    @Override
    @NotNull
    public Option<T> If(Predicate<T> condition, Consumer<T> action) {
        if (condition.test(value)) {
            try {
                action.accept(value);
            } catch (RuntimeException exception) {
                throw exception;
            }
        }
        return this;
    }

    @Override
    @NotNull
    public Option<T> IfNull(Runnable action) {
        return this;
    }

    @Override
    @NotNull
    public Option<T> IfNotNull(Consumer<T> action) {
        action.accept(value);
        return this;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <R> Option<T> IfCast(Class<R> type, Consumer<Option<R>> consumer) {
        if (type.isInstance(value)) {
            consumer.accept(Option.of((R) value));
        }
        return this;
    }

    @Override
    @NotNull
    public <R> Option<T> Case(Function<T, R> condition, Consumer<R> action) {
        var val = condition.apply(value);
        if (val != null) {
            try {
                action.accept(val);
            } catch (RuntimeException exception) {
                throw exception;
            }
        }
        return this;
    }

    @Override
    public boolean is(Class<?> type) {
        return type.isInstance(value);
    }

    @Override
    public boolean contains(T t) {
        return value.equals(t);
    }

    @Override
    @NotNull
    public Stream<T> stream() {
        return Stream.of(value);
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    @Override
    public Option<T> filter(Predicate<T> predicate) {
        if (predicate.test(value)) {
            return this;
        }
        return Option.none();
    }

    @Override
    public <R> Option<R> map(Function<T, R> mapper) {
        if (isEmpty()) {
            return Option.none();
        }
        return Option.of(mapper.apply(value));
    }

    @Override
    public <R> Option<R> flatMap(Function<T, Option<R>> mapper) {
        return mapper.apply(value);
    }

    @Override
    public T or(T other) {
        return other;
    }

    @Override
    public T orElseThrow(Supplier<RuntimeException> exceptionSupplier) {
        return value;
    }

    @Override
    public T orElseThrow() {
        return value;
    }

    @Override
    @Nullable
    public T get() {
        return value;
    }

    @Override
    public Result<T, ?> asResult() {
        return Result.ok(value);
    }

    @Override
    public Optional<T> asOptional() {
        return Optional.of(value);
    }

    @Override
    public Present<T> toPresent() {
        return new Present<>(value);
    }

    @NotNull
    @Override
    public Iterator<T> iterator() {
        return new SingleIterator<>(value);
    }
}
