/*
 * MIT License
 *
 * Copyright (c) 2022 InlinedLambdas and Contributors
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

package org.inlambda.kiwi.collection.stack;

import org.inlambda.kiwi.collection.iterator.ArrayIterator;
import org.inlambda.kiwi.option.Option;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Iterator;

import static org.inlambda.kiwi.range.IntRange.rangePositive;

public class ArrayFixedStack<E> implements ArrayStack<E> {
    private final E[] array;
    private int len;

    public ArrayFixedStack(int length) {
        rangePositive().inRange(length);
        this.array = (E[]) new Object[length];
    }

    @Override
    public E pop() {
        if (isEmpty()) {
            throw new ArrayIndexOutOfBoundsException("Stack is empty");

        }
        var e = array[len - 1];
        array[--len] = null;
        return e;
    }

    @Override
    public void push(E element) {
        if (len == array.length) {
            throw new ArrayIndexOutOfBoundsException("Stack is full");
        }
        array[++len] = element;
    }

    @Override
    public @NotNull Option<E> peek() {
        return Option.of(array[len - 1]);
    }

    @Override
    public boolean isEmpty() {
        return len == 0;
    }

    @Override
    public E get(int index) {
        return array[index];
    }

    @Override
    public int size() {
        return len;
    }

    @Override
    public E[] toArray() {
        return Arrays.copyOf(array, len);
    }

    @Override
    public boolean hasSlot() {
        return len != array.length;
    }

    @NotNull
    @Override
    public Iterator<E> iterator() {
        return new ArrayIterator<>(array);
    }

    @Override
    public String toString() {
        return Arrays.toString(array);
    }
}
