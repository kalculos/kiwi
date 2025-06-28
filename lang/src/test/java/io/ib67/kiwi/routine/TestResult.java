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

package io.ib67.kiwi.routine;

import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

public class TestResult {
    @Test
    public void testResult() {
        Result<String> result = Fail.none();
        assertInstanceOf(Fail.class, result);
        assertEquals(result, Fail.none());
        result = new Some<>("hello");
        assertEquals(new Some<>("hello"), result);
        assertNull(Fail.none().result());
        Fail.none().accept(it -> {
            throw new IllegalStateException("Should not invoke the handler");
        });
        var failCalled = new boolean[]{false};
        Fail.none().onFail(fail -> failCalled[0] = true);
        assertTrue(failCalled[0]);
        result.onItem(it -> assertEquals("hello", it));
        assertEquals("hello", result.orElse("abcd"));
        assertEquals("hello", result.orElseGet(() -> "abcd"));
        assertEquals("hello", result.orElseThrow());

        var exception = new RuntimeException();
        result = Fail.of(exception);
        assertThrows(RuntimeException.class, result::orElseThrow);
        assertEquals("hello", result.orElse("hello"));
        assertEquals("hello", result.orElseGet(() -> "hello"));
        Result<String> finalResult = result;
        assertThrows(IllegalArgumentException.class, () -> finalResult.orElseThrow(IllegalArgumentException::new));

        result = Fail.of("str");
        assertThrows(IllegalStateException.class, result::orElseThrow);
        assertTrue(result.toOptional().isEmpty());

        assertEquals("hello", Result.fromAny(() -> "hello").result());
        assertInstanceOf(Fail.class, Result.fromAny(() -> {
            if(true) throw new IOException("hello");
            return "hello";
        }));
        assertInstanceOf(Some.class, Result.runAny(() -> {}));
        assertInstanceOf(Fail.class, Result.runAny(() -> {
            throw new IOException("hello");
        }));
    }
}
