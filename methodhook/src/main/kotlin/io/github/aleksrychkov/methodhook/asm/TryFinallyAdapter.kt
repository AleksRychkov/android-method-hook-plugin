package io.github.aleksrychkov.methodhook.asm

import io.github.aleksrychkov.methodhook.injects.Injector
import io.github.aleksrychkov.methodhook.injects.InjectorContext
import io.github.aleksrychkov.methodhook.utils.Log
import org.objectweb.asm.Label
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.commons.AdviceAdapter

internal class TryFinallyAdapter(
    private val injectors: Set<Injector>,
    className: String,
    api: Int,
    mv: MethodVisitor,
    access: Int,
    name: String,
    descriptor: String,
) : AdviceAdapter(api, mv, access, name, descriptor) {

    private val injectorContext = InjectorContext(
        className = className,
        methodName = name,
        methodDescriptor = descriptor,
        access = access,
    )

    private val startFinally = Label()

    override fun visitCode() {
        super.visitCode()
        mv.visitLabel(startFinally)
    }

    override fun onMethodEnter() {
        injectOnEnter()
    }

    override fun visitMaxs(maxStack: Int, maxLocals: Int) {
        val endFinally = Label()
        mv.visitTryCatchBlock(startFinally, endFinally, endFinally, null)
        mv.visitLabel(endFinally)

        injectOnExit(opcode = ATHROW)

        mv.visitInsn(ATHROW)
        mv.visitMaxs(maxStack, maxLocals)
    }

    override fun onMethodExit(opcode: Int) {
        if (opcode != ATHROW) injectOnExit(opcode = opcode)
    }

    private fun injectOnEnter() {
        injectors.forEach {
            logInject(
                injector = it,
                injectMethod = "onEnter",
            )

            it.onEnter(
                context = injectorContext,
                mv = mv,
            )
        }
    }

    private fun injectOnExit(opcode: Int) {
        injectors.forEach {
            logInject(
                injector = it,
                injectMethod = "onExit",
            )

            it.onExit(
                opcode = opcode,
                context = injectorContext,
                mv = mv,
                adviceAdapter = this,
            )
        }
    }

    private fun logInject(injector: Injector, injectMethod: String) {
        val injectorName = injector::class.java.simpleName
        val className = injectorContext.className.replace("/", ".")
        Log.i("$injectorName->$injectMethod->$className.${injectorContext.methodName}")
    }
}
