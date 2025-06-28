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
import io.ib67.kiwi.event.api.EventListenerHost;
import io.ib67.kiwi.event.api.annotation.SubscribeEvent;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.objectweb.asm.Opcodes;

import java.lang.invoke.MethodHandles;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TestListenerResolver implements Opcodes {
    @Test
    void testAsmResolver() throws Exception {
        MethodHandles.Lookup lookup = MethodHandles.lookup();
        var called = new AtomicBoolean(false);
        EventListenerHost host = new TestListener(called);
        var handler = new AsmListenerResolver(lookup, host).resolveHandlers().getFirst().handler();
        assertNotNull(handler);
        handler.handle(null);
        assertTrue(called.get());
    }

    @Test
    void testMhResolver() throws Exception {
        MethodHandles.Lookup lookup = MethodHandles.lookup();
        var called = new AtomicBoolean(false);
        EventListenerHost host = new TestListener(called);
        var handler = new ReflectionListenerResolver(lookup, host).resolveHandlers().getFirst().handler();
        assertNotNull(handler);
        handler.handle(null);
        assertTrue(called.get());
    }

    static class TestEvent implements Event {
        @Override
        public TypeToken<?> type() {
            return null;
        }
    }

    @RequiredArgsConstructor
    static class TestListener implements EventListenerHost {
        private final AtomicBoolean called;
        @SubscribeEvent
        public void onTest(TestEvent event) {
            called.set(true);
        }
    }
}