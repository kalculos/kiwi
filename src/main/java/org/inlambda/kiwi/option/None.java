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

import org.inlambda.kiwi.Option;
import org.inlambda.kiwi.Result;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.Iterator;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Stream;

public final class None<T> implements Option<T> {
    public static final None<?> NONE = new None<>();

    private None() {

    }

    @Override
    public @NotNull Option<T> If(Predicate<T> condition, Consumer<T> action) {
        return this;
    }

    @Override
    public @NotNull Option<T> IfNull(Runnable action) {
        return this;
    }

    @Override
    public @NotNull Option<T> IfNotNull(Consumer<T> action) {
        return this;
    }

    @Override
    public <R> Option<T> IfCast(Class<R> type, Consumer<Option<R>> consumer) {
        return this;
    }

    @Override
    public @NotNull <R> Option<T> Case(Function<T, R> condition, Consumer<R> action) {
        return this;
    }

    @Override
    public boolean is(Class<?> type) {
        return false;
    }

    @Override
    public boolean contains(T t) {
        return false;
    }

    @Override
    public @NotNull Stream<T> stream() {
        return Stream.empty();
    }

    @Override
    public boolean isEmpty() {
        return true;
    }

    @Override
    public Option<T> filter(Predicate<T> predicate) {
        return this;
    }

    @Override
    public <R> Option<R> map(Function<T, R> mapper) {
        return Option.none();
    }

    @Override
    public <R> Option<R> flatMap(Function<T, Option<R>> mapper) {
        return Option.none();
    }

    @Override
    public T or(T other) {
        return other;
    }

    @Override
    public T orElseThrow(Supplier<RuntimeException> exceptionSupplier) {
        throw exceptionSupplier.get();
    }

    @Override
    public T orElseThrow() {
        throw new NullPointerException("Nothing in Option");
    }

    @Override
    public T get() {
        return null;
    }

    @Override
    public Result<T, ?> asResult() {
        return Result.err();
    }

    @Override
    public Optional<T> asOptional() {
        return Optional.empty();
    }

    @NotNull
    @Override
    public Iterator<T> iterator() {
        return Collections.emptyIterator();
    }
}
