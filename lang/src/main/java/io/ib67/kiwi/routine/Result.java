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

package io.ib67.kiwi.routine;

import io.ib67.kiwi.closure.AnyRunnable;
import io.ib67.kiwi.closure.AnySupplier;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

public sealed interface Result<T> extends Uni<T> permits Fail, Some {
    static <T> Result<T> fromAny(AnySupplier<T> supplier) {
        try {
            return new Some<>(supplier.get());
        } catch (Exception e) {
            return Fail.of(e);
        }
    }

    static <T> Result<T> fromNotNull(AnySupplier<T> supplier) {
        try{
            var r = supplier.get();
            if(r == null) return Fail.none();
            return new Some<>(r);
        }catch (Exception e){
            return Fail.of(e);
        }
    }

    static Result<? extends @Nullable Object> runAny(AnyRunnable runnable) {
        try {
            runnable.run();
            return new Some<>(null);
        } catch (Exception e) {
            return Fail.of(e);
        }
    }

    default T orElse(T defaultValue) {
        return switch (this) {
            case Fail f -> defaultValue;
            case Some(T v) -> v;
        };
    }

    default T orElseGet(Supplier<T> supplier) {
        return switch (this) {
            case Fail f -> supplier.get();
            case Some(T value) -> value;
        };
    }

    default T orElseThrow(Supplier<? extends RuntimeException> supplier) {
        return switch (this) {
            case Fail f -> throw supplier.get();
            case Some(T t) -> t;
        };
    }

    default T orElseThrow() {
        return switch (this) {
            case Fail(RuntimeException e) -> {
                throw e;
            }
            case Fail(Object o) -> {
                throw new IllegalStateException("Failure: " + o);
            }
            case Some(T value) -> value;
        };
    }

    default Result<T> onFail(Consumer<Fail<?>> failConsumer) {
        if (this instanceof Fail) {
            failConsumer.accept((Fail) this);
        }
        return this;
    }

    default Optional<T> toOptional() {
        return Optional.ofNullable(result());
    }

    @Override
    default Result<T> filter(Predicate<? super T> predicate) {
        if(this instanceof Fail) {
            return this;
        }else if(this instanceof Some(T t)){
            if(predicate.test(t)){
                return this;
            }
        }
        return Fail.none();
    }

    @Override
    default <M> Result<M> map(Function<? super T, M> mapper) {
        if(this instanceof Fail f) {
            return f;
        }else if(this instanceof Some(T t)){
            return new Some<>(mapper.apply(t));
        }
        throw new IllegalStateException("Impossible");
    }

    T result();
}
