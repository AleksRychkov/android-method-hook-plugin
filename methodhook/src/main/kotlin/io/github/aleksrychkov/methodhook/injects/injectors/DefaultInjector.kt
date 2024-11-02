package io.github.aleksrychkov.methodhook.injects.injectors

import io.github.aleksrychkov.methodhook.injects.Injector
import io.github.aleksrychkov.methodhook.injects.InjectorContext
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes
import org.objectweb.asm.commons.AdviceAdapter

/**
 * An implementation of the [Injector] interface that allows for injecting behavior at the
 * entry and exit points of a method.
 *
 * The [DefaultInjector] uses specified methods to inject behavior when a method is entered
 * and exited. It dynamically invokes the provided methods with the class name, method name,
 * and method descriptor as parameters.
 *
 * @param enterInjectMethod The fully qualified name of the method to be invoked upon entering
 *        the target method. If null, no injection occurs on entry.
 * @param exitInjectMethod The fully qualified name of the method to be invoked upon exiting
 *        the target method. If null, no injection occurs on exit.
 */
internal class DefaultInjector(
    private val enterInjectMethod: String?,
    private val exitInjectMethod: String?,
) : Injector {

    override fun onEnter(context: InjectorContext, mv: MethodVisitor) = with(mv) {
        if (enterInjectMethod == null) return@with

        visitLdcInsn(context.className.replace("/", "."))
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

        visitLdcInsn(context.className.replace("/", "."))
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
