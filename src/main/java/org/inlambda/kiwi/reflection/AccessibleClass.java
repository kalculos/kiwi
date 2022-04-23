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

package org.inlambda.kiwi.reflection;

import org.inlambda.kiwi.Kiwi;
import org.jetbrains.annotations.ApiStatus;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@ApiStatus.AvailableSince("0.1.0")
public class AccessibleClass<T> {
    private static final MethodHandles.Lookup TRUSTED_LOOKUP;

    static {
        Unsafe.ensureClassInitialized(MethodHandles.Lookup.class);
        TRUSTED_LOOKUP = (MethodHandles.Lookup) new AccessibleField<>(MethodHandles.Lookup.class, "IMPL_LOOKUP", true).get(null);
    }

    private Class<T> clazz;
    private Map<String, AccessibleField<T>> fields = new HashMap<>();

    private AccessibleClass(Class<T> clazz) {
        this.clazz = clazz;
    }

    public static <A> AccessibleClass<A> of(Class<A> clazz) {
        return new AccessibleClass<>(clazz);
    }

    public AccessibleField<T> virtualField(String fieldName) {
        return fields.computeIfAbsent(fieldName, f -> new AccessibleField<>(clazz, fieldName, false));
    }

    public AccessibleField<T> staticField(String fieldName) {
        return fields.computeIfAbsent(fieldName, f -> new AccessibleField<>(clazz, fieldName, true));
    }

    public AccessibleClass<T> fillInFields(){
        for (Field declaredField : clazz.getDeclaredFields()) {
            virtualField(declaredField.getName());
        }
        return this;
    }

    public Collection<AccessibleField<T>> fields(){
        return fields.values().stream().filter(e->!e.isStatic()).collect(Collectors.toUnmodifiableList());
    }

    @SuppressWarnings("unchecked")
    public T newInstance(Object... args) throws NoSuchMethodException {
        if (args.length == 0) {
            // find empty accessible valid constructor
            try {
                var con = clazz.getDeclaredConstructor();
                con.setAccessible(true);
                return con.newInstance();
            } catch (Throwable ignored) {

            }
            // failed.
            try {
                return (T) Unsafe.getUnsafe().allocateInstance(clazz);
            } catch (InstantiationException e) {
                e.printStackTrace();
                throw new NoSuchMethodException("No accessible constructor found");
            }
        }
        try {
            Class<?>[] paraTypes = new Class[args.length];
            for (int i = 0; i < args.length; i++) {
                paraTypes[i] = args[i].getClass();
            }
            var con = clazz.getDeclaredConstructor(paraTypes);
            con.setAccessible(true);
            return con.newInstance(args);
        } catch (Throwable ignored) {
            throw new NoSuchMethodException("No accessible constructor found for " + clazz.getName());
        }
    }

    public MethodHandle method(String name, MethodType type) {
        return Kiwi.runAny(() -> TRUSTED_LOOKUP.findVirtual(clazz, name, type)).orElseThrow();
    }

}