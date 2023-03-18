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

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

/**
 * An incomplete state of some computations that is going to be evaluated.
 *
 * @param <R> Type of result
 * @param <E> Type of possible exceptions
 */
@ApiStatus.AvailableSince("0.4")
public interface Future<R, E> {

    /**
     * To check if the computation is done.
     *
     * @return done
     */
    boolean isDone();

    /**
     * Get result from the future
     *
     * @return computed result, or null if failed.
     * @throws IllegalStateException if future is not completed
     */
    @Nullable R get() throws IllegalStateException;

    /**
     * Subscribe to successful results, or invoke the handler if the future is done.
     *
     * @param consumer handler
     * @return this
     */
    Future<R, E> onSuccess(Consumer<R> consumer);

    /**
     * Subscribe to exceptions, or invoke the handler if the future is failed.
     *
     * @param consumer handler
     * @return this
     */
    Future<R, E> onFailure(Consumer<E> consumer);

    /**
     * Your handler will be called when the future is done, regardless of success or failure.
     *
     * @param consumer handler
     * @return this
     */
    Future<R, E> onComplete(Consumer<Result<R, E>> consumer);

    /**
     * Blocks current thread until the computation is done.
     *
     * @return this
     */
    Result<R, E> sync() throws InterruptedException;

    /**
     * A fail-fast version of {@link #sync()}
     *
     * @return this
     */
    default Future<R, E> syncUninterruptible() {
        try {
            return sync();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * A utility to deal with JDK Future required APIs.
     *
     * @return JDK Future
     * @apiNote The returned future is not fully capable.
     */
    default CompletableFuture<R> toStandardFuture() {
        var future = new WrapperCompletableFuture<>(this);
        onComplete(result -> result.onSuccess(future::complete).onFailure(fail -> {
            if (fail instanceof Throwable t) future.completeExceptionally(t);
        }));
        return future;
    }
}
