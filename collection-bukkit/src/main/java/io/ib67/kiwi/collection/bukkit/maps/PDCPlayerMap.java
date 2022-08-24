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

package io.ib67.kiwi.collection.bukkit.maps;

import io.ib67.kiwi.collection.bukkit.PlayerMap;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataHolder;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

public class PDCPlayerMap<T> implements PlayerMap<T> {
    private static final Set<Class<?>> PRIMITIVES = Set.of(
            Byte.class,
            byte[].class,
            Short.class,
            Integer.class,
            int[].class,
            Long.class,
            long[].class,
            Float.class,
            Double.class,
            String.class
    );
    private final PersistentDataType<?, T> tag;
    private final NamespacedKey namedKey;

    public PDCPlayerMap(PersistentDataType<?, T> tag, NamespacedKey key) {
        this.tag = tag;
        this.namedKey = key;
        if (!PRIMITIVES.contains(tag.getPrimitiveType())) {
            throw new IllegalArgumentException("Unsupported Primitive Type: " + tag.getPrimitiveType().getName());
        }
    }

    @Override
    public int size() {
        throw new UnsupportedOperationException("PDCPlayerMap doesn't have a size");
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    @Override
    public boolean containsKey(Object key) {
        if (key instanceof PersistentDataHolder) {
            var p = (PersistentDataHolder) key;
            return p.getPersistentDataContainer().has(namedKey, tag);
        }
        throw new IllegalArgumentException("Only PersistentDataHolders are supported.");
    }

    @Override
    public boolean containsValue(Object value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public T get(Object key) {
        if (key instanceof PersistentDataHolder) {
            var p = ((PersistentDataHolder) key);
            return p.getPersistentDataContainer().get(namedKey, tag);
        } else {
            throw new UnsupportedOperationException("Not a persistentDataHolder");
        }
    }

    @Nullable
    @Override
    public T put(Player key, T value) {
        var old = get(key);
        key.getPersistentDataContainer().set(namedKey, tag, value);
        return old;
    }

    @Override
    public T remove(Object key) {
        if (key instanceof PersistentDataHolder) {
            var pdc = ((PersistentDataHolder) key).getPersistentDataContainer();
            var old = get(key);
            pdc.remove(namedKey);
            return old;
        }
        throw new UnsupportedOperationException();
    }

    @Override
    public void putAll(@NotNull Map<? extends Player, ? extends T> m) {
        m.forEach(this::put);
    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException();
    }

    @NotNull
    @Override
    public Set<Player> keySet() {
        throw new UnsupportedOperationException();
    }

    @NotNull
    @Override
    public Collection<T> values() {
        throw new UnsupportedOperationException();
    }

    @NotNull
    @Override
    public Set<Entry<Player, T>> entrySet() {
        throw new UnsupportedOperationException();
    }
}
