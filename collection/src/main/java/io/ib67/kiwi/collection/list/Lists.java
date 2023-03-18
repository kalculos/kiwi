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

import lombok.experimental.UtilityClass;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Some utility for {@link java.util.List}s.
 */
@UtilityClass
public class Lists {
    /**
     * This utility accepts two lists and return a compound view of them, avoid copying them into a new List etc.
     *
     * @param listA list A
     * @param listB list B
     * @param <T>   Type of value
     * @return a compound view of them.
     * @apiNote be aware of empty slots in ArrayList.
     */
    public static <T> List<T> ofCompoundListView(List<@NotNull T> listA, List<@NotNull T> listB) {
        return new CompoundListView<>(listA, listB);
    }

    public static <T> List<T> ofCopiedCompoundList(List<T> listA, List<T> listB) {
        var newList = new ArrayList<T>(listA.size() + listB.size());
        newList.addAll(listA);
        newList.addAll(listB);
        return newList;
    }

    /**
     * Remove an element from arrayList without resizing the array. Can't guarantee the order.
     *
     * @param list
     * @param index
     */
    public static <T> void fastRemove(ArrayList<T> list, int index) {
        list.set(index, list.get(list.size() - 1));
        list.remove(list.size() - 1);
    }

    /**
     * Remove an element from arrayList without resizing the array. Can't guarantee the order.
     *
     * @param list
     * @param object
     * @param <T>
     */
    public static <T> void fastRemove(ArrayList<T> list, T object) {
        var index = list.indexOf(object);
        if (index != -1) fastRemove(list, index);
    }

    public static <T> void fastRemoveAll(ArrayList<T> list, T object) {
        int index;
        while ((index = list.indexOf(object)) != -1) fastRemove(list, index);
    }

    /**
     * Returns a list view preventing null from being added into the list.<br/>
     * Such bulk operations as {@link List#addAll(Collection)} will be slower. Null elements in the origin won't be noticed by the decorator.
     *
     * @param origin original list
     * @param <T>    type of Element
     * @return null-intolerant list
     */
    public static <T> NonNullList<T> noNullList(List<T> origin) {
        return new NullIntolerantList<>(origin);
    }

    @SafeVarargs
    public static <T> ArrayList<T> newArrayList(T... elements) {
        var list = new ArrayList<T>(elements.length);
        for (T a : elements) {
            list.add(a);
        }
        return list;
    }

}
