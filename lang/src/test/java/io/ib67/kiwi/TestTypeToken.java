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
    public void testArray() {
        var token = new TypeToken<List<String[]>[]>() {
        };
        assertTrue(token.isArray());
        assertEquals("List<String[]>[]", token.toString());
    }

    @Test
    public void testEquals() {
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
        class TypeC extends TypeB<TypeA[]> {
        }
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

            public List<? extends A> complexProduce() {
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

    @Test
    public void testAssignableTo() {
        //@formatter:off
        var typeListString = new TypeToken<List<String>>(){};
        var typeListCharSeq = new TypeToken<List<CharSequence>>(){};
        var typeListWhoExtendsCharSeq = new TypeToken<List<? extends CharSequence>>(){};
        var typeListWhoSuperString = new TypeToken<List<? super String>>(){};

        var typeArrayString = new TypeToken<String[]>(){};
        var typeArrayCharSeq = new TypeToken<CharSequence[]>(){};
        var typeListArrayWhoExtendsCharSeq = new TypeToken<List<? extends CharSequence[]>>(){};
        var typeListArrayString = new TypeToken<List<String[]>>(){};

        var typeInteger = new TypeToken<Integer>(){};
        var typeNumber = new TypeToken<Number>(){};
        //@formatter:on
        // idential to themselves
        assertTrue(typeListString.assignableTo(typeListString));
        assertTrue(typeListCharSeq.assignableTo(typeListCharSeq));
        assertTrue(typeListWhoExtendsCharSeq.assignableTo(typeListWhoExtendsCharSeq));
        assertTrue(typeListWhoSuperString.assignableTo(typeListWhoSuperString));
        assertTrue(typeArrayString.assignableTo(typeArrayString));
        assertTrue(typeArrayCharSeq.assignableTo(typeArrayCharSeq));
        assertTrue(typeListArrayString.assignableTo(typeListArrayString));
        assertTrue(typeInteger.assignableTo(typeInteger));
        assertTrue(typeNumber.assignableTo(typeNumber));
        // for type params: type comparison, wildcard support
        assertFalse(typeListString.assignableTo(typeListCharSeq)); // List<String> NOT List<CharSequence>
        assertFalse(typeListCharSeq.assignableTo(typeListString)); // AND vice versa

        assertTrue(typeListString.assignableTo(typeListWhoExtendsCharSeq)); // List<String> IS List<? extends CharSequence>
        assertFalse(typeListWhoExtendsCharSeq.assignableTo(typeListString)); // AND List<? extends CharSequence> NOT List<String>

        assertFalse(typeListWhoExtendsCharSeq.assignableTo(typeListWhoSuperString)); // List<? extends CharSequence> NOT List<? super String>
        assertFalse(typeListWhoSuperString.assignableTo(typeListWhoExtendsCharSeq)); // AND vice versa

        assertTrue(typeListString.assignableTo(typeListWhoSuperString)); // List<String> IS List<? super String>
        assertFalse(typeListWhoSuperString.assignableTo(typeListString)); // AND List<? super String> NOT List<String>

        assertTrue(typeListCharSeq.assignableTo(typeListWhoExtendsCharSeq)); // List<CharSequence> IS List<? extends CharSequence>
        assertFalse(typeListWhoExtendsCharSeq.assignableTo(typeListCharSeq)); // AND List<? extends CharSequence> NOT List<CharSequence>

        // for arrays(and in param)
        assertTrue(typeArrayString.assignableTo(typeArrayCharSeq)); // String[] IS CharSequence[]
        assertFalse(typeArrayCharSeq.assignableTo(typeArrayString)); // AND vice versa

        assertTrue(typeListArrayString.assignableTo(typeListArrayWhoExtendsCharSeq)); // List<String[]> IS List<? extends CharSequence[]>
        assertFalse(typeListArrayWhoExtendsCharSeq.assignableTo(typeListArrayString)); // AND List<? extends CharSequence[]> NOT List<String[]>

        //for normal types:
        assertTrue(typeInteger.assignableTo(typeNumber));
        assertFalse(typeNumber.assignableTo(typeInteger));
    }

    //todo test hash function

}
