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

package io.ib67.kiwi.bukkit;

import lombok.experimental.UtilityClass;
import org.bukkit.Location;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
@UtilityClass
public class LocHash {
    public static long posHash(final Location location) {
        return longHash(Double.doubleToLongBits(location.getX()),
                Double.doubleToLongBits(location.getY()),
                Double.doubleToLongBits(location.getZ()));
    }

    public static boolean posEq(Location a, Location b) {
        return a.getWorld() == b.getWorld() &&
                Math.floor(a.getX()) == Math.floor(b.getX()) &&
                Math.floor(a.getY()) == Math.floor(b.getY()) &&
                Math.floor(a.getZ()) == Math.floor(b.getZ());
    }

    // borrowed from baritone.
    public static long longHash(final long x, final long y, final long z) {
        long hash = 3241;
        hash = 3457689L * hash + x;
        hash = 8734625L * hash + y;
        hash = 2873465L * hash + z;
        return hash;
    }
}
