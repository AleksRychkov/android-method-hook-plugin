package io.github.aleksrychkov.methodhook.injects.injectors

import io.github.aleksrychkov.methodhook.injects.Injector
import io.github.aleksrychkov.methodhook.injects.InjectorContext
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes
import org.objectweb.asm.commons.AdviceAdapter

object TraceInjector : Injector {

    private const val TRACE_MSG_LENGTH = 127
    private const val TRACE_MSG_PREFIX = "=>"

    override fun onEnter(context: InjectorContext, mv: MethodVisitor) {
        val className = context.className.substringAfterLast("/")
        val msg = "$TRACE_MSG_PREFIX${className}.${context.methodName}${context.methodDescriptor}"
            .takeLast(TRACE_MSG_LENGTH)

        mv.visitLdcInsn(msg)
        mv.visitMethodInsn(
            Opcodes.INVOKESTATIC,
            "android/os/Trace",
            "beginSection",
            "(Ljava/lang/String;)V",
            false,
        )

    }

    override fun onExit(
        opcode: Int,
        context: InjectorContext,
        mv: MethodVisitor,
        adviceAdapter: AdviceAdapter
    ) {
        mv.visitMethodInsn(
            Opcodes.INVOKESTATIC,
            "android/os/Trace",
            "endSection",
            "()V",
            false,
        )
    }
}
