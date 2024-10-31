package io.github.aleksrychkov.methodhook.injects

import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.commons.AdviceAdapter

interface Injector {
    fun onEnter(context: InjectorContext, mv: MethodVisitor)
    fun onExit(
        opcode: Int,
        context: InjectorContext,
        mv: MethodVisitor,
        adviceAdapter: AdviceAdapter,
    )
}
