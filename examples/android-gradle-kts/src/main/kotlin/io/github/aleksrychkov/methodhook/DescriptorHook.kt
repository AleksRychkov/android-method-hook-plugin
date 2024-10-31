package io.github.aleksrychkov.methodhook

@Suppress("unused")
object DescriptorHook {

    @JvmStatic
    fun enterFoo(param1: String, param2: Int) {
        println("DescriptorHook::enterFoo $param1 $param2")
    }

    @JvmStatic
    fun exitFoo(result: Any?) {
        println("DescriptorHook::exitFoo $result")
    }

    @JvmStatic
    fun enterLong() {
        println("DescriptorHook::enterLong")
    }

    @JvmStatic
    fun exitLong(result: Any?) {
        println("DescriptorHook::exitLong $result")
    }

    @JvmStatic
    fun enterAny(any: Any) {
        println("DescriptorHook::enterAny $any")
    }

    @JvmStatic
    fun exitAny(result: Any?) {
        println("DescriptorHook::exitAny $result")
    }

    @JvmStatic
    fun enterInt(p: Int) {
        println("DescriptorHook::enterInt $p")
    }

    @JvmStatic
    fun exitInt(result: Any?) {
        println("DescriptorHook::exitInt $result")
    }

    @JvmStatic
    fun enterVoid() {
        println("DescriptorHook::enterVoid")
    }

    @JvmStatic
    fun exitVoid(result: Any?) {
        println("DescriptorHook::exitVoid $result")
    }
}