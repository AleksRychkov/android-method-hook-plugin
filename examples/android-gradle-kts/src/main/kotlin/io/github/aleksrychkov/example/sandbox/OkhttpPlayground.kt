package io.github.aleksrychkov.example.sandbox

import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.io.IOException

class OkhttpPlayground : Playground {

    override fun test() {
        val okHttp = OkHttpClient()
        val request: Request = Request.Builder()
            .url("https://baconipsum.com/api/?type=meat-and-filler")
            .build()

        okHttp.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
            }

            override fun onResponse(call: Call, response: Response) {
            }
        })
    }
}