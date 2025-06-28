package io.ib67.kiwi.routine.op;

import io.ib67.kiwi.routine.Uni;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.UnaryOperator;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TestUniOp {
    @Test
    public void testUniDispatcher() {
        UniDispatcher<Integer> dispatcher = UniDispatcher.of();
        AtomicBoolean condition1 = new AtomicBoolean(false);
        AtomicBoolean condition2 = new AtomicBoolean(false);
        AtomicBoolean defaultCondition = new AtomicBoolean(false);

        dispatcher.add(
                i -> i == 1,
                t -> condition1.set(true)
        ).add(
                i -> i == 2,
                t -> condition2.set(true)
        );

        UnaryOperator<Uni<Integer>> operator = dispatcher.build();
        Uni<Integer> uni = c -> c.onValue(1);
        operator.apply(uni).onItem(t -> defaultCondition.set(true));

        assertTrue(condition1.get());
        assertFalse(condition2.get());
        assertFalse(defaultCondition.get());

        condition1.set(false);
        uni = c -> c.onValue(2);
        operator.apply(uni).onItem(t -> defaultCondition.set(true));

        assertFalse(condition1.get());
        assertTrue(condition2.get());
        assertFalse(defaultCondition.get());

        condition2.set(false);
        condition1.set(false);
        defaultCondition.set(false);
        uni = c -> c.onValue(3);
        operator.apply(uni).onItem(t -> defaultCondition.set(true));

        assertFalse(condition1.get());
        assertFalse(condition2.get());
        assertTrue(defaultCondition.get());
    }

    @Test
    public void testDispatch() {
        AtomicBoolean condition1 = new AtomicBoolean(false);
        AtomicBoolean defaultCondition = new AtomicBoolean(false);

        UnaryOperator<Uni<Integer>> operator = UniOp.dispatch(
                i -> i == 1,
                t -> condition1.set(true)
        );

        Uni<Integer> uni = c -> c.onValue(1);
        operator.apply(uni).onItem(t -> defaultCondition.set(true));

        assertTrue(condition1.get());
        assertFalse(defaultCondition.get());

        condition1.set(false);
        uni = c -> c.onValue(2);
        operator.apply(uni).onItem(t -> defaultCondition.set(true));

        assertFalse(condition1.get());
        assertTrue(defaultCondition.get());
    }

    @Test
    public void testDispatchMultiple() {
        AtomicBoolean condition1 = new AtomicBoolean(false);
        AtomicBoolean condition2 = new AtomicBoolean(false);
        AtomicBoolean defaultCondition = new AtomicBoolean(false);

        UnaryOperator<Uni<Integer>> operator = UniOp.dispatch(
                i -> i == 1,
                t -> condition1.set(true),
                i -> i == 2,
                t -> condition2.set(true)
        );

        Uni<Integer> uni = c -> c.onValue(1);
        operator.apply(uni).onItem(t -> defaultCondition.set(true));

        assertTrue(condition1.get());
        assertFalse(condition2.get());
        assertFalse(defaultCondition.get());

        condition1.set(false);
        uni = c -> c.onValue(2);
        operator.apply(uni).onItem(t -> defaultCondition.set(true));

        assertFalse(condition1.get());
        assertTrue(condition2.get());
        assertFalse(defaultCondition.get());

        condition2.set(false);
        condition1.set(false);
        defaultCondition.set(false);
        uni = c -> c.onValue(3);
        operator.apply(uni).onItem(t -> defaultCondition.set(true));

        assertFalse(condition1.get());
        assertFalse(condition2.get());
        assertTrue(defaultCondition.get());
    }

    @Test
    public void testExecutor() throws InterruptedException {
        AtomicBoolean executed = new AtomicBoolean(false);
        Uni<Integer> uni = c -> {
            executed.set(true);
            c.onValue(1);
        };

        UnaryOperator<Uni<Integer>> operator = UniOp.dispatch(
                i -> i == 1,
                t -> {}
        );

        Thread testThread = new Thread(() -> {
            operator.apply(uni).onItem(t -> {});
        });
        testThread.start();
        testThread.join();

        assertTrue(executed.get());
    }
}
