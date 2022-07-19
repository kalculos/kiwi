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

package io.ib67.kiwi.lazy;

import org.jetbrains.annotations.ApiStatus;

import java.util.function.Function;

/**
 * A thread-safe function that caches value.
 *
 * @param <K>
 * @param <V>
 */
@ApiStatus.AvailableSince("0.1.0")
public final class LazyFunction<K,V> implements Function<K,V> {
    private final Function<K,V> function;
    private volatile V value;

    private LazyFunction(Function<K, V> function, V value) {
        this.function = function;
        this.value = value;
    }

    public static <K,V> LazyFunction<K,V> by(Function<K,V> function){
        return new LazyFunction<>(function,null);
    }

    @Override
    public V apply(K k) {
        if(value == null){
            synchronized (this){
                if(value==null){
                    value = function.apply(k);
                }
            }
        }
        return value;
    }
}
