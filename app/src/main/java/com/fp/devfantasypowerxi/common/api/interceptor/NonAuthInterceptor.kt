package com.fp.devfantasypowerxi.common.api.interceptor

import android.util.Base64
import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException
import java.io.UnsupportedEncodingException

class NonAuthInterceptor : Interceptor {
    private val TAG = "NonAuthInterceptor"
    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val original = chain.request()
        val header = "automator_app" + ":" + "automator_app_access"
        var authHeader = ""
        try {
            val base64 = Base64.encodeToString(header.toByteArray(charset("UTF-8")), Base64.DEFAULT)
            authHeader = "Basic $base64"
        } catch (e: UnsupportedEncodingException) {
            e.printStackTrace()
        }
        // Customize the request
        val request = original.newBuilder()
            // .header("Authorization", "Basic YXV0b21hdG9yX2FwcDphdXRvbWF0b3JfYXBwX2FjY2Vzcw==")
            .method(original.method, original.body)
            .build()

        // Customize or return the response
        return chain.proceed(request)
    }
}