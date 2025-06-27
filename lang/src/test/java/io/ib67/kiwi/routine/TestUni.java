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

package io.ib67.kiwi.routine;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TestUni {
    @Test
    void testUniOf() {
        Uni<String> uni = Uni.of("test");
        uni.onItem(value -> assertEquals("test", value));
    }

    @Test
    void testUniOfSupplier() {
        Uni<String> uni = Uni.of(() -> "test");
        uni.onItem(value -> assertEquals("test", value));
    }

    @Test
    void testMap() {
        Uni<String> uni = Uni.of("test");
        Uni<Integer> mapped = uni.map(String::length);
        mapped.onItem(value -> assertEquals(4, value));
    }

    @Test
    void testFlatMap() {
        Uni<String> uni = Uni.of("test");
        Uni<Integer> flatMapped = uni.flatMap(str -> Uni.of(str.length()));
        flatMapped.onItem(value -> assertEquals(4, value));
    }

    @Test
    void testFilter() {
        Uni<Integer> uni = Uni.of(5);
        List<Integer> results = new ArrayList<>();

        uni.filter(i -> i > 3).onItem(results::add);
        assertEquals(List.of(5), results);

        results.clear();
        uni.filter(i -> i > 10).onItem(results::add);
        assertTrue(results.isEmpty());
    }

    @Test
    void testLimit() {
        List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5);
        List<Integer> limited = new ArrayList<>();
        Uni<Integer> uni = numbers::forEach;
        uni.limit(3).onItem(limited::add);

        assertEquals(Arrays.asList(1, 2, 3), limited);
    }

    @Test
    void testAny() {
        Uni<Integer> uni = Uni.from(consumer -> {
            for (int i = 1; i <= 5; i++) {
                consumer.onValue(i);
            }
        });

        assertTrue(uni.any(i -> i > 3));
        assertFalse(uni.any(i -> i > 10));
    }

    @Test
    void testAll() {
        Uni<Integer> uni = Uni.from(consumer -> {
            for (int i = 1; i <= 5; i++) {
                consumer.onValue(i);
            }
        });

        assertTrue(uni.all(i -> i > 0));
        assertFalse(uni.all(i -> i > 3));
    }

    @Test
    void testNone() {
        Uni<Integer> uni = Uni.from(consumer -> {
            for (int i = 1; i <= 5; i++) {
                consumer.onValue(i);
            }
        });

        assertTrue(uni.none(i -> i > 10));
    }

    @Test
    void testTakeOne() {
        Uni<String> uni = Uni.from(consumer -> {
            consumer.onValue("first");
            consumer.onValue("second");
        });

        assertEquals("first", uni.takeOne());
    }

    @Test
    void testToList() {
        List<Integer> expected = Arrays.asList(1, 2, 3);
        Uni<Integer> uni = Uni.from(consumer -> {
            for (int i : expected) {
                consumer.onValue(i);
            }
        });

        assertEquals(expected, uni.toList());
    }

    @Test
    void testPeek() {
        List<Integer> peeked = new ArrayList<>();
        List<Integer> result = new ArrayList<>();

        Uni<Integer> uni = Uni.of(42);
        uni.peek(value -> peeked.add(value))
                .onItem(result::add);

        assertEquals(List.of(42), peeked);
        assertEquals(List.of(42), result);
    }

    @Test
    void testReduce() {
        Uni<Integer> uni = Uni.from(consumer -> {
            for (int i = 1; i <= 3; i++) {
                consumer.onValue(i);
            }
        });

        uni.reduce(Integer::sum).onItem(value -> assertEquals(6, value));
    }

    @Test
    void testUniq() {
        Uni<Integer> uni = c -> {
            int i = 0;
            while (true) {
                c.onValue(i++);
                c.onValue(i);
            }
        };
        // sum [0 to 4]
        assertEquals(10, uni.unique().limit(5).reduce(Integer::sum).takeOne());
    }

    @Test
    void testMultiMap() {
        Uni.infiniteAscendingNum().<Integer>multiMap((i, sink) -> {
            sink.onValue(i);
            sink.onValue(i);
        }).limit(2).onItem(value -> assertEquals(0, value));
    }

    @Test
    void testForFirst() {
        Uni.infiniteAscendingNum()
                .forFirst(it -> it + 1)
                .limit(2).onItem(value -> assertEquals(1, value));

    }

    @Test
    void testOnce() {
        var uni = Uni.infiniteAscendingNum()
                .once()
                .filter(it -> it % 2 == 0).limit(2);
        uni.onItem(it -> {
        });
        assertThrows(IllegalStateException.class, () -> uni.onItem(System.out::println));
    }

    @Test
    void testThen() {
        var uni = Uni.of(1)
                .then(it -> null);
        assertNull(uni);
    }
}