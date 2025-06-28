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

package io.ib67.kiwi;

import io.ib67.kiwi.reflection.Reflections;
import io.ib67.kiwi.reflection.Unsafe;
import io.ib67.kiwi.routine.Uni;
import lombok.Generated;
import lombok.experimental.UtilityClass;
import org.jetbrains.annotations.ApiStatus;

import java.lang.reflect.Array;
import java.lang.reflect.Modifier;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Useful tools for diagnosing problems. Business codes should not rely on these tools as it utilizes {@link Unsafe}
 */
@Generated // not going to unit-test this
@ApiStatus.Experimental
@UtilityClass
public final class Diag {
    public static final Map<Predicate<Object>, Function<Object, String>> OBJECT_DUMPER = new ConcurrentHashMap<>();

    static {
        OBJECT_DUMPER.put(it -> Reflections.isPrimitiveOrBox(it.getClass()), String::valueOf);
        OBJECT_DUMPER.put(it -> it instanceof Class,
                it -> "(class) " + ((Class) it).getName() + "#" + Long.toHexString(System.identityHashCode(it)));
        OBJECT_DUMPER.put(it -> it instanceof String,
                it -> "\"" + it + "\"");
    }

    private static Uni<String> dumpArray0(int indent, Object object, Set<Object> visited) {
        var indentString = " ".repeat(indent);
        var len = Array.getLength(object);
        return c -> {
            if (Reflections.isPrimitiveOrBox(object.getClass().getComponentType())) {
                c.onValue(len + " [" + Uni.infiniteAscendingNum()
                        .limit(len)
                        .map(it -> String.valueOf(Array.get(object, it)))
                        .collect(Collectors.joining(", ")) + "]");
            }else{
                c.onValue(len + " [");
                for (int i = 0; i < len; i++) {
                    int finalI = i;
                    dumpObjectTree0(indent, Array.get(object, i), visited)
                            .forFirst(it -> finalI + ". " + it)
                            .map(it -> indentString + it)
                            .onItem(c);
                }
                c.onValue("]");
            }
        };
    }

    public static Uni<String> dumpObjectTree(int indent, Object object) {
        return dumpObjectTree0(indent, object, new HashSet<>());
    }

    private static Uni<String> dumpObjectTree0(int indent, Object object, Set<Object> visited) {
        if (object == null) return Uni.of("null");
        for (Predicate<Object> predicate : OBJECT_DUMPER.keySet()) {
            if (predicate.test(object)) {
                return Uni.of(OBJECT_DUMPER.get(predicate).apply(object));
            }
        }
        if (visited.contains(object)) {
            return Uni.of("(visited object) #" + Long.toHexString(System.identityHashCode(object)));
        }
        visited.add(object);
        var indentString = " ".repeat(indent);
        return c -> {
            var type = object.getClass();
            if (type.isArray()) {
                dumpArray0(indent, object, visited).onItem(c);
            } else {
                c.onValue(toSimplifiedFQDNClass(type.getName()) + " #" + Long.toHexString(System.identityHashCode(object)) + " {");
                Reflections.iterateFieldsWithSuper(type)
                        .filter(field -> !Modifier.isStatic(field.getModifiers()))
                        .onItem(declaredField -> {
                            try {
                                var handle = Unsafe.lookup().unreflectGetter(declaredField);
                                var fieldObj = handle.invoke(object);
                                var isSuper = declaredField.getDeclaringClass() == type;
                                var keyName = declaredField.getName() + (isSuper ? " (super)" : "");
                                if (fieldObj == null) {
                                    c.onValue(indentString + keyName + " = null");
                                    return;
                                }
                                dumpObjectTree0(indent, fieldObj, visited)
                                        .forFirst(it -> keyName + " = " + it)
                                        .map(it -> indentString + it)
                                        .onItem(c);

                            } catch (Throwable e) {
                                c.onValue(indentString + "(inaccessible) " + declaredField.getName());
                            }
                        });
                c.onValue("}");
            }
        };
    }

    public static String toSimplifiedFQDNClass(String fqdnClz) {
        Objects.requireNonNull(fqdnClz);
        var split = fqdnClz.split("\\.");
        var str = new StringBuilder();
        for (String word : split) {
            if (word.isEmpty()) continue;
            var firstChar = word.charAt(0);
            if (Character.isLowerCase(firstChar)) {
                str.append(word.charAt(0)).append(".");
                continue;
            }
            str.append(word);
        }
        return str.toString();
    }
}
