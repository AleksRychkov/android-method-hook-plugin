package io.github.aleksrychkov.example2

@Suppress("unused")
object MethodHook2 {

    @JvmStatic
    fun start(runtimeClazz: String, clazz: String, method: String) {
        println("MethodHook2::start::$runtimeClazz::$clazz::$method")
    }

    @JvmStatic
    fun end(runtimeClazz: String, clazz: String, method: String) {
        println("MethodHook2::end::$runtimeClazz::$clazz::$method")
    }
}
