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

import org.inlambda.kiwi.reflection.AccessibleClass;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.*;

/**
 * A simple hashset implementation that uses weak references to the objects.
 * Also see {@link java.lang.ref.WeakReference}
 *
 * @param <E>
 */
@ApiStatus.AvailableSince("0.1.0")
public final class WeakHashSet<E> implements Set<E> {
    private static final Object EMPTY_OBJ = new Object();
    private final Map<E, Object> map;

    public WeakHashSet(int capacity) {
        this.map = new WeakHashMap<>(capacity);
    }

    public WeakHashSet() {
        this(16);
    }

    @Override
    public int size() {
        return map.size();
    }

    @Override
    public boolean isEmpty() {
        return map.size() == 0;
    }

    @Override
    public boolean contains(Object o) {
        return map.containsKey(o);
    }

    @NotNull
    @Override
    public Iterator<E> iterator() {
        return map.keySet().iterator();
    }

    @NotNull
    @Override
    public Object[] toArray() {
        return map.keySet().toArray();
    }

    @NotNull
    @Override
    public <T> T[] toArray(@NotNull T[] a) {
        return map.keySet().toArray(a);
    }

    @Override
    public boolean add(E e) {
        return map.put(e, EMPTY_OBJ) == null;
    }

    @Override
    public boolean remove(Object o) {
        return map.remove(o) != null;
    }

    @Override
    public boolean containsAll(@NotNull Collection<?> c) {
        return c.stream().allMatch(this::contains);
    }

    @Override
    public boolean addAll(@NotNull Collection<? extends E> c) {
        c.forEach(this::add);
        return true;
    }

    @Override
    public boolean retainAll(@NotNull Collection<?> c) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean removeAll(@NotNull Collection<?> c) {
        c.forEach(this::remove);
        return true;
    }

    @Override
    public void clear() {
        map.clear();
    }

    /**
     * Disable resizing.
     */
    public void disableResizing() {
        AccessibleClass.of(WeakHashMap.class).virtualField("threshold").set((WeakHashMap<?, ?>) map, Integer.MAX_VALUE);
    }
}