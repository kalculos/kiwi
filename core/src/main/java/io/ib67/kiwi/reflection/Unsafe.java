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

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.security.ProtectionDomain;
import java.util.Objects;

/**
 * @author IzzelAliz
 */
@SuppressWarnings("all")
@ApiStatus.AvailableSince("0.1.0")
public class Unsafe {

    private static final sun.misc.Unsafe unsafe;
    private static final MethodHandles.Lookup lookup;
    private static final MethodHandle defineClass;

    static {
        try {
            Field theUnsafe = sun.misc.Unsafe.class.getDeclaredField("theUnsafe");
            theUnsafe.setAccessible(true);
            unsafe = (sun.misc.Unsafe) theUnsafe.get(null);
            Unsafe.ensureClassInitialized(MethodHandles.Lookup.class);
            Field field = MethodHandles.Lookup.class.getDeclaredField("IMPL_LOOKUP");
            Object base = unsafe.staticFieldBase(field);
            long offset = unsafe.staticFieldOffset(field);
            lookup = (MethodHandles.Lookup) unsafe.getObject(base, offset);
            MethodHandle mh;
            try {
                Method sunMisc = unsafe.getClass().getMethod("defineClass", String.class, byte[].class, int.class, int.class, ClassLoader.class, ProtectionDomain.class);
                mh = lookup.unreflect(sunMisc).bindTo(unsafe);
            } catch (Exception e) {
                Class<?> jdkInternalUnsafe = Class.forName("jdk.internal.misc.Unsafe");
                Field internalUnsafeField = jdkInternalUnsafe.getDeclaredField("theUnsafe");
                Object internalUnsafe = unsafe.getObject(unsafe.staticFieldBase(internalUnsafeField), unsafe.staticFieldOffset(internalUnsafeField));
                Method internalDefineClass = jdkInternalUnsafe.getMethod("defineClass", String.class, byte[].class, int.class, int.class, ClassLoader.class, ProtectionDomain.class);
                mh = lookup.unreflect(internalDefineClass).bindTo(internalUnsafe);
            }
            defineClass = Objects.requireNonNull(mh);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> T getStatic(Class<?> cl, String name) {
        try {
            Unsafe.ensureClassInitialized(cl);
            Field field = cl.getDeclaredField(name);
            Object materialByNameBase = Unsafe.staticFieldBase(field);
            long materialByNameOffset = Unsafe.staticFieldOffset(field);
            return (T) Unsafe.getObject(materialByNameBase, materialByNameOffset);
        } catch (Exception e) {
            return null;
        }
    }

    public static MethodHandles.Lookup lookup() {
        return lookup;
    }

    public static sun.misc.Unsafe getUnsafe() {
        return unsafe;
    }

    public static Object getObject(Object o, long l) {
        return unsafe.getObject(o, l);
    }

    public static void putObject(Object o, long l, Object o1) {
        unsafe.putObject(o, l, o1);
    }

    public static long staticFieldOffset(Field field) {
        return unsafe.staticFieldOffset(field);
    }

    public static long objectFieldOffset(Field field) {
        return unsafe.objectFieldOffset(field);
    }

    public static Object staticFieldBase(Field field) {
        return unsafe.staticFieldBase(field);
    }

    public static void ensureClassInitialized(Class<?> aClass) {
        unsafe.ensureClassInitialized(aClass);
    }

    private static Class<?> getCallerClass() {
        return INSTANCE.getClassContext()[3];
    }

    private static final CallerClass INSTANCE = new CallerClass();

    private static class CallerClass extends SecurityManager {

        @Override
        public Class<?>[] getClassContext() {
            return super.getClassContext();
        }
    }

}