package dev.aleksrychkov.methodhook.helpers.asm

object TestInjector {

    val startMethod: String = TestInjector::class.java.name + ".start"
    val endMethod: String = TestInjector::class.java.name + ".end"

    private var startTestHookValue: String = ""
    private var endTestHookValue: String = ""

    @JvmStatic
    fun start(
        @Suppress("UNUSED_PARAMETER") runtimeClass: String,
        className: String,
        method: String,
    ) {
        startTestHookValue = className.replace("/", ".") + method
    }

    @JvmStatic
    fun end(
        @Suppress("UNUSED_PARAMETER") runtimeClass: String,
        className: String,
        method: String,
    ) {
        endTestHookValue = className.replace("/", ".") + method
    }

    fun startWasCalledFor(
        className: String,
        method: String,
    ): Boolean = startTestHookValue == className + method

    fun endWasCalledFor(
        className: String,
        method: String,
    ): Boolean = endTestHookValue == className + method
}
