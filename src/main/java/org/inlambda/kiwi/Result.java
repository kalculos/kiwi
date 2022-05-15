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

package org.inlambda.kiwi;

import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import org.inlambda.kiwi.option.Some;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Like {@link Some}, but with error handling and more semantic.
 *
 * @param <T>
 * @param <E>
 */
@ToString
@EqualsAndHashCode
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class Result<T, E> {
    private final T result;
    private final E err;

    public static <T> Result<T, ?> fromOption(Some<T> option) {
        return option.asResult();
    }

    public static <T> Result<T, ?> fromOptional(Optional<T> optional) {
        return optional.isPresent() ? ok(optional.get()) : err();
    }

    public static <T, E> @NotNull Result<T, E> ok(@NotNull T T) {
        return new Result<>(T, null);
    }

    public static <E> @NotNull Result<Unit, E> ok() {
        return new Result<>(Unit.UNIT, null);
    }

    public static <T, E> @NotNull Result<T, E> err(@NotNull E err) {
        return new Result<>(null, err);
    }

    public static <T> @NotNull Result<T, Unit> err() {
        return new Result<>(null, Unit.UNIT);
    }

    public T get() throws Throwable {
        if (err != null) {
            if (err instanceof Throwable) {
                throw (Throwable) err;
            }
            throw new IllegalStateException(err.toString());
        }
        return result;
    }

    public T orElseThrow(Supplier<RuntimeException> supplier) {
        if (err == null) {
            throw supplier.get();
        }
        return result;
    }

    public T orElseThrow() {
        if (err != null) {
            throw new IllegalStateException("No values represent but error: " + err);
        }
        return result;
    }

    public T or(T anotherResult) {
        return err == null ? result : anotherResult;
    }

    public T or(Supplier<T> supplier) {
        return err == null ? result : supplier.get();
    }


    public Result<T, E> success(Consumer<T> consumer) {
        if (err == null) {
            consumer.accept(result);
        }
        return this;
    }

    public Result<T, E> fail(Consumer<E> consumer) {
        if (result == null) {
            consumer.accept(err);
        }
        return this;
    }

    public <A> Result<A, E> map(Function<T, A> function) {
        if (err == null) {
            return Result.ok(function.apply(result));
        } else {
            return Result.err(err);
        }
    }

    public boolean isFailed() {
        return err != null;
    }

    public Optional<T> asOptional() {
        return toOption().asOptional();
    }

    public Option<T> toOption() {
        if (err != null) {
            return Option.none();
        }
        return Option.of(result);
    }

}
