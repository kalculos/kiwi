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
    private SortedArraySet<Integer> set;

    @BeforeEach
    void setUp() {
        set = new SortedArraySet<>(10, Comparator.naturalOrder());
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

        assertTrue(set.remove(2));
        assertFalse(set.remove(4));
        assertEquals(2, set.size());
    }

    @Test
    void testFirstLast() {
        set.add(3);
        set.add(1);
        set.add(2);

        assertEquals(1, set.first());
        assertEquals(3, set.last());
    }

    @Test
    void testSubSet() {
        set.add(1);
        set.add(2);
        set.add(3);
        set.add(4);
        set.add(5);

        var subset = set.subSet(2, 4);
        assertEquals(2, subset.size());
        assertTrue(subset.contains(2));
        assertTrue(subset.contains(3));
    }

    @Test
    void testHeadSet() {
        set.add(1);
        set.add(2);
        set.add(3);
        set.add(4);
        set.add(5);

        var headSet = set.headSet(3);
        assertEquals(2, headSet.size());
        assertTrue(headSet.contains(1));
        assertTrue(headSet.contains(2));
    }

    @Test
    void testTailSet() {
        set.add(1);
        set.add(2);
        set.add(3);
        set.add(4);
        set.add(5);

        var tailSet = set.tailSet(3);
        assertEquals(3, tailSet.size());
        assertTrue(tailSet.contains(3));
        assertTrue(tailSet.contains(4));
        assertTrue(tailSet.contains(5));
    }

    @Test
    void testNullHandling() {
        assertThrows(IllegalArgumentException.class, () -> set.add(null));
        assertFalse(set.contains(null));
        assertFalse(set.remove(null));
    }
}
