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
import io.ib67.kiwi.event.util.SortedArraySet;
import io.ib67.kiwi.routine.Interruption;

import java.util.Comparator;
import java.util.SortedSet;

import static java.util.Objects.requireNonNull;

public class SingleTypedEventBus implements EventBus {
    protected final TypeToken<?> type;
    protected final SortedSet<EventHandler> handlers;

    public SingleTypedEventBus(TypeToken<?> type, int initialCapacity) {
        this.type = requireNonNull(type);
        this.handlers = new SortedArraySet<>(initialCapacity, Comparator.comparingInt(EventHandler::priority));
    }

    @Override
    public boolean post(Event event) {
        if (!type.equals(event.type())) {
            throw new IllegalArgumentException("Event type mismatch");
        }
        try {
            for (var handler : handlers) {
                handler.handle(event);
            }
            return true;
        } catch (Interruption ignored) {
            return false;
        }
    }

    @Override
    public <E extends Event> void register(TypeToken<E> type, EventHandler<E> handler) {
        if (!this.type.equals(type)) {
            throw new IllegalArgumentException("Event type mismatch");
        }
        handlers.add(handler);
    }
}
