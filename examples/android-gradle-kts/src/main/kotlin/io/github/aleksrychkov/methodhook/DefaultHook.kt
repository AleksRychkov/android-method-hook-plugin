package io.github.aleksrychkov.methodhook

@Suppress("unused")
object DefaultHook {

    @JvmStatic
    fun enter(clazz: String, method: String, descriptor: String) {
        println("DefaultHook::enter::$clazz.$method.$descriptor")
    }

    @JvmStatic
    fun exit(clazz: String, method: String, descriptor: String) {
        println("DefaultHook::exit::$clazz.$method.$descriptor")
    }
}
