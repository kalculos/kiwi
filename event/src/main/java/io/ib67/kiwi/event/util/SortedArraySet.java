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
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

import static java.util.Collections.binarySearch;

public class SortedArraySet<E> implements SortedSet<E> {
    protected final Comparator<? super E> comparator;
    protected final List<E> backingList;

    /**
     * Copy constructor.
     */
    protected SortedArraySet(List<E> backingArray, Comparator<? super E> comparator) {
        this.backingList = backingArray;
        this.comparator = comparator;
    }

    public SortedArraySet(int initialCapacity, Comparator<? super E> comparator) {
        this.comparator = comparator;
        this.backingList = new ArrayList<>(initialCapacity);
    }

    @Override
    public @Nullable Comparator<? super E> comparator() {
        return comparator;
    }

    @Override
    public @NotNull SortedSet<E> subSet(E fromElement, E toElement) {
        return new SortedArraySet<>(
                backingList.subList(
                        indexOfThrow(fromElement),
                        indexOfThrow(toElement)
                ), comparator
        );
    }

    @Override
    public @NotNull SortedSet<E> headSet(E toElement) {
        return new SortedArraySet<>(
                backingList.subList(0, indexOfThrow(toElement)),
                comparator
        );
    }

    @Override
    public @NotNull SortedSet<E> tailSet(E fromElement) {
        return new SortedArraySet<>(
                backingList.subList(indexOfThrow(fromElement), backingList.size()),
                comparator
        );
    }

    protected int indexOfThrow(E element) {
        Objects.requireNonNull(element, "element");
        var index = binarySearch(backingList, element, comparator);
        if(index < 0){
            throw new NoSuchElementException(element.toString());
        }
        return index;
    }

    @Override
    public E first() {
        return backingList.getFirst();
    }

    @Override
    public E last() {
        return backingList.getLast();
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
}
