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

package io.ib67.kiwi.reflection;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.util.Objects;

@ApiStatus.AvailableSince("0.3.1")
public final class MutableReference<T> {
    private T data;
    private Reference<T> ref;

    public MutableReference(T data) {
        Objects.requireNonNull(this.data = data);
    }

    public void weaken() {
        ref = new WeakReference<>(data);
        data = null;
    }

    public void clear() {
        ref = null;
        data = null;
    }

    public boolean isWeak() {
        return ref != null;
    }

    public boolean unWeak() {
        if ((data = ref.get()) == null) {
            ref = null;
            return false;
        } else {
            return true;
        }
    }

    @NotNull
    public Reference<T> unwrap() {
        return isWeak() ? ref : new WeakReference<>(data);
    }

    @Nullable
    public T get() {
        if (isWeak()) {
            return ref.get();
        } else {
            return data;
        }
    }
}
