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

import lombok.SneakyThrows;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class TestTypeToken {
    @Test
    public void testTypeResolveConstructor() {
        var token = new TypeToken<List<String>>() {
        };
        assertEquals(List.class, token.getBaseTypeRaw());
        assertEquals(String.class, token.getTypeParams().getFirst().getBaseTypeRaw());
    }

    @Test
    public void testGetParameterized() {
        var tokenString = TypeToken.getParameterized(List.class, String.class);
        assertEquals(List.class, tokenString.getBaseTypeRaw());
        assertEquals(String.class, tokenString.getTypeParams().getFirst().getBaseTypeRaw());
        assertEquals(new TypeToken<List<String>>() {
        }, tokenString);
    }

    @Test
    public void testArray(){
        var token = new TypeToken<List<String[]>[]>(){};
        assertTrue(token.isArray());
        assertEquals("List<String[]>[]", token.toString());
    }

    @Test
    public void testEquals(){
        var tokenString = TypeToken.getParameterized(List.class, String.class);
        var token = new TypeToken<List<String>>() {
        };
        assertEquals(tokenString, token);
        assertNotEquals(null, token);
        assertEquals(token, token);
    }

    @Test
    public void testResolve() {
        var token = TypeToken.resolve(List.class);
        assertEquals(List.class, token.getBaseTypeRaw());
        assertEquals(Object.class, token.getTypeParams().getFirst().getBaseTypeRaw());
        class Box<S extends String> {
        }
        assertEquals(String.class, TypeToken.resolve(Box.class).getTypeParams().getFirst().getBaseTypeRaw());
    }

    @Test
    public void testWildcards() {
        var wildcardToken = new TypeToken<List<? extends CharSequence>>() {
        };
        assertEquals("List<? extends CharSequence>", wildcardToken.toString());
        assertEquals("List<CharSequence>", TypeToken.reduceBounds(wildcardToken, false).toString());
        class TypeA {
        }
        class TypeB<T> extends TypeA {
        }
        var wildcardToken2 = new TypeToken<List<? super TypeB<?>>>() {
        };
        assertEquals("List<? super TypeB<?>>", wildcardToken2.toString());
        assertEquals("List<TypeA>", TypeToken.reduceBounds(wildcardToken2, true).toString());
        assertEquals("List<Object>", TypeToken.reduceBounds(wildcardToken2, false).toString());
        class TypeC extends TypeB<TypeA[]>  {}
        var typeC = TypeToken.resolve(TypeC.class);
        assertEquals("TypeB<TypeA[]>", typeC.resolveDirectParent().toString());
        assertTrue(typeC.resolveDirectParent().getTypeParams().getFirst().isArray());
    }

    @Test
    @SneakyThrows
    public void testInfer() {
        interface SuperType<D> {
        }
        class TypeA<T1, T2> {
        }
        class TypeB<E, D> extends TypeA<String, E> implements SuperType<D> {
        }
        class TypeC<A, C, D> extends TypeB<C, D> {
            public C field;

            public A produce() {
                return null;
            }
            public List<? extends A> complexProduce(){
                return null;
            }
        }
        var token = new TypeToken<TypeC<Double, Integer, Boolean>>() {
        };
        assertEquals("TypeA<String, Integer>", token.inferType(TypeA.class).toString());
        assertEquals("TypeB<?, ?>", new TypeToken<TypeC<?, ?, ?>>() {
        }.resolveDirectParent().toString());
        assertEquals("SuperType<Boolean>", token.inferType(SuperType.class).toString());
        var methodProduce = TypeC.class.getMethod("produce");
        var fieldField = TypeC.class.getField("field");
        assertEquals("Double", token.resolveReturnValue(methodProduce).toString());
        assertEquals("Integer", token.resolveField(fieldField).toString());
        var methodComplexProduce = TypeC.class.getMethod("complexProduce");
        assertThrows(IllegalArgumentException.class, () -> token.resolveReturnValue(methodComplexProduce).toString());
    }

    @Test
    public void testToString() {
        var token = new TypeToken<Map<String, Map<Map<String, String>, List<?>>>>() {
        };
        assertEquals("Map<String, Map<Map<String, String>, List<?>>>", token.toString());
    }
}
