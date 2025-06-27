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

class TestHierarchyEventBus {
    private HierarchyEventBus eventBus;

    interface BaseEvent extends Event {
    }

    static class ParentEvent implements BaseEvent {
        private final TypeToken<ParentEvent> type = TypeToken.resolve(ParentEvent.class);
        boolean parentHandled = false;
        boolean baseHandled = false;

        @Override
        public TypeToken<?> type() {
            return type;
        }
    }

    static class ChildEvent extends ParentEvent {
        private final TypeToken<ChildEvent> type = TypeToken.resolve(ChildEvent.class);
        boolean childHandled = false;

        @Override
        public TypeToken<?> type() {
            return type;
        }
    }

    @BeforeEach
    void setUp() {
        eventBus = new HierarchyEventBus();
    }

    @Test
    void testEventHierarchy() {
        TypeToken<BaseEvent> baseType = TypeToken.resolve(BaseEvent.class);
        TypeToken<ParentEvent> parentType = TypeToken.resolve(ParentEvent.class);
        TypeToken<ChildEvent> childType = TypeToken.resolve(ChildEvent.class);

        eventBus.register(baseType, (EventHandler<BaseEvent>) event -> {
            if (event instanceof ParentEvent parentEvent) {
                parentEvent.baseHandled = true;
            }
        });

        eventBus.register(parentType, (EventHandler<ParentEvent>) event ->
                event.parentHandled = true);

        eventBus.register(childType, (EventHandler<ChildEvent>) event ->
                event.childHandled = true);

        ChildEvent childEvent = new ChildEvent();
        assertTrue(eventBus.post(childEvent));

        assertTrue(childEvent.childHandled);
        assertTrue(childEvent.parentHandled);
        assertTrue(childEvent.baseHandled);
    }

    @Test
    void testInterruptionPropagation() {
        TypeToken<ParentEvent> parentType = TypeToken.resolve(ParentEvent.class);
        TypeToken<ChildEvent> childType = TypeToken.resolve(ChildEvent.class);
        boolean[] parentHandlerCalled = {false};
        eventBus.register(parentType, event -> {
            parentHandlerCalled[0] = true;
        });

        eventBus.register(childType, event ->
        {
            throw Interruption.INTERRUPTION;
        });

        ChildEvent childEvent = new ChildEvent();
        assertFalse(eventBus.post(childEvent));
        assertFalse(parentHandlerCalled[0]);
    }

    @Test
    void testHandlerPriority() {
        TypeToken<ParentEvent> parentType = TypeToken.resolve(ParentEvent.class);
        StringBuilder order = new StringBuilder();

        EventHandler<ParentEvent> handler1 = new EventHandler<>() {
            @Override
            public void handle(ParentEvent event) {
                order.append("1");
            }

            @Override
            public int priority() {
                return 1;
            }
        };

        EventHandler<ParentEvent> handler2 = new EventHandler<>() {
            @Override
            public void handle(ParentEvent event) {
                order.append("2");
            }

            @Override
            public int priority() {
                return 2;
            }
        };

        eventBus.register(parentType, handler1);
        eventBus.register(parentType, handler2);

        ParentEvent event = new ParentEvent();
        assertTrue(eventBus.post(event));
        assertEquals("12", order.toString());
    }

    @Test
    void testMultipleHandlersInHierarchy() {
        TypeToken<BaseEvent> baseType = TypeToken.resolve(BaseEvent.class);
        TypeToken<ParentEvent> parentType = TypeToken.resolve(ParentEvent.class);

        StringBuilder order = new StringBuilder();

        eventBus.register(baseType, (EventHandler<BaseEvent>) event ->
                order.append("base"));
        eventBus.register(parentType, (EventHandler<ParentEvent>) event ->
                order.append("parent"));

        ParentEvent event = new ParentEvent();
        assertTrue(eventBus.post(event));
        assertEquals("parentbase", order.toString());
    }
}