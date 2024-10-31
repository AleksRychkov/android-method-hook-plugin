package io.github.aleksrychkov.methodhook.injects.injectors

import io.github.aleksrychkov.methodhook.injects.Injector
import io.github.aleksrychkov.methodhook.injects.InjectorContext
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes
import org.objectweb.asm.commons.AdviceAdapter

internal class DefaultInjector(
    private val enterInjectMethod: String?,
    private val exitInjectMethod: String?,
) : Injector {

    override fun onEnter(context: InjectorContext, mv: MethodVisitor) = with(mv) {
        if (enterInjectMethod == null) return@with

        visitLdcInsn(context.className)
        visitLdcInsn(context.methodName)
        visitLdcInsn(context.methodDescriptor)

        visitMethodInsn(
            Opcodes.INVOKESTATIC,
            enterInjectMethod.substringBeforeLast(".").replace(".", "/"),
            enterInjectMethod.substringAfterLast("."),
            "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)V",
            false,
        )
    }

    override fun onExit(
        opcode: Int,
        context: InjectorContext,
        mv: MethodVisitor,
        adviceAdapter: AdviceAdapter
    ) = with(mv) {
        if (exitInjectMethod == null) return@with

        visitLdcInsn(context.className)
        visitLdcInsn(context.methodName)
        visitLdcInsn(context.methodDescriptor)

        visitMethodInsn(
            Opcodes.INVOKESTATIC,
            exitInjectMethod.substringBeforeLast(".").replace(".", "/"),
            exitInjectMethod.substringAfterLast("."),
            "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)V",
            false,
        )
    }
}
