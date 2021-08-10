package com.fp.devfantasypowerxi.common.api.interceptor


import com.fp.devfantasypowerxi.app.di.module.AppModule
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import java.io.IOException

class UserAuthInterceptor     // Better to have these as constructor arguments
    (var appModule: AppModule) : Interceptor {
    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val original: Request = chain.request()

        // Customize the request
        val request =
            original.newBuilder() //.header("Authorization", "Bearer " + this.tinyDB.getString(Constants.SHARED_PREFERENCES_ACCESS_TOKEN))
                //.header("registeredDeviceId", appModule.provideDeviceId())
                .build()
        // Customize or return the response
        return chain.proceed(request)
    }

    fun randomWithRange(min: Int, max: Int): Int {
        val range = max - min + 1
        return (Math.random() * range).toInt() + min
    }
}