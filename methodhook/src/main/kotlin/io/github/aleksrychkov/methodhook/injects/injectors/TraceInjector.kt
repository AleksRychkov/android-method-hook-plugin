package io.github.aleksrychkov.methodhook.injects.injectors

import io.github.aleksrychkov.methodhook.injects.Injector
import io.github.aleksrychkov.methodhook.injects.InjectorContext
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes
import org.objectweb.asm.commons.AdviceAdapter

/**
 * An implementation of the [Injector] interface that injects tracing behavior
 * at the entry and exit points of a method using Android's tracing APIs.
 *
 * The [TraceInjector] injects method tracing calls to `android/os/Trace`, which
 * allows tracking the execution of methods in an Android application. When a target
 * method is entered, it begins a new trace section, and when the method is exited,
 * it ends the trace section.
 *
 * @param msgPrefix - Prefix of trace's section name.
 */
internal class TraceInjector(
    private val msgPrefix: String? = null
) : Injector {

    private companion object {
        const val TRACE_MSG_LENGTH = 127
        const val TRACE_MSG_PREFIX = ""
    }

    override fun onEnter(context: InjectorContext, mv: MethodVisitor) {
        val prefix = msgPrefix ?: TRACE_MSG_PREFIX
        val className = context.className.substringAfterLast("/")
        val method = context.methodName
        val descriptor = context.methodDescriptor.stripPackages()
        val msg = "${prefix}${className}.${method}${descriptor}".take(TRACE_MSG_LENGTH)

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

    private fun String.stripPackages(): String {
        return this.replace(Regex("L[^;]+/"), "L")
            .replace("(Ljava/lang/Void;)", "()")
            .replace("()V", "()")
    }
}
