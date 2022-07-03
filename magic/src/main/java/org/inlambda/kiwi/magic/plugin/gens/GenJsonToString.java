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

import com.sun.source.tree.Tree;
import com.sun.tools.javac.code.Flags;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.TreeMaker;
import com.sun.tools.javac.util.List;
import com.sun.tools.javac.util.Name;
import com.sun.tools.javac.util.Names;

import java.util.stream.Collectors;

public class GenJsonToString {
    private GenJsonToString() {
    }

    public static JCTree.JCMethodDecl genMethod(TreeMaker maker, Names symbolTable, JCTree.JCClassDecl claz) {
        var method = maker.at(claz.pos).MethodDef(
                maker.Modifiers(Flags.PUBLIC | Flags.FINAL),
                symbolTable.fromString("toString"),
                maker.Ident(symbolTable.fromString("String")),
                List.nil(),
                List.nil(),
                List.nil(),
                maker.Block(0, makeReturnJsonExpress(maker, symbolTable, claz)), null
        );
        return method;
    }

    private static List<JCTree.JCStatement> makeReturnJsonExpress(TreeMaker maker, Names name, JCTree.JCClassDecl claz) {
        var json = claz.getMembers().stream().filter(e -> e.getKind() == Tree.Kind.VARIABLE)
                .filter(e -> e instanceof JCTree.JCVariableDecl).map(e -> (JCTree.JCVariableDecl) e).collect(Collectors.toMap(e -> e.getName(), e -> e.getType()));
        var list = json.entrySet().stream()
                .map(e -> maker.Binary(JCTree.Tag.PLUS, maker.Literal("\"" + e.getKey() + "\":"), processValue(e.getValue(), e.getKey(), maker, name)))
                .reduce((e1, e2) -> maker.Binary(JCTree.Tag.PLUS, e1, maker.Binary(JCTree.Tag.PLUS, maker.Literal(","), e2)))
                .orElseThrow();
        list = maker.Binary(JCTree.Tag.PLUS, maker.Literal("{"), list);
        list = maker.Binary(JCTree.Tag.PLUS, list, maker.Literal("}"));
        return List.of(maker.Return(
                list
        ));
    }

    private static JCTree.JCExpression processValue(JCTree value, Name name, TreeMaker maker, Names names) {
        var typeName = value.toString();
        switch (typeName) {
            case "List<String>":
            case "List<CharSequence>":
            case "Collection<CharSequence>":
            case "Collection<String>":
            case "Set<CharSequence>":
            case "Set<String>":
            case "Queue<CharSequence>":
            case "Queue<String>":
            case "String":
            case "CharSequence":
            case "StringBuilder":
            case "StringBuffer":
                var toS = maker.Exec(maker.Apply(
                        List.nil(),
                        maker.Select(
                                maker.Ident(name),
                                names.fromString("toString")
                        ),
                        List.nil()
                ));
                var replaced = maker.Exec(
                        maker.Apply(
                                List.nil(),
                                maker.Select(
                                        toS.expr,
                                        names.fromString("replaceAll")
                                ),
                                List.of(
                                        maker.Literal("\\\""),
                                        maker.Literal("\\\\\"")
                                )
                        )
                );
                //System.out.println(replaced.expr);
                return maker.Binary(JCTree.Tag.PLUS, maker.Literal("\""),
                        maker.Binary(JCTree.Tag.PLUS, replaced.expr, maker.Literal("\"")));
            default:
                return maker.Ident(name);
        }
    }
}
