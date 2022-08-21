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
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.Collectors;

public final class SimplePlayerMap<T> implements PlayerMap<T> {
    private final Map<UUID, T> map = new HashMap<>(Math.min(Bukkit.getMaxPlayers(), 32));

    @Override
    public int size() {
        return map.size();
    }

    @Override
    public boolean isEmpty() {
        return map.isEmpty();
    }

    @Override
    public boolean containsKey(Object key) {
        if (key instanceof Player) {
            return map.containsKey(((Player) key).getUniqueId());
        }
        return map.containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
        return map.containsValue(value);
    }

    @Override
    public T get(Object key) {
        if (key instanceof Player) {
            return map.get(((Player) key).getUniqueId());
        }
        return map.get(key);
    }

    @Nullable
    @Override
    public T put(Player key, T value) {
        return map.put(key.getUniqueId(), value);
    }

    @Override
    public T remove(Object key) {
        if (key instanceof Player) {
            return map.remove(((Player) key).getUniqueId());
        }
        return map.remove(key);
    }

    @Override
    public void putAll(@NotNull Map<? extends Player, ? extends T> m) {
        m.entrySet().stream().map(entry -> Map.entry(entry.getKey().getUniqueId(), entry.getValue()))
                .forEach(entry -> map.put(entry.getKey(), entry.getValue()));
    }

    @Override
    public void clear() {
        map.clear();
    }

    @NotNull
    @Override
    public Set<@Nullable Player> keySet() {
        return map.keySet().stream().map(Bukkit::getPlayer).collect(Collectors.toSet());
    }

    @NotNull
    @Override
    public Collection<T> values() {
        return map.values();
    }

    @NotNull
    @Override
    public Set<Entry<@Nullable Player, T>> entrySet() {
        return map.entrySet().stream().map(it -> Map.entry(Bukkit.getPlayer(it.getKey()), it.getValue()))
                .collect(Collectors.toSet());
    }
}
