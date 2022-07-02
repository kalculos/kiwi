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

import org.inlambda.kiwi.option.Option;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

import static org.inlambda.kiwi.range.IntRange.rangePositive;

/**
 * An {@link ArrayStack} that accepts unlimited elements
 *
 * @param <E>
 */
@ApiStatus.AvailableSince("0.2.3")
public final class ArrayOpenStack<E> implements ArrayStack<E> {
    private final int expandMultiplier;
    private E[] elements;
    private int len;

    public ArrayOpenStack() {
        this(16, 2);
    }

    @SuppressWarnings("unchecked")
    public ArrayOpenStack(int capacity, int expandMultiplier) {
        rangePositive().inRange(capacity);
        rangePositive().inRange(expandMultiplier);
        this.expandMultiplier = expandMultiplier;
        elements = (E[]) new Object[capacity];
    }

    @Override
    public E pop() {
        if (len == 0) {
            throw new ArrayIndexOutOfBoundsException("pop from empty stack");
        }
        var e = elements[len - 1];
        elements[--len] = null; // otherwise memory leak.
        return e;
    }

    @Override
    public void push(E element) {
        if (len == elements.length) {
            var newElements = (E[]) new Object[elements.length * expandMultiplier];
            System.arraycopy(elements, 0, newElements, 0, len);
            elements = newElements;
        }
        elements[++len] = element;
    }

    @Override
    public @NotNull Option<E> peek() {
        if (len == 0) {
            return Option.none();
        }
        return Option.of(elements[len - 1]);
    }

    @Override
    public boolean isEmpty() {
        return len == 0;
    }

    @Override
    public E get(int index) {
        if (index - 1 > len) {
            throw new ArrayIndexOutOfBoundsException("index out of bounds");
        }
        return elements[index];
    }

    @Override
    public int size() {
        return len;
    }

    @Override
    public E[] toArray() {
        return Arrays.copyOf(elements, len);
    }

    @Override
    public boolean hasSlot() {
        return true;
    }
}
