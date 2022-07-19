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

package io.ib67.kiwi.magic.plugin.jc;

import com.sun.source.tree.ClassTree;
import com.sun.source.util.TreeScanner;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.TreeMaker;
import com.sun.tools.javac.util.Context;
import com.sun.tools.javac.util.Names;
import io.ib67.kiwi.magic.plugin.gens.GenJsonToString;

public class JsonizedTreeScanner extends TreeScanner<Void, Context> {

    @Override
    public Void visitClass(ClassTree node, Context ctx) {
        try {
            var maker = TreeMaker.instance(ctx);
            var name = Names.instance(ctx);
            var claz = (JCTree.JCClassDecl) node;
            if (node.getModifiers().getAnnotations().stream().anyMatch(e -> e.getAnnotationType().toString().equals("Jsonized"))) {
                System.out.println("Jsonized class found: " + node.getSimpleName());
                //System.out.println(method);
                claz.defs = claz.getMembers().append(GenJsonToString.genMethod(maker, name, claz));
                ///  System.out.println(claz);
                super.visitClass(node, ctx);
            }
        } catch (Throwable t) {
            System.err.println(t.getMessage());
            for (StackTraceElement stackTraceElement : t.getStackTrace()) {
                System.out.println(stackTraceElement); // Since Javac Blocks it being output from stderr
            }
        }
        super.visitClass(node, ctx);
        return null;
    }

}
