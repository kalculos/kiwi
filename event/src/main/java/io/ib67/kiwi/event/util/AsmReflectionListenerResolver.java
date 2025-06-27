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

package io.ib67.kiwi.event.util;

import io.ib67.kiwi.event.api.EventHandler;
import io.ib67.kiwi.event.api.EventListenerHost;
import io.ib67.kiwi.routine.Interruption;
import io.ib67.kiwi.routine.Uni;
import lombok.SneakyThrows;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import java.lang.invoke.MethodHandles;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

public class AsmReflectionListenerResolver extends ReflectionListenerResolver implements Opcodes {
    protected static final String DUMP_PATH = System.getProperty("kiwi.event.asmdumpdir", null);
    protected static final AtomicInteger CLASS_COUNTER = new AtomicInteger();

    public AsmReflectionListenerResolver(MethodHandles.Lookup lookup, EventListenerHost host) {
        super(lookup, host);
    }

    @SneakyThrows
    @Override
    protected EventHandler<?> createEventHandler(Method method) {
        var newClazz = defineEventHandlerClass(generateCaller(method));
        return (EventHandler<?>) newClazz.getConstructor(host.getClass()).newInstance(host);
    }

    @SneakyThrows
    protected byte[] generateCaller(Method method) {
        //todo doc: default methods as subscribers are not supported.
        var handlerMethod = Uni.of(EventHandler.class.getMethods()).filter(it -> !it.isDefault()).takeOne();
        assert handlerMethod != null;
        var cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
        var hostInternalName = Type.getInternalName(method.getDeclaringClass());
        var accessorName = hostInternalName + "$" + method.getName() + "$" + CLASS_COUNTER.getAndIncrement();
        cw.visit(
                V21,
                ACC_PUBLIC + ACC_FINAL,
                accessorName,
                null, "java/lang/Object", new String[]{Type.getInternalName(EventHandler.class)}
        );
        cw.visitField(
                ACC_PRIVATE + ACC_FINAL,
                "listenerHost",
                Type.getDescriptor(method.getDeclaringClass()),
                null, null
        ).visitEnd();
        var constructor = cw.visitMethod(ACC_PUBLIC, "<init>", "(L" + hostInternalName + ";)V", null, null);
        constructor.visitVarInsn(ALOAD, 0);
        constructor.visitInsn(DUP);
        constructor.visitMethodInsn(INVOKESPECIAL, Type.getInternalName(Object.class), "<init>", "()V", false);
        constructor.visitVarInsn(ALOAD, 1);
        constructor.visitMethodInsn(INVOKESTATIC, Type.getInternalName(Objects.class), "requireNonNull", "(Ljava/lang/Object;)Ljava/lang/Object;", false);
        constructor.visitTypeInsn(CHECKCAST, Type.getInternalName(method.getDeclaringClass()));
        constructor.visitFieldInsn(PUTFIELD, accessorName, "listenerHost", Type.getDescriptor(method.getDeclaringClass()));
        constructor.visitInsn(RETURN);
        constructor.visitMaxs(3, 3);
        constructor.visitEnd();
        var handle = cw.visitMethod(
                ACC_PUBLIC + ACC_FINAL,
                "handle", Type.getMethodDescriptor(handlerMethod), // this is an assertion
                null, new String[]{Type.getInternalName(Interruption.class)}
        );
        handle.visitVarInsn(ALOAD, 0);
        handle.visitFieldInsn(GETFIELD, accessorName, "listenerHost", Type.getDescriptor(method.getDeclaringClass()));
        handle.visitVarInsn(ALOAD, 1);
        handle.visitTypeInsn(CHECKCAST, Type.getInternalName(method.getParameterTypes()[0]));
        handle.visitMethodInsn(INVOKEVIRTUAL, hostInternalName, method.getName(), Type.getMethodDescriptor(method), false);
        handle.visitInsn(RETURN);
        handle.visitMaxs(2, 2);
        handle.visitEnd();
        cw.visitEnd();
        var result = cw.toByteArray();
        if (DUMP_PATH != null) {
            var baseDir = Path.of(DUMP_PATH);
            Files.createDirectories(baseDir.resolve(hostInternalName).getParent());
            Files.write(baseDir.resolve(accessorName + ".class"), result);
        }
        return result;
    }

    @SneakyThrows
    protected Class<?> defineEventHandlerClass(byte[] method) {
//        Files.write(Path.of("Test.class"), result); //todo dump option
        return lookup.defineClass(method);
    }
}
