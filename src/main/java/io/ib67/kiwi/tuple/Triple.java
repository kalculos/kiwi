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

package io.ib67.kiwi.tuple;

import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Contract;

import java.util.List;
import java.util.function.Predicate;

/**
 * A triple of three different elements, immutable.
 * @param <A> the type of the first element
 * @param <B> the type of the second element
 * @param <C> the type of the third element
 */
@ApiStatus.AvailableSince("0.1.0")
@RequiredArgsConstructor
@EqualsAndHashCode(cacheStrategy = EqualsAndHashCode.CacheStrategy.LAZY)
public final class Triple<A,B,C> {
    public final A a;
    public final B b;
    public final C c;

    @Contract(" -> new")
    public Triple<C,B,A> asReversed(){
        return new Triple<>(c, b, a);
    }

    @SuppressWarnings("unchecked")
    public <T> boolean anyMatch(Predicate<T> predicate){
        return predicate.test((T) a) || predicate.test((T) b) || predicate.test((T) c);
    }

    @SuppressWarnings("unchecked")
    public <T> List<T> asImmutableList(Class<T> tClass){
        return (List<T>) List.of(a,b,c);
    }

    public boolean norNull(){
        return a != null && b != null && c != null;
    }
}
