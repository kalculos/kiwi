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

package io.ib67.kiwi.collection.stack;

import io.ib67.kiwi.Stack;
import io.ib67.kiwi.option.Option;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Stream;

@ApiStatus.AvailableSince("0.2.3")
public final class LinkedOpenStack<E> implements Stack<E> {
    private final LinkedList<E> list = new LinkedList<>();

    @Override
    public E pop() {
        var e = list.pollLast();
        if (e == null) throw new ArrayIndexOutOfBoundsException("The stack is empty");
        return e;
    }

    @Override
    public void push(E element) {
        list.add(element);
    }

    @Override
    public @NotNull Option<E> peek() {
        var e = list.peekLast();
        return Option.of(e);
    }

    @Override
    public boolean isEmpty() {
        return list.isEmpty();
    }

    @Override
    public E get(int index) {
        return list.get(index);
    }

    @Override
    public int indexOf(E e) {
        return list.lastIndexOf(e);
    }

    @Override
    public int size() {
        return list.size();
    }

    @Override
    @SuppressWarnings("unchecked")
    public E[] toArray() {
        return (E[]) list.toArray();
    }

    @Override
    public boolean contains(E e) {
        return list.contains(e);
    }

    @Override
    public List<E> toList() {
        return list;
    }

    @Override
    public Stream<E> stream() {
        return list.stream();
    }

    @NotNull
    @Override
    public Iterator<E> iterator() {
        return list.iterator();
    }

    @Override
    public String toString() {
        return list.toString();
    }

    @Override
    public void forEach(Consumer<? super E> consumer) {
        list.forEach(consumer);
    }
}
