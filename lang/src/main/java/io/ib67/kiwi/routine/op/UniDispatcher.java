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

package io.ib67.kiwi.routine.op;

import io.ib67.kiwi.routine.Uni;
import org.jetbrains.annotations.ApiStatus;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;

/**
 * An UniOp that allows more conditions.
 * See also: {@link UniOp#dispatch(Predicate, Uni.InterruptibleConsumer)}
 * @param <T> type of uni's element
 */
@ApiStatus.AvailableSince("1.0.0")
public class UniDispatcher<T> {
    private final Map<Predicate<T>, Uni.InterruptibleConsumer<T>> map = new HashMap<>();
    private UniDispatcher() {
    }

    public static <T> UniDispatcher<T> of() {
        return new UniDispatcher<>();
    }

    public UniDispatcher<T> add(Predicate<T> predicate, Uni.InterruptibleConsumer<T> consumer){
        map.put(predicate, consumer);
        return this;
    }

    public UnaryOperator<Uni<T>> build(){
        var finalMap = Map.copyOf(map);
        return u -> c -> u.accept(t->{
            for (var entry : finalMap.entrySet()) {
                if(entry.getKey().test(t)){
                    entry.getValue().onValue(t);
                    return;
                }
            }
            c.onValue(t);
        });
    }
}
