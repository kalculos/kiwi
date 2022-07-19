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


import lombok.Getter;
import lombok.SneakyThrows;
import org.jetbrains.annotations.ApiStatus;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Field;

/**
 * 用于访问 Field 的包装。
 * 访问方式有两种：
 * 1. 通过暴露出来的 getter / setter
 * 2. 使用 unsafe 设置
 * @param <T>
 */
@ApiStatus.AvailableSince("0.1.0")
public class AccessibleField<T> {
    private final Class<T> clazz;
    @Getter
    private final String fieldName;
    private MethodHandle setter;
    private MethodHandle getter;
    private long offset;
    @Getter
    private final boolean isStatic;

    @SneakyThrows
    public AccessibleField(Class<T> clazz, String fieldName, boolean isStatic) {
        this.isStatic = isStatic;
        this.clazz = clazz;
        this.fieldName = fieldName;
        /**
         * Find Setters.
         */
        Field field = clazz.getDeclaredField(fieldName);
        Class<?> fieldType = field.getType();
        try {
            if (isStatic) {
                setter = MethodHandles.lookup().findStatic(clazz, "set" + uppercaseFirst(fieldName), MethodType.methodType(void.class, fieldType));
            } else {
                setter = MethodHandles.lookup().findVirtual(clazz, "set" + uppercaseFirst(fieldName), MethodType.methodType(void.class, fieldType));
            }
        }catch(Throwable excepted){
            // Maybe there isn't a setter method.
            try {
                offset = !isStatic ? Unsafe.objectFieldOffset(field) : Unsafe.staticFieldOffset(field);
            } catch (Throwable t) {
                // and unsafe is unsupported

            }
        }
        try {
            if (isStatic) {
                getter = MethodHandles.lookup().findStatic(clazz, "get" + uppercaseFirst(fieldName), MethodType.methodType(fieldType));
            } else {
                getter = MethodHandles.lookup().findVirtual(clazz, "get" + uppercaseFirst(fieldName), MethodType.methodType(fieldType));
            }
        }catch(Throwable excepted) {
            // Maybe there isn't a Getter method.
            try {
                if (offset != 0L)
                    offset = !isStatic ? Unsafe.objectFieldOffset(field) : Unsafe.staticFieldOffset(field);
            } catch (Throwable t) {
                // and unsafe is unsupported.
            }
        }
    }

    @SneakyThrows
    public void set(T t,Object data) {
        //Object d = processors.stream().map(e->e.fromDatabase(fieldName,data)).filter(Objects::nonNull).findFirst().orElse(data);
        if (setter != null) {
            setter.invokeExact(t, data);
        }
        if (offset == 0) {
            var field = clazz.getDeclaredField(fieldName);
            if (!field.trySetAccessible()) {
                throw new IllegalAccessException("Field " + fieldName + " is not accessible.");
            }
            field.setAccessible(true);
            field.set(t, data);
            return;
        }
        Unsafe.putObject(t, offset, data);
    }
    @SneakyThrows
    public Object get(Object t) {
        //return processors.stream().map(e->e.toDatabase(fieldName,data)).filter(Objects::nonNull).findFirst().orElse(data);
        if (getter == null && offset == 0) {
            var field = clazz.getDeclaredField(fieldName);
            if (!field.trySetAccessible()) {
                throw new IllegalAccessException("Field " + fieldName + " is not accessible.");
            }
            field.setAccessible(true);
            return field.get(t);
        }
        return getter == null ? (!isStatic ? Unsafe.getObject(t, offset) : Unsafe.getStatic(clazz, fieldName)) : getter.invokeExact();
    }

    @SneakyThrows
    public Field reflect(){
        return clazz.getDeclaredField(fieldName);
    }
    private static final String uppercaseFirst(String str){
        char[] arr = str.toCharArray();
        arr[0] = Character.toUpperCase(arr[0]);
        return new String(arr);
    }
}