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

import java.lang.reflect.Type;
import java.util.Deque;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class HierarchyEventBus implements EventBus {
    protected record ChainedBus(EventBus bus, ChainedBus parent) {
    }

    protected final Map<TypeToken<?>, ChainedBus> busses = new HashMap<>();
    protected final Lock readLock;
    protected final Lock writeLock;

    public HierarchyEventBus() {
        var eventType = TypeToken.resolve(Event.class);
        var lock = new ReentrantReadWriteLock();
        readLock = lock.readLock();
        writeLock = lock.writeLock();

        busses.put(eventType, new ChainedBus(createBus(eventType), null));
    }

    @Override
    public boolean post(Event event) {
        readLock.lock();
        try {
            var chain = busses.get(event.type());
            while (chain != null) {
                if (!chain.bus.post(event)) {
                    return false;
                }
                chain = chain.parent;
            }
            return true;
        }finally {
            readLock.unlock();
        }
    }

    SimpleEventBus createBus(TypeToken<?> type) {
        return new SimpleEventBus(4);
    }

    protected ChainedBus locateBusOrCreate(Deque<Type> deque, TypeToken<?> typeToken) {
        var chain = busses.get(typeToken);
        if (chain != null) {
            return chain;
        }
        chain = new ChainedBus(createBus(typeToken), locateBusOrCreate(deque, TypeToken.resolve(deque.pop())));
        busses.put(typeToken, chain);
        return chain;
    }

    @Override
    public <E extends Event> void register(TypeToken<E> type, EventHandler<E> handler) {
        writeLock.lock();
        try{
            var bus = locateBusOrCreate(type.pathToSuper(true, Event.class), type);
            bus.bus().register(type, handler);
        }finally {
            writeLock.unlock();
        }
    }
}