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

package kiwi.bukkit;

import be.seeseemelk.mockbukkit.MockBukkit;
import io.ib67.kiwi.bukkit.FastLocHash;
import org.bukkit.Location;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

import java.util.ArrayList;
import java.util.List;

import static io.ib67.kiwi.RandomHelper.number;
import static java.util.Objects.requireNonNull;
import static org.bukkit.Bukkit.getWorlds;

@BenchmarkMode(Mode.Throughput)
@State(Scope.Benchmark)
public class FastLocHashBench {
    private static final List<Location> locations = new ArrayList<>(1000);

    @Setup
    public void setupLocations() {
        MockBukkit.getOrCreateMock();
        MockBukkit.createMockPlugin();
        var a = MockBukkit.getMock().addSimpleWorld("testA");
        var b = MockBukkit.getMock().addSimpleWorld("testB");
        requireNonNull(getWorlds());
        for (int i = 0; i < 1000; i++) {
            locations.add(new Location(null, number(), number(-64, 327), number()));
        }
    }

    @Benchmark
    public void bukkitHashCode(Blackhole blackhole) {
        for (Location location : locations) {
            blackhole.consume(location.hashCode());
        }
    }

    @Benchmark
    public void kiwiHashCode(Blackhole hole) {
        for (Location location : locations) {
            hole.consume(FastLocHash.posHash(location));
        }
    }

    @TearDown
    public void unMock() {
        MockBukkit.unmock();
    }
}
