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

package io.ib67.kiwi;

import io.ib67.kiwi.routine.Uni;
import lombok.Generated;
import lombok.experimental.UtilityClass;

import java.security.SecureRandom;
import java.util.concurrent.ThreadLocalRandom;

@UtilityClass
@Generated // not going to unit-test this
public final class RandomHelper {

    public static int number(int bound) {
        return ThreadLocalRandom.current().nextInt(bound);
    }

    public static boolean bool() {
        return ThreadLocalRandom.current().nextBoolean();
    }

    public static boolean bool(float possibility) {
        return ThreadLocalRandom.current().nextDouble(1) > possibility;
    }

    public static int number(int from, int to) {
        return ThreadLocalRandom.current().nextInt(from, to);
    }

    public static int number() {
        return number(9999);
    }

    public static String string() {
        return string(16);
    }

    public static String string(int len) {
        StringBuilder sb = new StringBuilder(len);
        for (int i = 0; i < len; i++) {
            sb.append((char) number(65, 65 + 57));
        }
        return sb.toString();
    }

    public static Uni<Integer> randomInt() {
        return c -> {
            while (true) c.onValue(number());
        };
    }

    public static Uni<Integer> safeRandomInt() {
        var rand = new SecureRandom();
        return c -> {
            while (true) c.onValue(rand.nextInt());
        };
    }
}
