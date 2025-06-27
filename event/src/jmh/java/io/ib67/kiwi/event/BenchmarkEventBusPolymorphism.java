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
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

import java.util.concurrent.TimeUnit;

@BenchmarkMode(Mode.AverageTime)
@State(Scope.Benchmark)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
public class BenchmarkEventBusPolymorphism {
    @Param({"100"})
    public int numHandlers;
    private EventBus eventBus;
    private EventBus singleEventBus;
    private TestEventA eventA;
    private TestEventB eventB;
    private TestEventC eventC;

    @Setup
    public void setup() {
        eventBus = new HierarchyEventBus();
        singleEventBus = new HierarchyEventBus();
        eventA = new TestEventA();
        eventB = new TestEventB();
        eventC = new TestEventC();
        for (int i = 0; i < numHandlers ; i++) {
            singleEventBus.register(eventC.type(), this::handleEventC);
            eventBus.register(eventC.type(), this::handleEventC);
        }
        for (int i = 0; i < (numHandlers / 2); i++) {
            eventBus.register(eventA.type(), this::handleEventA);
            eventBus.register(eventB.type(), this::handleEventB);
        }
    }

    @Benchmark
    @CompilerControl(CompilerControl.Mode.DONT_INLINE)
    public void postEventHierarchy() {
        eventBus.post(eventB);
    }

    @Benchmark
    @CompilerControl(CompilerControl.Mode.DONT_INLINE)
    public void postEventSingle() {
        eventBus.post(eventC);
    }

    @Benchmark
    @CompilerControl(CompilerControl.Mode.DONT_INLINE)
    public void postEventSingleBus() {
        singleEventBus.post(eventC);
    }

    void handleEventA(TestEventA event) {
        Blackhole.consumeCPU(10);
    }

    void handleEventB(TestEventB event) {
        Blackhole.consumeCPU(10);
    }

    void handleEventC(TestEventC event) {
        Blackhole.consumeCPU(10);
    }


    class TestEventA implements Event {
        static final TypeToken<TestEventA> TYPE = TypeToken.resolve(TestEventA.class);

        @Override
        public TypeToken<? extends TestEventA> type() {
            return TYPE;
        }
    }

    class TestEventB extends TestEventA {
        static final TypeToken<TestEventB> TYPE = TypeToken.resolve(TestEventB.class);

        @Override
        public TypeToken<? extends TestEventB> type() {
            return TYPE;
        }
    }

    record TestEventC() implements Event {
        static final TypeToken<TestEventC> TYPE = TypeToken.resolve(TestEventC.class);

        @Override
        public TypeToken<TestEventC> type() {
            return TYPE;
        }
    }
}
