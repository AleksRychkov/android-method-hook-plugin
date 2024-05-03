package dev.aleksrychkov.methodhook.helpers.asm

import dev.aleksrychkov.methodhook.config.MethodHookConfig

class SimpleSample {

    companion object {
        val className: String = SimpleSample::class.java.name
        val methods: Set<MethodHookConfig.Method> = setOf(
            MethodHookConfig.Method("simpleMethod", "()V"),
            MethodHookConfig.Method("exceptionMethod", "()V"),
        )
    }

    @Suppress("EmptyFunctionBlock")
    fun simpleMethod() {
    }

    @Suppress("TooGenericExceptionThrown")
    fun exceptionMethod() {
        throw RuntimeException("Test")
    }
}
