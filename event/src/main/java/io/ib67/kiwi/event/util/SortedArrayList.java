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

import io.ib67.kiwi.routine.Uni;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

import static java.util.Collections.binarySearch;

/**
 * A List implementation backed by ArrayList with sort-at-insertion, offering fast access and slow insertion.
 *
 * @param <E>
 */
@ApiStatus.AvailableSince("1.0.0")
public class SortedArrayList<E> implements List<E> {
    protected final Comparator<? super E> comparator;
    protected final List<E> backingList;

    /**
     * Copy constructor.
     */
    protected SortedArrayList(List<E> backingArray, Comparator<? super E> comparator) {
        this.backingList = backingArray;
        this.comparator = comparator;
    }

    public SortedArrayList(int initialCapacity, Comparator<? super E> comparator) {
        this.comparator = comparator;
        this.backingList = new ArrayList<>(initialCapacity);
    }

    public @Nullable Comparator<? super E> comparator() {
        return comparator;
    }

    protected int indexOfThrow(E element) {
        Objects.requireNonNull(element, "element");
        var index = binarySearch(backingList, element, comparator);
        if (index < 0) {
            throw new NoSuchElementException(element.toString());
        }
        return index;
    }

    @Override
    public int size() {
        return backingList.size();
    }

    @Override
    public boolean isEmpty() {
        return backingList.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        if (o == null) return false;
        return binarySearch(backingList, (E) o, comparator) >= 0;
    }

    @Override
    public @NotNull Iterator<E> iterator() {
        return backingList.iterator();
    }

    @Override
    public @NotNull Object[] toArray() {
        return backingList.toArray();
    }

    @Override
    public @NotNull <T> T[] toArray(@NotNull T[] a) {
        return backingList.toArray(a);
    }

    @Override
    public boolean add(E e) {
        if (e == null) {
            throw new IllegalArgumentException("Cannot add null element");
        }
        if (backingList.isEmpty()) {
            return backingList.add(e);
        }
        for (int i = 0; i < backingList.size(); i++) {
            var element = backingList.get(i);
            if (comparator.compare(element, e) < 0) {
                continue;
            }
            backingList.add(i, e);
            return true;
        }
        backingList.add(e);
        return true;
    }

    @Override
    public boolean remove(Object o) {
        if (o == null) return false;
        var index = binarySearch(backingList, (E) o, comparator);
        if (index < 0) {
            return false;
        }
        backingList.remove(index);
        return true;
    }

    @Override
    public boolean containsAll(@NotNull Collection<?> c) {
        return new HashSet<>(backingList).containsAll(c);
    }

    @Override
    public boolean addAll(@NotNull Collection<? extends E> c) {
        return Uni.from(c::forEach).any(this::add);
    }

    @Override
    public boolean addAll(int index, @NotNull Collection<? extends E> c) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean retainAll(@NotNull Collection<?> c) {
        return backingList.retainAll(c);
    }

    @Override
    public boolean removeAll(@NotNull Collection<?> c) {
        return backingList.removeAll(c);
    }

    @Override
    public void clear() {
        backingList.clear();
    }

    @Override
    public E get(int index) {
        return backingList.get(index);
    }

    @Override
    public E set(int index, E element) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void add(int index, E element) {
        throw new UnsupportedOperationException();
    }

    @Override
    public E remove(int index) {
        return backingList.remove(index);
    }

    @Override
    public int indexOf(Object o) {
        return indexOfThrow((E) o);
    }

    @Override
    public int lastIndexOf(Object o) {
        return backingList.lastIndexOf(o);
    }

    @Override
    public @NotNull ListIterator<E> listIterator() {
        return backingList.listIterator();
    }

    @Override
    public @NotNull ListIterator<E> listIterator(int index) {
        return backingList.listIterator(index);
    }

    @Override
    public @NotNull List<E> subList(int fromIndex, int toIndex) {
        return new SortedArrayList<>(
                backingList.subList(
                        fromIndex,
                        toIndex
                ), comparator
        );
    }
}
