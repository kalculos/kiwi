/*
 * MIT License
 *
 * Copyright (c) 2023 InlinedLambdas and Contributors
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

package io.ib67.kiwi.platform;

public class KiwiPlatform {
    public static final PlatformType CURRENT = getType();
    public static final boolean IS_AOT = Boolean.getBoolean("com.oracle.graalvm.isaot");
    public static final boolean HAS_GUI = isGuiSupported();

    private static boolean isGuiSupported() {
        if (CURRENT == PlatformType.WINDOWS) {
            return true; // why not? :)
        }
        // check environment variable.
        return !System.getenv("XDG_SESSION_TYPE").isEmpty();
    }

    private static PlatformType getType() {
        var os_name = System.getProperty("os.name");
        if (os_name.toLowerCase().contains("nux")) {
            return PlatformType.LINUX;
        } else if (os_name.toLowerCase().contains("win")) {
            return PlatformType.WINDOWS;
        }
        return PlatformType.UNIX;
    }

}
