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

package io.ib67.kiwi.collection.bukkit;

import io.ib67.kiwi.collection.bukkit.maps.PDCPlayerMap;
import io.ib67.kiwi.collection.bukkit.maps.SimplePlayerMap;
import io.ib67.kiwi.collection.bukkit.maps.WeakPlayerMap;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;

import java.util.Map;
import java.util.function.Function;

public interface PlayerMap<T> extends Map<Player, T>, Function<Player, T> {
    static <T> PlayerMap<T> createWeakMap() {
        return new WeakPlayerMap<>();
    }

    /**
     * Some returned collections may contain null for Player
     *
     * @param <T>
     * @return
     */
    static <T> PlayerMap<T> createUUIDBasedMap() {
        return new SimplePlayerMap<>();
    }
    
    static <T> PlayerMap<T> createPDCBasedMap(NamespacedKey key, PersistentDataType<?, T> type) {
        return new PDCPlayerMap<>(type, key);
    }

    @Override
    default T apply(Player player) {
        return get(player);
    }
}
