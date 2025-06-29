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

public class SimpleEventBus implements EventBus {
    protected final SortedSet<EventHandler<?>> handlers;
    public SimpleEventBus(int initialCapacity) {
        this.handlers = new SortedArraySet<>(initialCapacity, Comparator.comparingInt(EventHandler::priority));
    }

    @Override
    public boolean post(Event event) {
        try{
            for (EventHandler handler : handlers) {
                handler.handle(event);
            }
            return true;
        } catch (Interruption e) {
            return false;
        }
    }

    @Override
    public <E extends Event> void register(TypeToken<E> type, EventHandler<E> handler) {
        handlers.add(handler);
    }
}
