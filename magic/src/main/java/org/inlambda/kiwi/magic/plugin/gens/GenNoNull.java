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

package org.inlambda.kiwi.magic.plugin.gens;

import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.TreeMaker;
import com.sun.tools.javac.util.List;
import com.sun.tools.javac.util.ListBuffer;
import com.sun.tools.javac.util.Name;
import com.sun.tools.javac.util.Names;

import java.util.ArrayList;
import java.util.Collections;

public class GenNoNull {
    public static void genNonNulls(TreeMaker maker, Names names, JCTree.JCMethodDecl jmeth) {
        try {
            var param = new ListBuffer<Name>();
            for (JCTree.JCVariableDecl parameter : jmeth.getParameters()) {
                var annotation = parameter.getModifiers().annotations;

                if (annotation.stream().noneMatch(i -> i.annotationType.toString().contains("Nullable"))) {
                    param = param.append(parameter.name);
                }
            }
            maker.Import(maker.Ident(names.fromString("java.util.Objects")).getTree(), false);

            // make body
            var params = new ArrayList<>(java.util.List.of(param.toArray(new Name[0])));
            Collections.reverse(params);
            var statements = new ListBuffer<JCTree.JCStatement>();
            for (Name name : params) {
                var m = maker.at(jmeth.getBody().pos)
                        .Exec(
                                maker.Apply(
                                        List.nil(),
                                        maker.Select(maker.Ident(names.fromString("Objects")), names.fromString("requireNonNull")),
                                        List.of(
                                                maker.Ident(name),
                                                maker.Literal(name.toString() + " cannot be null")
                                        )
                                )
                        );
                statements.add(m);
            }
            jmeth.getBody().stats = jmeth.getBody().getStatements().prependList(statements.toList());
        } catch (Throwable t) {
            t.printStackTrace(System.out);
        }

    }
}
