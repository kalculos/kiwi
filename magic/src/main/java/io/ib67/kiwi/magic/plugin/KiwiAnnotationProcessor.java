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

package io.ib67.kiwi.magic.plugin;

import com.sun.source.util.Trees;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.TreeMaker;
import com.sun.tools.javac.util.Context;
import com.sun.tools.javac.util.Names;
import io.ib67.kiwi.magic.Jsonized;
import io.ib67.kiwi.magic.NoNullExcepted;
import io.ib67.kiwi.magic.plugin.gens.GenJsonToString;
import io.ib67.kiwi.magic.plugin.gens.GenNoNull;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import java.util.Set;

@SupportedAnnotationTypes("*")
@SupportedSourceVersion(SourceVersion.RELEASE_11)
public class KiwiAnnotationProcessor extends AbstractProcessor {
    private Context context;
    private Trees trees;
    private TreeMaker maker;
    private Names names;

    @Override
    public void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        trees = Trees.instance(processingEnv);
        try {
            var f = processingEnv.getClass().getDeclaredField("context");
            var f2 = processingEnv.getClass().getDeclaredField("taskListener");
            f.setAccessible(true);
            f2.setAccessible(true);
            context = (Context) f.get(processingEnv);
            maker = TreeMaker.instance(context);
            names = Names.instance(context);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        for (TypeElement annotation : annotations) {
            if (annotation.getQualifiedName().contentEquals(Jsonized.class.getName())) {
                for (Element element : roundEnv.getElementsAnnotatedWith(annotation)) {
                    var jtree = (JCTree.JCClassDecl) trees.getTree(element);
                    var method = GenJsonToString.genMethod(maker, names, jtree);
                    jtree.defs = jtree.defs.append(method);
                }
            } else if (annotation.getQualifiedName().contentEquals(NoNullExcepted.class.getName())) {
                for (Element element : roundEnv.getElementsAnnotatedWith(annotation)) {
                    var jmeth = (JCTree.JCMethodDecl) trees.getTree(element);
                    GenNoNull.genNonNulls(maker, names, jmeth);
                }
            }
        }
        return true;
    }

}
