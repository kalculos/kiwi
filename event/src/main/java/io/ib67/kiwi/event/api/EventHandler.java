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

import io.ib67.kiwi.routine.Interruption;

/**
 * Handler of events.
 * @param <E> type of event
 */
public interface EventHandler<E extends Event> {
    /**
     * This method will be called everytime when a new event is posted into the associating {@link EventBus}
     * @param event the new event
     * @throws Interruption to interrupt the event delivery. Subsequent handlers will not be aware of the event.
     */
    void handle(E event) throws Interruption;

    /**
     * The priority of this handler. Lower is earlier.
     * @return priority. should be constant once registered
     */
    default int priority() {
        return 0;
    }
}
