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

package io.ib67.kiwi.event.util;

import io.ib67.kiwi.TypeToken;
import io.ib67.kiwi.event.api.Event;
import io.ib67.kiwi.event.api.EventHandler;
import io.ib67.kiwi.event.api.EventListenerHost;
import io.ib67.kiwi.event.api.annotation.SubscribeEvent;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * An utilities that generates MethodHandle handlers for each subscriber methof from subclasses of {@link EventListenerHost}
 */
public class ReflectionListenerResolver {
    protected final EventListenerHost host;
    protected final MethodHandles.Lookup lookup;

    public ReflectionListenerResolver(
            MethodHandles.Lookup lookup,
            EventListenerHost host
    ) {
        this.host = host;
        this.lookup = lookup;
    }

    @SneakyThrows
    public List<EventTuple<?>> resolveHandlers() {
        var hostType = TypeToken.resolve(host.getClass());
        var result = new ArrayList<EventTuple<?>>();
        var methods = host.getClass().getDeclaredMethods();
        for (Method method : methods) {
            if (!method.isAnnotationPresent(SubscribeEvent.class)) {
                continue;
            }
            if (method.getReturnType() == void.class
                    && method.getParameterCount() == 1
                    && Event.class.isAssignableFrom(method.getParameterTypes()[0])) {
                result.add(new EventTuple(hostType.resolveType(method.getGenericParameterTypes()[0]), createEventHandler(method)));
            }
        }
        return result;
    }

    @SneakyThrows
    protected EventHandler<?> createEventHandler(Method method) {
        return new MHEventHandler(lookup.unreflect(method).bindTo(host));
    }

    @RequiredArgsConstructor
    static final class MHEventHandler<E extends Event> implements EventHandler<E> {
        private final MethodHandle handle;

        @Override
        @SneakyThrows
        public void handle(Event event) {
            handle.invoke(event); // the method handle is bound.
        }
    }
}
