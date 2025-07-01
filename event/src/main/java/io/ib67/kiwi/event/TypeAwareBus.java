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
import io.ib67.kiwi.event.api.Event;
import io.ib67.kiwi.event.api.EventBus;
import io.ib67.kiwi.event.api.EventHandler;
import io.ib67.kiwi.event.util.SortedArrayList;
import io.ib67.kiwi.routine.Interruption;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

class TypeAwareBus implements EventBus {
    private static final Predicate<Exception> ALWAYS_TRUE = e -> true;
    protected final List<HandlerEntry> handlers;
    protected final Map<TypeToken<?>, TypeTokenSet> signatureCache;
    protected final Predicate<Exception> exceptionHandler;

    public TypeAwareBus(int initialCapacity) {
        this(initialCapacity, ALWAYS_TRUE);
    }

    public TypeAwareBus(int initialCapacity, Predicate<Exception> exceptionHandler) {
        this.handlers = new SortedArrayList<>(initialCapacity, Comparator.comparingInt(HandlerEntry::priority));
        this.exceptionHandler = exceptionHandler;
        this.signatureCache = new HashMap<>();
    }

    @Override
    public boolean post(Event event) {
        var index = 0;
        while (index < handlers.size()) {
            try {
                for (index = 0; index < handlers.size(); index++) {
                    var eventType = event.type();
                    var handler = handlers.get(index);
                    var cache = handler.singatureCache();
                    var result = cache.get(eventType);
                    if (result == null) { // use null to represent value not present.
                        result = eventType.assignableTo(handler.type());
                        cache.put(eventType, result);
                    }
                    if (result) handler.handler().handle(event);
                }
            } catch (Interruption ignored) {
                return false;
            } catch (Exception e) {
                if (!exceptionHandler.test(e)) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public <E extends Event> void register(TypeToken<E> type, EventHandler<E> handler) {
        handlers.add(new HandlerEntry<>(handler, type, signatureCache.computeIfAbsent(type, it -> new TypeTokenSet(16))));
    }

    record HandlerEntry<E extends Event>(
            EventHandler<E> handler,
            TypeToken<E> type,
            TypeTokenSet singatureCache
    ) {
        int priority() {
            return handler.priority();
        }
    }
}
