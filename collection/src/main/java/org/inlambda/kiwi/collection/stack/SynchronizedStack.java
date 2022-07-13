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

import lombok.RequiredArgsConstructor;
import org.inlambda.kiwi.Stack;
import org.inlambda.kiwi.option.Option;
import org.jetbrains.annotations.NotNull;

import java.util.Iterator;

@RequiredArgsConstructor
public class SynchronizedStack<E> implements Stack<E> {
    private final Stack<E> origin;

    @Override
    public synchronized E pop() {
        return origin.pop();
    }

    @Override
    public synchronized void push(E element) {
        origin.push(element);
    }

    @Override
    public @NotNull
    synchronized Option<E> peek() {
        return origin.peek();
    }

    @Override
    public synchronized boolean isEmpty() {
        return origin.isEmpty();
    }

    @Override
    public synchronized E get(int index) {
        return origin.get(index);
    }

    @Override
    public synchronized int indexOf(E e) {
        return origin.indexOf(e);
    }

    @Override
    public synchronized int size() {
        return origin.size();
    }

    @Override
    public synchronized E[] toArray() {
        return origin.toArray();
    }

    @NotNull
    @Override
    public synchronized Iterator<E> iterator() {
        return origin.iterator();
    }

    @Override
    public String toString() {
        return origin.toString();
    }
}
