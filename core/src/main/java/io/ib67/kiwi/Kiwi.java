/*
 * MIT License
 *
 * Copyright (c) 2023 InlinedLambdas and Contributors
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

package io.ib67.kiwi;

import io.ib67.kiwi.exception.AnyRunnable;
import io.ib67.kiwi.exception.AnySupplier;
import io.ib67.kiwi.future.Promise;
import io.ib67.kiwi.future.Result;
import io.ib67.kiwi.future.TaskPromise;
import io.ib67.kiwi.lazy.LazyFunction;
import io.ib67.kiwi.lazy.LazySupplier;
import io.ib67.kiwi.lock.CloseableLock;
import io.ib67.kiwi.lock.CloseableTryLock;
import io.ib67.kiwi.reflection.AccessibleClass;
import io.ib67.kiwi.tuple.Pair;
import io.ib67.kiwi.tuple.Triple;
import lombok.experimental.UtilityClass;
import org.jetbrains.annotations.ApiStatus;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

@ApiStatus.AvailableSince("0.1.0")
@UtilityClass
public class Kiwi {
    public static <T> Result<T, ? extends Throwable> fromAny(AnySupplier<T> supplier) {
        try {
            var result = supplier.get();
            return Result.ok(result);
        } catch (Throwable e) {
            return Result.fail(e);
        }
    }

    public static Result<?, ? extends Throwable> runAny(AnyRunnable runnable) {
        try {
            runnable.run();
            return Result.ok(null);
        } catch (Throwable e) {
            return Result.fail(e);
        }
    }

    public static <V> LazySupplier<V> byLazy(Supplier<V> supplier) {
        return LazySupplier.by(supplier);
    }

    public static <K, V> LazyFunction<K, V> byLazy(Function<K, V> function) {
        return LazyFunction.by(function);
    }

    public static <T> T todo(String msg) {
        throw new UnsupportedOperationException("TODO: " + msg);
    }

    public static <T> T todo() {
        throw new UnsupportedOperationException("TODO");
    }

    public static <K, V> Pair<K, V> pairOf(K k, V v) {
        return new Pair<>(k, v);
    }

    public static <A, B, C> Triple<A, B, C> tripleOf(A a, B b, C c) {
        return new Triple<>(a, b, c);
    }

    public static <T> AccessibleClass<T> accessClass(Class<T> clazz) {
        return AccessibleClass.of(clazz);
    }

    public static CloseableLock withLock(Lock lock) {
        return new CloseableLock(lock);
    }

    public static CloseableTryLock withTryLock(Lock lock, long time, TimeUnit unit) throws InterruptedException {
        return new CloseableTryLock(lock, time, unit);
    }

    public static <T> T deAsync(Consumer<Promise<T, ?>> asyncCodes) throws InterruptedException {
        var promise = new TaskPromise<T, Object>();
        asyncCodes.accept(promise);
        return promise.sync().orElseThrow();
    }

    @SuppressWarnings("unchecked")
    public static <T, R> T typeMagic(R r) { // useful when dealing with parameterized types.
        return (T) r;
    }
}
