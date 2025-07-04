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

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Comparator;

import static org.junit.jupiter.api.Assertions.*;

class TestSortedArraySet {
    private SortedArrayList<Integer> set;

    @BeforeEach
    void setUp() {
        set = new SortedArrayList<>(10, Comparator.naturalOrder());
    }

    @Test
    void testAdd() {
        assertTrue(set.add(3));
        assertTrue(set.add(1));
        assertTrue(set.add(2));

        assertEquals(Arrays.asList(1, 2, 3), Arrays.asList(set.toArray()));
    }

    @Test
    void testAddDuplicates() {
        assertTrue(set.add(1));
        assertTrue(set.add(1)); // Implementation allows duplicates
        assertEquals(2, set.size());
    }

    @Test
    void testRemove() {
        set.add(1);
        set.add(2);
        set.add(3);
        assertTrue(set.remove((Object)2));
        assertFalse(set.remove((Object)4));
        assertEquals(2, set.size());
    }

    @Test
    void testFirstLast() {
        set.add(3);
        set.add(1);
        set.add(2);

        assertEquals(1, set.getFirst());
        assertEquals(3, set.getLast());
    }

    @Test
    void testNullHandling() {
        assertThrows(IllegalArgumentException.class, () -> set.add(null));
        assertFalse(set.contains(null));
        assertFalse(set.remove(null));
    }
}
