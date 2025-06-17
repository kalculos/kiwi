/*
 * MIT License
 *
 * Copyright (c) 2025 InlinedLambdas and Contributors
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

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestTypeToken {
    @Test
    @SneakyThrows
    public void testResolveType() {
        class A<T> {
            List<List<T>> fieldA;
        }
        var token = new TypeToken<A<Integer>>() {};
        var fieldType = token.resolveField(A.class.getDeclaredField("fieldA"));
        assertEquals("List<List<Integer>>", fieldType.toString());
    }

    @Test
    @SneakyThrows
    public void testResolveParent() {
        interface Z<T3> {
        }
        class A<T1, T2> implements Z<T2> {
            T1 returns() {
                return null;
            }
        }
        class B<T2> extends A<String, T2> {
        }
        class C extends B<Integer> {
        }
        var token = TypeToken.resolve(C.class);
        System.out.println(token.inferType(Z.class));
    }

    @Test
    public void testWildcard() {
        var type = new TypeToken<List<? super List<? extends List<?>>>>() {
        };
        System.out.println(type);
        System.out.println(TypeToken.reduceBounds(type, true));
    }
}
