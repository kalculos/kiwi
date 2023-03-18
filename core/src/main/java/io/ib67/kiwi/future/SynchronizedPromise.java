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

import io.ib67.kiwi.Kiwi;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Consumer;

import static java.util.Objects.requireNonNull;

/**
 * A thread safe wrapper for other {@link Promise}s.<br>
 * synchronize blocks are not friendly to virtual threads, so this one is based on reentrantLocks.
 */
@ApiStatus.AvailableSince("0.4")
public class SynchronizedPromise<R, E> implements Promise<R, E> {
    protected final Promise<R, E> wrappedPromise;
    protected final Lock lock;

    public SynchronizedPromise(Promise<R, E> wrappedPromise) {
        requireNonNull(this.wrappedPromise = wrappedPromise);
        lock = new ReentrantLock();
    }

    @Override
    public boolean isDone() {
        try (var ignored = Kiwi.withLock(lock)) {
            return wrappedPromise.isDone();
        }
    }

    @Override
    public @Nullable R get() throws IllegalStateException {
        try (var ignored = Kiwi.withLock(lock)) {
            return wrappedPromise.get();
        }
    }

    @Override
    public Future<R, E> onSuccess(Consumer<R> consumer) {
        try (var ignored = Kiwi.withLock(lock)) {
            return wrappedPromise.onSuccess(consumer);
        }
    }

    @Override
    public Future<R, E> onFailure(Consumer<E> consumer) {
        try (var ignored = Kiwi.withLock(lock)) {
            return wrappedPromise.onFailure(consumer);
        }
    }

    @Override
    public Future<R, E> onComplete(Consumer<Result<R, E>> consumer) {
        try (var ignored = Kiwi.withLock(lock)) {
            return wrappedPromise.onComplete(consumer);
        }
    }

    @Override
    public Result<R, E> sync() throws InterruptedException {
        try (var ignored = Kiwi.withLock(lock)) {
            return wrappedPromise.sync();
        }
    }

    @Override
    public void success(R result) {
        try (var ignored = Kiwi.withLock(lock)) {
            wrappedPromise.success(result);
        }
    }

    @Override
    public void failure(@NotNull E exception) {
        try (var lo = Kiwi.withLock(lock)) {
            wrappedPromise.failure(exception);
        }
    }
}
