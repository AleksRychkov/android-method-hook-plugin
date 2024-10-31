package io.github.aleksrychkov.methodhook

@Suppress("unused")
object MethodHook {

    @JvmStatic
    fun enter(clazz: String, method: String, descriptor: String) {
        println("MethodHook::enter ${clazz}.${method}${descriptor}")
    }

    @JvmStatic
    fun exit(clazz: String, method: String, descriptor: String) {
        println("MethodHook::exit ${clazz}.${method}${descriptor}")
    }
}
