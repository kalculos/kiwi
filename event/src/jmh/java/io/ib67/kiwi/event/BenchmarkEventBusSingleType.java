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
import io.ib67.kiwi.routine.Interruption;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

import java.util.*;
import java.util.concurrent.TimeUnit;

@BenchmarkMode(Mode.AverageTime)
@State(Scope.Benchmark)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
public class BenchmarkEventBusSingleType {
    record TestEventA() implements Event {
        public static final TypeToken<TestEventA> TYPE = TypeToken.resolve(TestEventA.class);

        @Override
        public TypeToken<TestEventA> type() {
            return TYPE;
        }
    }

    record TestEventB<T extends Event>(TypeToken<T> type) implements Event {
    }

    @Param({"100"})
    public int numHandlers;

    @Param({"0.3", "0.6"})
    public double wrongFactor;

    private List<EventHandler<Event>> arrayHandlers;

    private Event eventToDeliver;
    private Event eventToChaoticDeliver;
    private EventBus busSimple;
    private EventBus busTyped;
    private EventBus busInterruption;
    private TypeAwareBus busTypedChaotic;

    @Setup
    public void setup() {
        var eventUntyped = new TestEventA();
        var eventTyped = new TestEventB<>(TypeToken.getParameterized(TestEventB.class, String.class));
        busSimple = new SimpleEventBus(numHandlers);
        busInterruption = new TypeAwareBus(numHandlers);
        busTyped = new TypeAwareBus(numHandlers);
        busTypedChaotic = new TypeAwareBus(numHandlers);
        arrayHandlers = new ArrayList<>();
        eventToDeliver = eventUntyped;
        for (int i = 0; i < numHandlers; i++) {
            busSimple.register(eventToDeliver.type(), this::handler);
            arrayHandlers.add(this::handler);
            busTyped.register(eventToDeliver.type(), this::handler);
            if (i == numHandlers - 1) {
                busInterruption.register(eventToDeliver.type(), this::handlerInterrupting);
            } else {
                busInterruption.register(eventToDeliver.type(), this::handler);
            }
        }

        int wrongListenerNum = (int) (wrongFactor * numHandlers);
        var wrongType = TypeToken.getParameterized(TestEventB.class, Integer.class);
        for (int i = 0; i < wrongListenerNum; i++) {
            busTypedChaotic.register(wrongType, this::handler);
        }
        var correctType = eventTyped.type();
        eventToChaoticDeliver = eventTyped;
        for (int i = 0; i < (numHandlers - wrongListenerNum); i++) {
            busTypedChaotic.register(correctType, this::handler);
        }
        var random = new Random(1337);
        var list = new ArrayList<>(busTypedChaotic.handlers);
        Collections.shuffle(list, random);
        // what we did here is to shuffle listener randomly but not using their hashCode
        // since its(a lambda object) hashCode will change on a new run. We need to eliminate these random factors.
        busTypedChaotic = new TypeAwareBus(Set.of(list.toArray(new TypeAwareBus.HandlerEntry[0])));
    }

    <E extends Event> void handler(E event) {
        Blackhole.consumeCPU(10);
    }

    <E extends Event> void handlerInterrupting(E event) throws Interruption {
        throw Interruption.INTERRUPTION;
    }

    @Benchmark
    @CompilerControl(CompilerControl.Mode.DONT_INLINE)
    public void deliverEventArray() {
        try {
            var handlers = this.arrayHandlers;
            for (int i = 0; i < handlers.size(); i++) {
                handlers.get(i).handle(eventToDeliver);
            }
        } catch (Interruption e) {
            throw new RuntimeException(e);
        }
    }

    @Benchmark
    @CompilerControl(CompilerControl.Mode.DONT_INLINE)
    public void deliverEventSimple() {
        busSimple.post(eventToDeliver);
    }

    @Benchmark
    @CompilerControl(CompilerControl.Mode.DONT_INLINE)
    public void deliverEventTyped() {
        busTyped.post(eventToDeliver);
    }

    @Benchmark
    @CompilerControl(CompilerControl.Mode.DONT_INLINE)
    public void deliverEventTypedChaotic() {
        busTypedChaotic.post(eventToChaoticDeliver);
    }

    public void deliverEventInterrupting() {
        busInterruption.post(eventToChaoticDeliver);
    }
}
