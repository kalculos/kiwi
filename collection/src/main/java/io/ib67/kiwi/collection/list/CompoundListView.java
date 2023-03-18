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

import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;

import java.util.*;

@RequiredArgsConstructor
class CompoundListView<T> implements List<T> {
    private final List<T> listA;
    private final List<T> listB;

    @Override
    public int size() {
        return listA.size() + listB.size();
    }

    @Override
    public boolean isEmpty() {
        return listA.isEmpty() && listB.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        return listA.contains(o) || listB.contains(o);
    }

    @NotNull
    @Override
    public Iterator<T> iterator() {
        return new CompoundListIterator(0);
    }

    @NotNull
    @Override
    public Object[] toArray() {
        var a = listA.toArray();
        var b = listB.toArray();
        var newArray = Arrays.copyOf(a, a.length + b.length);
        System.arraycopy(b, 0, newArray, a.length, b.length);
        return newArray;
    }

    @NotNull
    @Override
    public <T1> T1[] toArray(@NotNull T1[] a) {
        return (T1[]) toArray();
    }

    @Override
    public boolean add(T t) {
        return listB.add(t);
    }

    @Override
    public boolean remove(Object o) {
        return listA.remove(o) && listB.remove(o);
    }

    @Override
    public boolean containsAll(@NotNull Collection<?> c) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean addAll(@NotNull Collection<? extends T> c) {
        return listB.addAll(c);
    }

    @Override
    public boolean addAll(int index, @NotNull Collection<? extends T> c) {
        return listB.addAll(index, c);
    }

    @Override
    public boolean removeAll(@NotNull Collection<?> c) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean retainAll(@NotNull Collection<?> c) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void clear() {
        listA.clear();
        listB.clear();
    }

    @Override
    public T get(int index) {
        return index > listA.size() - 1 ? listB.get(index - listA.size()) : listA.get(index);
    }

    @Override
    public T set(int index, T element) {
        return index > listA.size() - 1 ? listB.set(index - listA.size(), element) : listA.set(index, element);
    }

    @Override
    public void add(int index, T element) {
        if (index > listA.size() - 1) {
            listB.add(index - listA.size(), element);
        } else {
            listA.add(index, element);
        }
    }

    @Override
    public T remove(int index) {
        return index > listA.size() - 1 ? listB.remove(index - listA.size()) : listA.remove(index);
    }

    @Override
    public int indexOf(Object o) {
        var i = listA.indexOf(o);
        if (i == -1) {
            var lb = listB.indexOf(o);
            return lb == -1 ? -1 : listA.size() + lb;
        } else {
            return i;
        }
    }

    @Override
    public int lastIndexOf(Object o) {
        var i = listB.lastIndexOf(o);
        return i == -1 ? listA.lastIndexOf(o) : listA.size() + i;
    }

    @NotNull
    @Override
    public ListIterator<T> listIterator() {
        return new CompoundListIterator(0);
    }

    @NotNull
    @Override
    public ListIterator<T> listIterator(int index) {
        return new CompoundListIterator(index);
    }

    @NotNull
    @Override
    public List<T> subList(int fromIndex, int toIndex) {
        throw new UnsupportedOperationException();
    }

    class CompoundListIterator implements ListIterator<T> {
        private ListIterator<T> currentIterator;
        private ListIterator<T> lastIterator;
        private boolean useIteratorB;

        CompoundListIterator(int index) {
            currentIterator = listA.listIterator(index);
        }

        @Override
        public boolean hasNext() {
            var hasNext = currentIterator.hasNext();
            if (!hasNext) {
                if (useIteratorB) return false;
                useIteratorB = true;
                lastIterator = currentIterator; // list A
                currentIterator = listB.listIterator();
                return hasNext();
            }
            return true;
        }

        @Override
        public T next() {
            return currentIterator.next();
        }

        @Override
        public boolean hasPrevious() {
            if (useIteratorB) {
                if (currentIterator.previousIndex() == -1) {
                    return lastIterator.previousIndex() != -1;
                }
            }
            return currentIterator.hasPrevious();
        }

        @Override
        public T previous() {
            if (useIteratorB) {
                if (currentIterator.previousIndex() == -1) {
                    useIteratorB = false;
                    currentIterator = lastIterator;
                    lastIterator = null;
                    return lastIterator.previous();
                }
            }
            return currentIterator.previous();
        }

        @Override
        public int nextIndex() {
            return useIteratorB ? listA.size() - 1 + currentIterator.nextIndex() : currentIterator.nextIndex();
        }

        @Override
        public int previousIndex() {
            return useIteratorB ?
                    currentIterator.previousIndex() == -1 ?
                            listA.size() - 1 : currentIterator.previousIndex()
                    : currentIterator.previousIndex();
        }

        @Override
        public void remove() {
            currentIterator.remove();
        }

        @Override
        public void set(T t) {
            currentIterator.set(t);
        }

        @Override
        public void add(T t) {
            currentIterator.add(t);
        }
    }
}
