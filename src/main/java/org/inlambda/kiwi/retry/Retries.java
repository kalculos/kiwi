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

package org.inlambda.kiwi.retry;

import org.inlambda.kiwi.Result;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;
import java.util.function.Predicate;

import static java.util.Objects.requireNonNull;

@ApiStatus.AvailableSince("0.3.1")
public class Retries<I, O, E extends Throwable> {
    private final Function<I, Result<O, ? extends E>> run;
    private final Predicate<O> until;
    private final Predicate<? super E> onFail;
    private final int maxTries;
    private final I input;

    public Retries(Function<I, Result<O, ? extends E>> run, Predicate<O> until, Predicate<? super E> onFail, int maxTries, I input) {
        this.input = input;
        requireNonNull(this.run = run);
        this.until = until == null ? t -> true : until;
        this.onFail = onFail == null ? e -> true : onFail;
        if (maxTries < 0) {
            throw new IllegalArgumentException("maxTries must be >= 0");
        }
        this.maxTries = Math.min(maxTries, 1);
    }

    public static <I, O, E extends Throwable> RetriesBuilder<I, O, E> of(@Nullable I input, Class<O> exceptedOutputType) {
        return new RetriesBuilder<>(input);
    }


    /**
     * Execute the given action and retry it if it fails.
     *
     * @return the result of the action
     */
    private Result<O, ?> execute() {
        for (int i = 0; i < maxTries; i++) {
            try {
                var result = run.apply(input);
                if (result.isFailed()) {
                    if (onFail.test(result.getErr())) {
                        continue;
                    } else {
                        return result;
                    }
                }
                if (until.test(result.orElseThrow())) {
                    return result;
                }
            } catch (Exception e) {
                throw new IllegalStateException("Unexpected Behaviour. Please use Result.err instead of throw it instantly.");
            }
        }
        return Result.err(new IllegalStateException("Retryer failed after " + maxTries + " tries."));
    }

    public static class RetriesBuilder<I, O, E extends Throwable> {
        private final I input;
        private Function<I, Result<O, ? extends E>> run;
        private Predicate<O> until;
        private Predicate<? super E> onFail;
        private int maxTries;

        RetriesBuilder(I input) {
            this.input = input;
        }

        public RetriesBuilder<I, O, ? extends E> run(Function<I, Result<O, ? extends E>> run) {
            this.run = run;
            return this;
        }

        public RetriesBuilder<I, O, E> until(Predicate<O> until) {
            this.until = until;
            return this;
        }

        public RetriesBuilder<I, O, E> onFail(Predicate<? super E> onFail) {
            this.onFail = onFail;
            return this;
        }

        public RetriesBuilder<I, O, E> maxTries(int maxTries) {
            this.maxTries = maxTries;
            return this;
        }

        public Result<O, ?> execute() {
            return new Retries<I, O, E>(run, until, onFail, maxTries, input).execute();
        }

        public String toString() {
            return "Retries.RetriesBuilder(run=" + this.run + ", until=" + this.until + ", onFail=" + this.onFail + ", maxTries=" + this.maxTries + ", input=" + this.input + ")";
        }
    }
}
