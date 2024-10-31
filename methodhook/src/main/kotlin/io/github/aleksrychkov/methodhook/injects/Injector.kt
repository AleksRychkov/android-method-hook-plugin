package io.github.aleksrychkov.methodhook.injects

import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.commons.AdviceAdapter

/**
 * An interface representing an injector that allows for the modification of method behavior
 * during method entry and exit.
 */
internal interface Injector {

    /**
     * Invoked when a method is entered.
     *
     * This method allows injectors to perform actions or modify the method behavior
     * at the point of entry. Implementations should use the provided [MethodVisitor]
     * to generate the necessary bytecode for modifications.
     *
     * @param context The context in which the injector is operating, providing information about the method.
     * @param mv The [MethodVisitor] used to generate bytecode for the method being entered.
     */
    fun onEnter(context: InjectorContext, mv: MethodVisitor)

    /**
     * Invoked when a method is exited.
     *
     * This method allows injectors to perform actions or modify the method behavior
     * at the point of exit. The opcode parameter indicates how the method is exited
     * (e.g., via a return, exception, etc.). Implementations should use the provided
     * [MethodVisitor] and [AdviceAdapter] to generate the necessary bytecode for modifications.
     *
     * @param opcode The opcode representing how the method is exited (e.g., return type).
     * @param context The context in which the injector is operating, providing information about the method.
     * @param mv The [MethodVisitor] used to generate bytecode for the method being exited.
     * @param adviceAdapter The [AdviceAdapter] providing additional capabilities for bytecode manipulation.
     */
    fun onExit(
        opcode: Int,
        context: InjectorContext,
        mv: MethodVisitor,
        adviceAdapter: AdviceAdapter,
    )
}
