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
import io.ib67.kiwi.event.api.EventHandler;
import io.ib67.kiwi.routine.Interruption;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TestSingleTypedEventBus {
    private SimpleEventBus eventBus;
    private TypeToken<TestEvent> eventType;

    static class TestEvent implements Event {
        private final TypeToken<TestEvent> type = TypeToken.resolve(TestEvent.class);
        boolean handled = false;

        @Override
        public TypeToken<?> type() {
            return type;
        }
    }

    static class TestInterruptingEvent implements Event {
        private final TypeToken<TestInterruptingEvent> type = TypeToken.resolve(TestInterruptingEvent.class);

        @Override
        public TypeToken<?> type() {
            return type;
        }
    }

    @BeforeEach
    void setUp() {
        eventType = TypeToken.resolve(TestEvent.class);
        eventBus = new SimpleEventBus(4);
    }

    @Test
    void testBasicEventHandling() {
        TestEvent event = new TestEvent();
        eventBus.register(eventType, e -> e.handled = true);

        assertTrue(eventBus.post(event));
        assertTrue(event.handled);
    }

    @Test
    void testHandlerPriority() {
        StringBuilder order = new StringBuilder();

        EventHandler<TestEvent> handler1 = new EventHandler<>() {
            @Override
            public void handle(TestEvent event) {
                order.append("2");
            }

            @Override
            public int priority() {
                return 1;
            }
        };

        EventHandler<TestEvent> handler2 = new EventHandler<>() {
            @Override
            public void handle(TestEvent event) {
                order.append("1");
            }

            @Override
            public int priority() {
                return 2;
            }
        };

        eventBus.register(eventType, handler2);
        eventBus.register(eventType, handler1);

        TestEvent event = new TestEvent();
        eventBus.post(event);

        assertEquals("21", order.toString());
    }

    @Test
    void testEventInterruption() {
        EventHandler<TestEvent> interruptingHandler = new EventHandler<>() {
            @Override
            public void handle(TestEvent event) throws Interruption {
                throw Interruption.INTERRUPTION;
            }

            @Override
            public int priority() {
                return 1;
            }
        };

        boolean[] secondHandlerCalled = {false};
        EventHandler<TestEvent> secondHandler = new EventHandler<>() {
            @Override
            public void handle(TestEvent event) {
                secondHandlerCalled[0] = true;
            }

            @Override
            public int priority() {
                return 2;
            }
        };

        eventBus.register(eventType, interruptingHandler);
        eventBus.register(eventType, secondHandler);

        TestEvent event = new TestEvent();
        assertFalse(eventBus.post(event));
        assertFalse(secondHandlerCalled[0]);
    }
}