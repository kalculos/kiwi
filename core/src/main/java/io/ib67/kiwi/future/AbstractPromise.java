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
    protected final List<Consumer<R>> successHandlers = new ArrayList<>();
    protected final List<Consumer<E>> failureHandlers = new ArrayList<>();
    protected final List<Consumer<Result<R, E>>> complementListeners = new ArrayList<>();

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
    public Promise<R, E> onSuccess(Consumer<R> consumer) {
        if (isDone()) {
            if (getResult().isSuccess()) consumer.accept(getResult().get());
        } else {
            successHandlers.add(consumer);
        }
        return this;
    }

    @Override
    public Promise<R, E> onFailure(Consumer<E> consumer) {
        if (isDone()) {
            if (getResult().isFailed()) consumer.accept(getResult().getFailure());
        } else {
            failureHandlers.add(consumer);
        }
        return this;
    }

    @Override
    public Future<R, E> onComplete(Consumer<Result<R, E>> consumer) {
        if (isDone()) {
            consumer.accept(getResult());
        } else {
            complementListeners.add(consumer);
        }
        return this;
    }

    @Override
    public abstract Result<R, E> sync() throws InterruptedException;

    @Override
    public void success(R r) {
        setResult(Result.ok(r));
        notifyCompleted();
        notifySuccess();
    }

    protected void notifyCompleted() {
        var result = getResult();
        complementListeners.forEach(it -> it.accept(result));
    }

    protected void notifySuccess() {
        var result = this.getResult().get();
        successHandlers.forEach(it -> it.accept(result));
    }

    @Override
    public void failure(@NotNull E exception) {
        setResult(Result.fail(exception));
        notifyCompleted();
        notifyFailure();
    }

    protected void notifyFailure() {
        var failure = this.getResult().getFailure();
        failureHandlers.forEach(it -> it.accept(failure));
    }
}
