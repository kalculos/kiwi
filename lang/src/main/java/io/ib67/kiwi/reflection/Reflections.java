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

package io.ib67.kiwi.reflection;

import io.ib67.kiwi.routine.Fail;
import io.ib67.kiwi.routine.Uni;
import lombok.SneakyThrows;
import org.jetbrains.annotations.ApiStatus;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * Some utility methods for reflection.
 */
@ApiStatus.AvailableSince("1.0.0")
public class Reflections {
    public static boolean isPrimitiveOrBox(Class<?> clazz) {
        return clazz.isPrimitive()
                || clazz == Integer.class
                || clazz == Long.class
                || clazz == Double.class
                || clazz == Float.class
                || clazz == Byte.class
                || clazz == Short.class
                || clazz == Boolean.class
                || clazz == Character.class;
    }

    public static Uni<Field> iterateFieldsWithSuper(Class<?> clazz) {
        if (clazz == null) return Fail.none();
        return c -> {
            for (Field declaredField : clazz.getDeclaredFields()) {
                c.onValue(declaredField);
            }
            iterateFieldsWithSuper(clazz.getSuperclass()).onItem(c);
        };
    }

    public static Uni<Method> iterateMethodsWithSuper(Class<?> clazz) {
        if (clazz == null) return Fail.none();
        return c -> {
            for (Method declaredMethod : clazz.getDeclaredMethods()) {
                c.onValue(declaredMethod);
            }
            for (Class<?> anInterface : clazz.getInterfaces()) {
                iterateMethodsWithSuper(anInterface).onItem(c);
            }
            iterateMethodsWithSuper(clazz.getSuperclass()).onItem(c);
        };
    }

    @SneakyThrows
    public static Field fieldOf(Class<?> clazz, String fieldName) {
        return clazz.getDeclaredField(fieldName);
    }

    @SneakyThrows
    public static Field fieldOfSuper(Class<?> clazz, String fieldName) {
        var field =  iterateFieldsWithSuper(clazz)
                .filter(it -> it.getName().equals(fieldName))
                .takeOne();
        if(field == null) throw new NoSuchFieldException("No such field:" + fieldName);
        return field;
    }

    @SneakyThrows
    public static Method methodOf(Class<?> clazz, String methodName) {
        return clazz.getDeclaredMethod(methodName);
    }

    @SneakyThrows
    public static Method methodOfSuper(Class<?> clazz, String methodName) {
        var method = iterateMethodsWithSuper(clazz)
                .filter(it -> it.getName().equals(methodName))
                .takeOne();
        if (method == null) throw new NoSuchMethodException("No such method:" + methodName);
        return method;
    }
}
