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

package io.ib67.kiwi.event.api;

import io.ib67.kiwi.event.util.AsmListenerResolver;
import io.ib67.kiwi.event.util.EventTuple;
import lombok.SneakyThrows;

import java.lang.invoke.MethodHandles;

public interface EventListenerHost {
    @SneakyThrows
    default void registerTo(EventBus bus) {
        var lookup = MethodHandles.privateLookupIn(this.getClass(), MethodHandles.lookup());
        var handlers = new AsmListenerResolver(lookup, this).resolveHandlers();
        handlers.forEach(t -> {
            var _t = (EventTuple<Event>) t;
            bus.register(_t.type(), _t.handler());
        });
    }
}
