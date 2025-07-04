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
import io.ib67.kiwi.event.api.EventListenerHost;
import io.ib67.kiwi.event.api.annotation.SubscribeEvent;
import io.ib67.kiwi.event.util.AsmListenerResolver;
import io.ib67.kiwi.event.util.EventTuple;
import io.ib67.kiwi.event.util.ReflectionListenerResolver;
import lombok.SneakyThrows;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

import java.lang.invoke.MethodHandles;
import java.util.concurrent.TimeUnit;

@BenchmarkMode(Mode.AverageTime)
@State(Scope.Benchmark)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
public class BenchmarkListenerResolvers {
    record TestEventA() implements Event {
        public static final TypeToken<TestEventA> TYPE = TypeToken.resolve(TestEventA.class);

        @Override
        public TypeToken<TestEventA> type() {
            return TYPE;
        }
    }

    @Param({"100"})
    public int numHandlers;

    private TestEventA event;
    private EventBus busSimple;
    private EventBus busRuntimeGen;
    private EventBus busMethodHandle;

    @Setup
    @SneakyThrows
    public void setup() {
        event = new TestEventA();
        busSimple = new SimpleEventBus(numHandlers);
        busRuntimeGen = new SimpleEventBus(numHandlers);
        busMethodHandle = new SimpleEventBus(numHandlers);
        for (int i = 0; i < numHandlers; i++) {
            busSimple.register(event.type(), this::handler);
        }

        var lookup = MethodHandles.privateLookupIn(SimpleListener.class, MethodHandles.lookup());
        var listenerHost = new SimpleListener();
        for (int i = 0; i < numHandlers; i++) {
            /*
             * Duplicate multiple handlers to avoid inlining. Especially for codegen resolver
             */
            var handlersGen = new AsmListenerResolver(lookup, listenerHost).resolveHandlers();
            var handlersMH = new ReflectionListenerResolver(lookup, listenerHost).resolveHandlers();
            ((EventHandler<TestEventA>) handlersGen.getFirst().handler()).handle(event);
            ((EventHandler<TestEventA>) handlersMH.getFirst().handler()).handle(event); // preload method handle
            for (var _entry : handlersGen) {
                var entry = (EventTuple<Event>) _entry;
                busRuntimeGen.register(entry.type(), entry.handler());
            }
            for (var _entry : handlersMH) {
                var entry = (EventTuple<Event>) _entry;
                busMethodHandle.register(entry.type(), entry.handler());
            }
        }
    }

    void handler(TestEventA event) {
        Blackhole.consumeCPU(10);
    }

    @Benchmark
    @CompilerControl(CompilerControl.Mode.DONT_INLINE)
    public void deliverEventBusMethodReference() {
        busSimple.post(event);
    }

    @Benchmark
    @CompilerControl(CompilerControl.Mode.DONT_INLINE)
    public void deliverEventCodeGen() {
        busRuntimeGen.post(event);
    }

    @Benchmark
    @CompilerControl(CompilerControl.Mode.DONT_INLINE)
    public void deliverEventMethodHandle() {
        busMethodHandle.post(event);
    }

    static class SimpleListener implements EventListenerHost {
        @SubscribeEvent
        public void onEvent(TestEventA event) {
            Blackhole.consumeCPU(10);
        }
    }
}
