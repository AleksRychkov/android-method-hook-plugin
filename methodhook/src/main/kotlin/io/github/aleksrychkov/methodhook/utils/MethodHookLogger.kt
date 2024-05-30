package io.github.aleksrychkov.methodhook.utils

import org.gradle.api.Project

var Log: MethodHookLogger = Stub()

interface MethodHookLogger {
    companion object {
        operator fun invoke(
            p: Project,
            forceLogging: Boolean,
        ): MethodHookLogger {
            if (Log is Stub) {
                Log = Impl(p, forceLogging)
            }
            return Log
        }

        fun stub() {
            Log = Stub()
        }
    }

    fun d(msg: String)

    fun i(msg: String)

    fun w(msg: String)

    fun e(msg: String, t: Throwable)

    fun e(t: Throwable)
}

private class Impl(
    private val p: Project,
    private val forceLogging: Boolean,
) : MethodHookLogger {
    companion object {
        private const val TAG = "AndroidMethodInjector::"
    }

    override fun d(msg: String) {
        if (forceLogging) {
            println(TAG + msg)
        } else {
            p.logger.debug(TAG + msg)
        }
    }

    override fun i(msg: String) {
        if (forceLogging) {
            println(TAG + msg)
        } else {
            p.logger.info(TAG + msg)
        }
    }

    override fun w(msg: String) {
        if (forceLogging) {
            println(TAG + msg)
        } else {
            p.logger.warn(TAG + msg)
        }
    }

    override fun e(
        msg: String,
        t: Throwable,
    ) {
        if (forceLogging) {
            println(TAG + msg + ": " + t.message)
        } else {
            p.logger.error(TAG + msg, t)
        }
    }

    override fun e(t: Throwable) {
        if (forceLogging) {
            println(TAG + t.message)
        } else {
            p.logger.error(TAG, t)
        }
    }
}

private class Stub : MethodHookLogger {
    override fun d(msg: String) {
        // no-op
    }

    override fun i(msg: String) {
        // no-op
    }

    override fun w(msg: String) {
        // no-op
    }

    override fun e(
        msg: String,
        t: Throwable,
    ) {
        // no-op
    }

    override fun e(t: Throwable) {
        // no-op
    }
}
