package dev.aleksrychkov.example

object MethodHook {

    @JvmStatic
    fun start(runtimeClazz: String, clazz: String, method: String) {
        println("MethodHook::start::$runtimeClazz::$clazz::$method")
    }

    @JvmStatic
    fun end(runtimeClazz: String, clazz: String, method: String) {
        println("MethodHook::end::$runtimeClazz::$clazz::$method")
    }
}
