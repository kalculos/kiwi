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

package org.inlambda.kiwi;

import lombok.experimental.UtilityClass;
import org.inlambda.kiwi.lazy.LazyFunction;
import org.inlambda.kiwi.lazy.LazySupplier;

import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

@UtilityClass
public class Kiwi {
    public static <T> Optional<T> runAny(AnySupplier<T> supplier){
        try{
            var result = supplier.get();
            return Optional.ofNullable(result);
        } catch (Throwable e) {
            return Optional.empty();
        }
    }
    public static <V> LazySupplier<V> byLazy(Supplier<V> supplier){
        return LazySupplier.by(supplier);
    }

    public static <K,V> LazyFunction<K,V> byLazy(Function<K,V> function){
        return LazyFunction.by(function);
    }

}
