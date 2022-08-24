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

package io.ib67.kiwi.collection.map;

import io.ib67.kiwi.KiwiMap;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@ApiStatus.AvailableSince("0.3.1")
@RequiredArgsConstructor
public class CachedForwardingMap<K, V> implements KiwiMap<K, V> {
    private final Map<K, V> targetMap;
    private final Map<K, V> cache = new HashMap<>();

    @Override
    public int size() {
        return targetMap.size();
    }

    @Override
    public boolean isEmpty() {
        return cache.isEmpty() && targetMap.isEmpty();
    }

    @Override
    public boolean containsKey(Object key) {
        return cache.containsKey(key) || targetMap.containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
        return cache.containsValue(value) || targetMap.containsValue(value);
    }

    @Override
    public V get(Object key) {
        return cache.computeIfAbsent((K) key, targetMap::get);
    }

    @Nullable
    @Override
    public V put(K key, V value) {
        if (!targetMap.containsKey(key)) {
            targetMap.put(key, value);
            cache.put(key, value);
            return null;
        }
        return cache.put(key, value);
    }

    @Override
    public V remove(Object key) {
        targetMap.remove(key);
        return cache.remove(key);
    }

    @Override
    public void putAll(@NotNull Map<? extends K, ? extends V> m) {
        m.forEach(this::put);
    }

    @Override
    public void clear() {
        cache.clear();
        targetMap.clear();
    }

    @NotNull
    @Override
    public Set<K> keySet() {
        flush();
        return targetMap.keySet();
    }

    @NotNull
    @Override
    public Collection<V> values() {
        flush();
        return targetMap.values();
    }

    public void flush() {
        targetMap.putAll(cache);
    }

    @NotNull
    @Override
    public Set<Entry<K, V>> entrySet() {
        flush();
        return targetMap.entrySet();
    }
}
