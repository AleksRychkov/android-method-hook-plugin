package io.github.aleksrychkov.example

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Application
import android.content.Context
import android.os.Bundle
import android.view.Choreographer
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.FrameLayout
import android.widget.TextView
import androidx.core.view.ViewCompat
import androidx.core.view.setPadding
import java.lang.ref.WeakReference

object FrameCounter : Application.ActivityLifecycleCallbacks {

    private val tvId = ViewCompat.generateViewId()
    private val callbacksMap = mutableMapOf<String, FrameCallback>()
    private var frameCounter: Int = 0

    private fun attach(container: Activity) {
        val counterView = buildCounterView(container)
        val rootView = container.findViewById<ViewGroup>(android.R.id.content)
        if (rootView.findViewById<View?>(tvId) == null) {
            rootView.addView(counterView, 0)
        }
        FrameCallback(WeakReference(counterView)).also {
            val identityHash = Integer.toHexString(System.identityHashCode(container))
            callbacksMap[identityHash] = it
            Choreographer.getInstance().postFrameCallback(it)
        }
    }

    private fun detach(container: Activity) {
        val identityHash = Integer.toHexString(System.identityHashCode(container))
        callbacksMap[identityHash]?.let { Choreographer.getInstance().removeFrameCallback(it) }
        callbacksMap.remove(identityHash)
        val rootView = container.findViewById<ViewGroup>(android.R.id.content)
        val counterView = rootView.findViewById<View?>(tvId)
        rootView.removeView(counterView)
    }

    private fun buildCounterView(context: Context): TextView =
        TextView(context).apply {
            layoutParams = FrameLayout.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
            ).apply {
                gravity = Gravity.TOP or Gravity.END
                setPadding(10)
            }
            setBackgroundColor(0x88000000.toInt())
            setTextColor(0xFFFFFFFF.toInt())
            textSize = 14f
            id = tvId
        }

    private class FrameCallback(
        private val tvCounter: WeakReference<TextView>,
    ) : Choreographer.FrameCallback {

        @SuppressLint("SetTextI18n")
        override fun doFrame(frameTimeNanos: Long) {
            val counterView = tvCounter.get() ?: return
            frameCounter++
            counterView.text = "Frame# $frameCounter"
            logTrace()
            Choreographer.getInstance().postFrameCallback(this)
        }

        private fun logTrace() {
            android.os.Trace.beginSection("=>Frame# $frameCounter")
            android.os.Trace.endSection()
        }
    }

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
    }

    override fun onActivityStarted(activity: Activity) {
    }

    override fun onActivityResumed(activity: Activity) {
        attach(activity)
    }

    override fun onActivityPaused(activity: Activity) {
        detach(activity)
    }

    override fun onActivityStopped(activity: Activity) {
    }

    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {
    }

    override fun onActivityDestroyed(activity: Activity) {
    }
}
