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

package io.ib67.kiwi.collection.bukkit;

import io.ib67.kiwi.bukkit.FastLocHash;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

public final class FastLoc2ObjMap<T> implements Map<Location, T>, Function<Location, T> {
    private final Long2ObjectMap<T> map = new Long2ObjectOpenHashMap<>();

    @Override
    public int size() {
        return map.size();
    }

    @Override
    public boolean isEmpty() {
        return map.isEmpty();
    }

    @Override
    public boolean containsKey(Object key) {
        return map.containsKey(FastLocHash.posHash((Location) key));
    }

    @Override
    public boolean containsValue(Object value) {
        return map.containsValue(value);
    }

    @Override
    @Deprecated
    public T get(Object key) {
        if (key instanceof Location) {
            return get((Location) key);
            // return map.get(FastLocHash.posHash((Location) key));
        }
        return null;
    }

    public T get(Location key) {
        return map.get(FastLocHash.posHash(key));
    }

    @Nullable
    @Override
    public T put(Location key, T value) {
//        locations.add(key);
        return map.put(FastLocHash.posHash(key), value);
    }

    @Override
    @Deprecated
    public T remove(Object key) {
        if (key instanceof Location) {
            return remove(key);
        }
        return null;
    }

    public T remove(Location key) {
        return map.remove(FastLocHash.posHash(key));
    }

    @Override
    public void putAll(@NotNull Map<? extends Location, ? extends T> m) {
        for (Map.Entry<? extends Location, ? extends T> entry : m.entrySet()) {
            put(entry.getKey(), entry.getValue());
        }
    }

    @Override
    public void clear() {
        map.clear();
    }

    @NotNull
    @Override
    public Set<Location> keySet() {
        throw new UnsupportedOperationException();
    }


    @NotNull
    @Override
    public Collection<T> values() {
        return map.values();
    }

    @NotNull
    @Override
    public Set<Entry<Location, T>> entrySet() {
        throw new UnsupportedOperationException();
    }

    @Override
    public T apply(Location location) {
        return get(location);
    }
}
