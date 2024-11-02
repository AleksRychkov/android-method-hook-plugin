package io.github.aleksrychkov.methodhook

import android.app.Application
import android.content.Context
import io.github.aleksrychkov.example.FrameCounter

@Suppress("unused")
object FrameCounterHook {

    private var isRegistered = false

    @JvmStatic
    fun attachBaseContext(context: Context) {
        if (isRegistered) return
        isRegistered = true
        (context.applicationContext as Application).registerActivityLifecycleCallbacks(FrameCounter)
    }
}
