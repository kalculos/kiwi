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

package kiwi.collection;

import be.seeseemelk.mockbukkit.MockBukkit;
import io.ib67.kiwi.collection.bukkit.FastLoc2ObjMap;
import org.bukkit.Location;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static io.ib67.kiwi.RandomHelper.number;
import static java.util.Objects.requireNonNull;
import static org.bukkit.Bukkit.getWorlds;

@BenchmarkMode(Mode.Throughput)
@State(Scope.Benchmark)
public class Loc2ObjMaps {
    private static final List<Location> locations = new ArrayList<>(1000);
    private static final String PAYLOAD = "sbnc";

    private final Map<Location, String> commonHashMap = new HashMap<>();
    private final Map<Location, String> _prefill_commonHashMap = new HashMap<>();
    private final Map<Location, String> kiwiMap = new FastLoc2ObjMap<>();
    private final Map<Location, String> _prefill_kiwiMap = new FastLoc2ObjMap<>();

    @Setup
    public void setupLocations() {
        MockBukkit.getOrCreateMock();
        MockBukkit.createMockPlugin();
        var a = MockBukkit.getMock().addSimpleWorld("testA");
        var b = MockBukkit.getMock().addSimpleWorld("testB");
        requireNonNull(getWorlds());
        for (int i = 0; i < 1000; i++) {
            locations.add(new Location(number(0, 10) >= 5 ? a : b, number(), number(-64, 327), number()));
        }
        //prefill
        for (final Location location : locations) {
            _prefill_commonHashMap.put(location, PAYLOAD);
        }
        for (final Location location : locations) {
            _prefill_kiwiMap.put(location, PAYLOAD);
        }
    }

    @Benchmark
    public void commonHashMapWrite() {
        for (final Location location : locations) {
            commonHashMap.put(location, PAYLOAD);
        }
    }

    @Benchmark
    public void kiwiHashMapWrite() {
        for (final Location location : locations) {
            kiwiMap.put(location, PAYLOAD);
        }
    }

    @Benchmark
    public void commonHashMapRead(Blackhole blackhole) {
        for (final Location location : locations) {
            blackhole.consume(_prefill_commonHashMap.get(location));
        }
    }

    @Benchmark
    public void kiwiHashMapRead(Blackhole blackhole) {
        for (final Location location : locations) {
            blackhole.consume(_prefill_kiwiMap.get(location));
        }
    }

    @TearDown
    public void unMock() {
        MockBukkit.unmock();
    }
}
