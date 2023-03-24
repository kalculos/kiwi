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

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

@ApiStatus.AvailableSince("0.4")
public abstract class AbstractPromise<R, E> implements Promise<R, E> {
    protected final List<Consumer> handlers = new ArrayList<>(0);

    @Getter(AccessLevel.PROTECTED)
    @Setter(AccessLevel.PROTECTED)
    protected Result<R, E> result;

    @Override
    public boolean isDone() {
        return getResult() != null;
    }

    @Override
    public @Nullable R get() throws IllegalStateException {
        if (!isDone()) throw new IllegalStateException("The future is not completed now.");
        return getResult().get();
    }

    @Override
    public Promise<R, E> onSuccess(SuccessHandler<R> consumer) {
        if (isDone()) {
            if (getResult().isSuccess()) consumer.accept(getResult().get());
        } else {
            handlers.add(consumer);
        }
        return this;
    }

    @Override
    public Promise<R, E> onFailure(FailureHandler<E> consumer) {
        if (isDone()) {
            if (getResult().isFailed()) consumer.accept(getResult().getFailure());
        } else {
            handlers.add(consumer);
        }
        return this;
    }

    @Override
    public Future<R, E> onComplete(ComplementHandler<R, E> consumer) {
        if (isDone()) {
            consumer.accept(getResult());
        } else {
            handlers.add(consumer);
        }
        return this;
    }

    @Override
    public abstract Result<R, E> sync() throws InterruptedException;

    @Override
    public void success(R r) {
        setResult(Result.ok(r));
        notifyHandlers();
    }

    private void notifyHandlers() {
        final var result = getResult();
        if (!result.isDone()) throw new IllegalStateException("This promise is not completed.");
        final var succeed = result.isSuccess();
        final var success = result.result;
        final var failure = result.failure;
        for (Consumer handler : handlers) {
            if (succeed && (handler instanceof Future.SuccessHandler successHandler)) {
                successHandler.accept(success);
            } else if (!succeed && (handler instanceof Future.FailureHandler failureHandler)) {
                failureHandler.accept(failure);
            } else if (handler instanceof Future.ComplementHandler complementHandler) {
                complementHandler.accept(result);
            }
        }
    }

    @Override
    public void failure(@NotNull E exception) {
        setResult(Result.fail(exception));
        notifyHandlers();
    }
}
