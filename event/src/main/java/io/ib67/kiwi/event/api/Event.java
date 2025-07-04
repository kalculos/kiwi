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

package io.ib67.kiwi.event.api;

import io.ib67.kiwi.TypeToken;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

/**
 * An {@link Event}, with its properties, can be delivered to subscribers by posting itself on the EventBus.
 */
@ApiStatus.AvailableSince("0.1.0")
public interface Event {
    ClassValue<TypeToken<? extends Event>> RAW_TOKENS = new ClassValue<>() {
        @Override
        protected TypeToken<? extends Event> computeValue(@NotNull Class<?> type) {
            return TypeToken.resolve(type);
        }
    };
    /**
     * TypeToken of the Event. it may depend on some information gathered from an instance of Event.
     * @return TypeToken of the Event.
     */
    default TypeToken<? extends Event> type(){
        return RAW_TOKENS.get(this.getClass());
    }
}
