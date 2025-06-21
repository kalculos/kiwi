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

import java.util.HashMap;
import java.util.Map;

public class HierarchyEventBus implements EventBus {
    protected record ChainedBus(EventBus bus, ChainedBus parent) {
    }

    protected final Map<TypeToken<?>, ChainedBus> busses = new HashMap<>();

    public HierarchyEventBus(){
        var eventType = TypeToken.resolve(Event.class);
        busses.put(eventType, new ChainedBus(createBus(eventType), null));
    }

    @Override
    public boolean post(Event event) {
        var chain = busses.get(event.type());
        while (chain != null) {
            if (!chain.bus.post(event)) {
                return false;
            }
            chain = chain.parent;
        }
        return true;
    }

    protected SingleTypedEventBus createBus(TypeToken<?> type) {
        return new SingleTypedEventBus(type, 4);
    }

    protected ChainedBus locateBusOrCreate(TypeToken<?> type) {
        return busses.computeIfAbsent(type, t -> new ChainedBus(createBus(t), locateBusOrCreate(t.resolveDirectParent())));
    }

    @Override
    public <E extends Event> void register(TypeToken<E> type, EventHandler<E> handler) {
        var bus = locateBusOrCreate(type);
        bus.bus().register(type, handler);
    }
}