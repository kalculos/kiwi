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

/**
 * A out-of-box {@link Promise} but not recommended to use it.<br>
 * Instead, subclass {@link AbstractPromise} to create your own one according to your project architecture.
 *
 * @param <R> Type of result
 * @param <E> Type of exception
 */
@ApiStatus.AvailableSince("0.4")
public class TaskPromise<R, E extends Exception> extends AbstractPromise<R, E> {
    @Override
    public Result<R, E> sync() throws InterruptedException {
        while (!isDone()) {
            synchronized (this) {
                this.wait();
            }
        }
        return result;
    }

    @Override
    protected void setResult(Result<R, E> result) {
        super.setResult(result);
        synchronized (this) {
            this.notifyAll();
        }
    }
}
