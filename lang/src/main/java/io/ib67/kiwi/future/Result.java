/*
 * MIT License
 *
 * Copyright (c) 2023 InlinedLambdas and Contributors
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

package io.ib67.kiwi.future;

import lombok.*;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.function.Supplier;
import java.util.stream.Stream;

/**
 * A completed state of {@link Future}, it is immutable.
 *
 * @param <R> Type of result
 * @param <E> Type of exception
 */
@ToString
@EqualsAndHashCode
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
@ApiStatus.AvailableSince("0.1.0")
public class Result<R, E> implements Future<R, E> {
    protected final R result;
    @Getter
    protected final E failure;

    public static <R, E> Result<R, E> ok(R result) {
        return new Result<>(result, null);
    }

    public static <R, E> Result<R, E> fail(E failure) {
        return new Result<>(null, failure);
    }

    @Override
    public boolean isDone() {
        return true;
    }

    @Override
    @Nullable
    public R get() {
        return result;
    }

    @Override
    public Future<R, E> onSuccess(SuccessHandler<R> consumer) {
        if (isSuccess()) {
            consumer.accept(result);
        }
        return this;
    }

    @Override
    public Future<R, E> onFailure(FailureHandler<E> consumer) {
        if (isFailed()) {
            consumer.accept(failure);
        }
        return this;
    }

    public boolean isFailed() {
        return failure != null;
    }

    public boolean isSuccess() {
        return failure == null; // result can be null
    }

    @Override
    public Future<R, E> onComplete(ComplementHandler<R, E> consumer) {
        consumer.accept(this);
        return this;
    }

    @Override
    public Result<R, E> sync() {
        return this;
    }

    public R orElse(R fallback) {
        return isSuccess() ? result : fallback;
    }

    public Result<R, NullPointerException> exceptNoNull() {
        if (result != null) {
            return (Result<R, NullPointerException>) this;
        }
        return Result.fail(new NullPointerException("The result is null"));
    }

    public R orElseThrow() {
        if (isSuccess()) {
            return result;
        }
        if (failure instanceof RuntimeException t) {
            throw t;
        } else if (failure instanceof Throwable t) {
            throw new RuntimeException(t);
        } else {
            throw new IllegalStateException(Objects.toString(failure));
        }
    }

    public R orElseThrow(Supplier<? extends RuntimeException> supplier) {
        if (isSuccess()) return result;
        throw supplier.get();
    }

    /**
     * @apiNote This will return an empty stream if your result is null.
     */
    public Stream<@NotNull R> stream() {
        return isSuccess() ? Stream.empty() : Stream.ofNullable(result);
    }

    /**
     * You won't care its type when you are using these logical operators.
     */
    public Result<?, ?> and(Result<?, ?> anotherResult) {
        if (isSuccess()) {
            return anotherResult.isSuccess() ? this : Result.fail(anotherResult.getFailure());
        }
        return this;
    }

    public Result<?, ?> or(Result<?, ?> anotherResult) {
        if (isSuccess()) return this;
        return anotherResult.isSuccess() ? anotherResult : Result.fail("Both are failed");
    }
}
