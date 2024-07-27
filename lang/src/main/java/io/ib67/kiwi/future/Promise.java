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
import org.jetbrains.annotations.NotNull;

/**
 * A {@link Promise} is a completable {@link Future} in order to make {@link Future} immutable. <br>
 * Once the result is set, it cannot be mutated anymore.
 *
 * @param <R> Type of result
 * @param <E> Type of exception
 */
@ApiStatus.AvailableSince("0.4")
public interface Promise<R, E> extends Future<R, E> {
    /**
     * Set the result to SUCCESS and notify handlers.
     *
     * @param result result, sometimes nullable.
     */
    void success(R result);

    /**
     * Set the result to FAILURE and notify handlers.
     *
     * @param exception the reason, cannot be null.
     * @throws IllegalArgumentException if exception is null.
     */
    void failure(@NotNull E exception);

    default void fromResult(Result<R, E> anotherState) {
        anotherState.onSuccess(this::success).onFailure(this::failure);
    }
}
