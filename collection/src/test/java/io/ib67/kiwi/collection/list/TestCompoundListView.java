/*
 * MIT License
 *
 * Copyright (c) 2023 InlinedLambdas and Contributors
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

package io.ib67.kiwi.collection.list;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

public class TestCompoundListView {
    @Test
    public void testForEach() {
        var listA = List.of(1);
        var listB = List.of(2);
        var list = new CompoundListView<>(listA, listB);
        var results = new int[2];
        var counter = 0;
        for (Integer integer : list) {
            results[counter++] = integer;
        }
        assertArrayEquals(new int[]{1, 2}, results);
    }

    @Test
    public void testIndexGet() {
        var listA = List.of("a", "a", "b");
        var listB = List.of("c", "d", "d");
        var com = new CompoundListView<>(listA, listB);
        assertEquals("a", com.get(0));
        assertEquals("c", com.get(3));
        assertThrows(ArrayIndexOutOfBoundsException.class, () -> com.get(6));
    }

    @Test
    public void testRemove() {
        var listA = new ArrayList<>(List.of("a", "b", "c"));
        var listB = new ArrayList<>(List.of("d", "e", "f"));
        var com = new CompoundListView<>(listA, listB);
        com.remove("b");
        assertFalse(com.contains("b"));
        assertEquals("acdef", com.stream().collect(Collectors.joining()));
    }

    @Test
    public void testIndexOf() {
        var listA = new ArrayList<>(List.of("d", "b", "c"));
        var listB = new ArrayList<>(List.of("d", "e", "f"));
        var com = new CompoundListView<>(listA, listB);
        assertEquals(2, com.lastIndexOf("c"));
        assertEquals(3, com.lastIndexOf("d"));
        assertEquals(5, com.indexOf("f"));
    }

    @Test
    public void testInsertion() {
        var listA = new ArrayList<>(List.of("a", "b", "c"));
        var listB = new ArrayList<>(List.of("d", "e", "f"));
        var com = new CompoundListView<>(listA, listB);
        com.add(1, "d");
        assertEquals("adbcdef", com.stream().collect(Collectors.joining()));
        com.add(5, "d");
        assertEquals("adbcddef", com.stream().collect(Collectors.joining()));
    }
}
