package io.github.aleksrychkov.example

import android.app.Service
import android.content.Intent
import android.os.IBinder

class MainService : Service(), AutoTrace {

    override fun onCreate() {
        super.onCreate()
    }

    @Suppress("UNUSED_VARIABLE")
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val clazz = this::class.java.name
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onBind(intent: Intent?): IBinder? = null
}
