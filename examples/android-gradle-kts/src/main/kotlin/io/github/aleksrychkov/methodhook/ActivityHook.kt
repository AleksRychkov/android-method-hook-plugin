package io.github.aleksrychkov.methodhook

import android.os.Bundle

@Suppress("unused")
object ActivityHook {

    @JvmStatic
    fun onCreateEnter(instance: Bundle?) {
        println("ActivityHook::onCreateEnter $instance")
    }

    @JvmStatic
    fun onCreateExit() {
        println("ActivityHook::onCreateExit")
    }
}