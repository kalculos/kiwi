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
import org.bukkit.World;
import org.jetbrains.annotations.ApiStatus;

@UtilityClass
public class FastLocHash {
    public static long posHash(final Location location) {
        return longHash(worldHash(location.getWorld()),
                (int) (Double.doubleToLongBits(location.getX()) ^ (Double.doubleToLongBits(location.getX()) >>> 32)),
                (int) (Double.doubleToLongBits(location.getY()) ^ (Double.doubleToLongBits(location.getY()) >>> 32)),
                (int) (Double.doubleToLongBits(location.getZ()) ^ (Double.doubleToLongBits(location.getZ()) >>> 32)));
    }

    @ApiStatus.Internal
    public static int worldHash(final World world) {
        return world == null ? 0 : world.getUID().hashCode(); // Most server doesn't have too much worlds.
    }

    // borrowed from baritone.
    public static long longHash(final int worldId, final int x, final int y, final int z) {
        long hash = 3241;
        hash = 1209428L * hash + worldId;
        hash = 3457689L * hash + x;
        hash = 8734625L * hash + y;
        hash = 2873465L * hash + z;
        return hash;
    }
}