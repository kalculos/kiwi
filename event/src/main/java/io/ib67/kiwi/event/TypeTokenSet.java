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

package io.ib67.kiwi.event;

import io.ib67.kiwi.TypeToken;

class TypeTokenSet {
    private static final float LOAD_FACTOR = 0.75f;

    private TypeToken[] keys;
    private boolean[] values;
    private boolean[] used;
    private int size;
    private int threshold;

    private static final long EMPTY = 0L;

    public TypeTokenSet(int initialCapacity) {
        keys = new TypeToken[initialCapacity];
        values = new boolean[initialCapacity];
        used = new boolean[initialCapacity];
        threshold = (int) (initialCapacity * LOAD_FACTOR);
    }

    private int index(long key) {
        return Long.hashCode(key) & (keys.length - 1);
    }

    public boolean get(TypeToken<?> key) {
        var hash = key.longHash();
        int idx = index(hash);
        while (used[idx]) {
            if (keys[idx].longHash() == key.longHash() && keys[idx].equals(key)) {
                return values[idx];
            }
            idx = (idx + 1) & (keys.length - 1);
        }
        return false;
    }

    public boolean containsKey(TypeToken<?> key) {
        var hash = key.longHash();
        int idx = index(hash);
        while (used[idx]) {
            if (keys[idx].longHash() == key.longHash() && keys[idx].equals(key)) return true;
            idx = (idx + 1) & (keys.length - 1);
        }
        return false;
    }

    public void put(TypeToken<?> key, boolean value) {
        var hash = key.longHash();
        if (size >= threshold) resize();
        int idx = index(hash);
        while (used[idx]) {
            if (keys[idx].longHash() == key.longHash() && keys[idx].equals(key)) {
                values[idx] = value;
                return;
            }
            idx = (idx + 1) & (keys.length - 1);
        }
        keys[idx] = key;
        values[idx] = value;
        used[idx] = true;
        size++;
    }

    private void resize() {
        TypeToken[] oldKeys = keys;
        boolean[] oldValues = values;
        boolean[] oldUsed = used;
        int newCapacity = keys.length * 2;
        keys = new TypeToken[newCapacity];
        values = new boolean[newCapacity];
        used = new boolean[newCapacity];
        threshold = (int) (newCapacity * LOAD_FACTOR);
        size = 0;
        for (int i = 0; i < oldKeys.length; i++) {
            if (oldUsed[i]) {
                put(oldKeys[i], oldValues[i]);
            }
        }
    }

    public int size() {
        return size;
    }
}