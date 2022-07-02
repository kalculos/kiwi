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

package org.inlambda.kiwi;

import org.inlambda.kiwi.option.Option;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Stream;

/**
 * Re-designed faster {@link java.util.Stack<E>}
 *
 * @param <E> element
 */
@ApiStatus.AvailableSince("0.2.4")
public interface Stack<E> {
    /**
     * Pop the top element
     *
     * @return element
     * @throws ArrayIndexOutOfBoundsException if the stack is empty
     */
    E pop();

    /**
     * Push a new element to the top.
     *
     * @param element element to push
     */
    void push(E element);

    /**
     * Peek the top element.
     *
     * @return element or {@link Option#none()} if the stack is empty
     */
    @NotNull
    Option<E> peek();

    /**
     * if this is empty
     *
     * @return true if this is empty
     */
    boolean isEmpty();

    /**
     * Get the element at the specified index.
     *
     * @param index index
     * @return element
     * @throws ArrayIndexOutOfBoundsException if the index is out of bounds
     */
    E get(int index);

    default boolean contains(E e) {
        return indexOf(e) != -1;
    }

    /**
     * Search the element in the stack
     *
     * @param e element to search
     * @return index or -1
     */
    int indexOf(E e);

    /**
     * Size of the stack
     *
     * @return
     */
    int size();

    /**
     * Get the stack as an array
     *
     * @return array
     */
    E[] toArray();

    /**
     * Get the stack as an immutable list
     *
     * @return list
     */
    default List<E> toList() {
        return List.of(toArray());
    }

    default Stream<E> stream() {
        return Stream.of(toArray());
    }

    default void forEach(Consumer<E> consumer) {
        stream().forEach(consumer);
    }
}
