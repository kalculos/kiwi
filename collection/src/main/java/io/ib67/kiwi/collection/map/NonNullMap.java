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

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

/**
 * a Map whose values are not null.
 * @param <K>
 * @param <V>
 */
@ApiStatus.AvailableSince("0.5.0")
public interface NonNullMap<K, V> extends Map<K, V> {
    static <K, V> NonNullMap<K, V> newMutableMap() {
        return new NonNullHashMap<>();
    }

    static <K, V> NonNullMap<K, V> newMutableMap(int capacity, float loadFactor) {
        return new NonNullHashMap<>(capacity, loadFactor);
    }

    static <K, V> NonNullMap<K, V> newMutableMap(Map<K, V> toCopy) {
        return new NonNullHashMap<>(toCopy);
    }
}
