package io.github.aleksrychkov.methodhook

import android.annotation.SuppressLint
import okhttp3.Interceptor
import okhttp3.Response

@Suppress("unused")
object OkhttpHook {

    @SuppressLint("NewApi")
    @JvmStatic
    fun enterIntercept(chain: Interceptor.Chain) {
        println("OkhttpHook::enterIntercept::${chain.request().url} ${Thread.currentThread().id}")

        android.os.Trace.beginSection("=>Request: ${chain.request().url}".takeLast(127))
    }

    @SuppressLint("NewApi")
    @JvmStatic
    fun exitIntercept(response: Any?) {
        println("OkhttpHook::exitIntercept::${response} ${Thread.currentThread().id}")
        if (response is Response) {
            response.peekBody(Long.MAX_VALUE).string()
        }
        android.os.Trace.endSection()
    }
}
