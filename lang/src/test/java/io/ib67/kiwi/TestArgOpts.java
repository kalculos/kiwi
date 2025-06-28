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

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

public class TestArgOpts {
    @Test
    public void test() {
        var args = new String[]{
                "--say", "world", "--boolFlag", "--help", "false", "--repeated", "1", "--repeated", "1", "nonargs", "nonargs"
        };
        var argsOpts = ArgOpts.builder()
                .programName("hellokiwi")
                .description("A simple helloworld")
                .args(args).build();
        var say = argsOpts.string("say", "ababababababababaaaaaaaaaaaaaaaaaaaaabab", "waludo");
        var boolFlag = argsOpts.bool("boolFlag", false);
        var flagHelp = argsOpts.bool("help", true);
        var repeated = argsOpts.stringList("repeated", List.of());
        assertEquals("world", say);
        assertTrue(boolFlag);
        assertFalse(flagHelp);
        assertEquals("11", repeated.stream().collect(Collectors.joining("")));
        assertEquals("nonargsnonargs", argsOpts.nonArgOptions().stream().collect(Collectors.joining("")));
    }
}
