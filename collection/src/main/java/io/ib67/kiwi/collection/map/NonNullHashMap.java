/*
 * MIT License
 *
 * Copyright (c) 2024 InlinedLambdas and Contributors
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

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

import static java.util.Objects.requireNonNull;

class NonNullHashMap<K, V> implements NonNullMap<K, V> {
    private final Map<K, V> underlyingMap;

    NonNullHashMap() {
        this.underlyingMap = new HashMap<>();
    }

    NonNullHashMap(int capacity,float loadFactor) {
        this.underlyingMap = new HashMap<>(capacity,loadFactor);
    }

    NonNullHashMap(Map<K, V> backingMap) {
        underlyingMap = new HashMap<>(backingMap.size());
        for (Entry<K, V> kvEntry : backingMap.entrySet()) {
            put(kvEntry.getKey(), kvEntry.getValue());
        }
    }

    @Override
    public int size() {
        return underlyingMap.size();
    }

    @Override
    public boolean isEmpty() {
        return underlyingMap.isEmpty();
    }

    @Override
    public boolean containsKey(Object key) {
        return underlyingMap.containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
        if (value == null) return false;
        return underlyingMap.containsValue(value);
    }

    @Override
    public V get(Object key) {
        return underlyingMap.get(key);
    }

    @Nullable
    @Override
    public V put(K key, V value) {
        requireNonNull(value, "value cannot be null");
        return underlyingMap.put(key, value);
    }

    @Override
    public V remove(Object key) {
        return underlyingMap.remove(key);
    }

    @Override
    public void putAll(@NotNull Map<? extends K, ? extends V> m) {
        for (Entry<? extends K, ? extends V> entry : m.entrySet()) {
            put(entry.getKey(), entry.getValue());
        }
    }

    @Override
    public void clear() {
        underlyingMap.clear();
    }

    @NotNull
    @Override
    public Set<K> keySet() {
        return underlyingMap.keySet();
    }

    @NotNull
    @Override
    public Collection<V> values() {
        return underlyingMap.values();
    }

    @NotNull
    @Override
    public Set<Entry<K, V>> entrySet() {
        return underlyingMap.entrySet();
    }
}
