package dev.aleksrychkov.methodhook.helpers.asm

import dev.aleksrychkov.methodhook.config.MethodHookConfig

class SimpleSample {

    companion object {
        val className: String = SimpleSample::class.java.name
        val methods: Set<MethodHookConfig.Method> = setOf(
            MethodHookConfig.Method("simpleMethod"),
            MethodHookConfig.Method("exceptionMethod"),
        )
    }

    @Suppress("EmptyFunctionBlock", "unused")
    fun simpleMethod() {
    }

    @Suppress("TooGenericExceptionThrown", "unused")
    fun exceptionMethod() {
        throw RuntimeException("Test")
    }
}
