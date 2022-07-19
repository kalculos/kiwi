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

package io.ib67.kiwi.range;

import io.ib67.kiwi.RandomHelper;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.ApiStatus;

@RequiredArgsConstructor
@ApiStatus.AvailableSince("0.1.0")
@Getter
public final class IntRange {
    private final int atLeast;
    private final int atMost;

    public boolean isInRange(int i) {
        return atLeast <= i && i <= atMost;
    }

    public void inRange(int i) {
        if (!isInRange(i)) {
            throw new IllegalArgumentException(i + " is not in range [" + atLeast + "," + atMost + "]");
        }
    }

    private static final IntRange POSITIVE = new IntRange(1, Integer.MAX_VALUE);

    public static IntRange rangeOf(int atLeast, int atMost) {
        return new IntRange(atLeast, atMost);
    }

    public static IntRange rangeAtMost(int atMost) {
        return new IntRange(Integer.MIN_VALUE, atMost);
    }

    public static IntRange rangeAtLeast(int atLeast) {
        return new IntRange(atLeast, Integer.MAX_VALUE);
    }

    public static IntRange rangePositive() {
        return POSITIVE;
    }

    public static IntRange rangeNegative() {
        return NEGATIVE;
    }

    private static final IntRange NEGATIVE = new IntRange(Integer.MIN_VALUE, -1);

    public int random() {
        return RandomHelper.number(atLeast, atMost);
    }
}
