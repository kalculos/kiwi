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

import io.ib67.kiwi.TypeToken;
import io.ib67.kiwi.routine.Uni;

/**
 * It delivers an {@link Event} to all {@link EventHandler} that is {@link #register(TypeToken)}ed in this object.
 */
public interface EventBus {
    /**
     * Delivers an Event to all related subscribers.
     *
     * @param event event to be posted
     * @return false if any handlers cancelled the event
     */
    boolean post(Event event);

    /**
     * Registers a {@link EventHandler} to receive events matching the typetoken.
     * @param type
     * @param handler
     * @param <E>
     */
    <E extends Event> void register(TypeToken<E> type, EventHandler<E> handler);

    default <E extends Event> void register(Class<E> type, EventHandler<E> handler) {
        register(TypeToken.resolve(type), handler);
    }

    default <E extends Event> Uni<E> register(TypeToken<E> type) {
        return c -> register(type, c::onValue);
    }
}
