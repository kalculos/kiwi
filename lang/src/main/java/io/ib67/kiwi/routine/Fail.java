/*
 * MIT License
 *
 * Copyright (c) 2025 Kalculos and Contributors
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

package io.ib67.kiwi.routine;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;

/**
 * Represents a value from failure operation. It can be {@link Nothing} or an error.
 * As a {@link Uni}, {@link Fail#onItem(InterruptibleConsumer)} do nothing and its {@link #result()} always returns null.
 * @param failure
 * @param <T>
 */
@ApiStatus.AvailableSince("1.0.0")
public record Fail<T>(Object failure) implements Result<T> {
    /**
     * Given a null in return, you can't know if this null represents failure or empty.
     * Nothing clarifies the confusion caused by null. It represents Nothing.
     * Often used with {@link Fail}
     */
    @ApiStatus.AvailableSince("1.0.0")
    public static class Nothing {
        public static final Nothing NOTHING = new Nothing();

        private Nothing() {
        }
    }

    private static final Fail<Nothing> NONE = new Fail<>(Nothing.NOTHING);

    /**
     * @param <T> any type
     * @return a Fail with {@link Nothing} as its value.
     */
    @SuppressWarnings("unchecked")
    public static <T> Fail<T> none() {
        return (Fail<T>) NONE;
    }

    /**
     * Create a Fail with failure reason.
     *
     * @param failure reason
     * @param <T>     type of element
     * @return Fail with reason
     */
    public static <T> Fail<T> of(Object failure) {
        return new Fail<>(failure);
    }

    /**
     * Always null.
     *
     * @return null
     */
    @Contract(value = "-> null", pure = true)
    @Override
    @Nullable
    public T result() {
        return null;
    }

    /**
     * Always do nothing.
     * @param consumer consumer
     */
    @Override
    public void accept(InterruptibleConsumer<T> consumer) {
    }
}
