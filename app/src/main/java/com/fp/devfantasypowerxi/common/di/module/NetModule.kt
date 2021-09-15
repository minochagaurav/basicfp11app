package com.fp.devfantasypowerxi.common.di.module

import android.content.Context
import android.util.Log
import com.fp.devfantasypowerxi.MyApplication
import com.fp.devfantasypowerxi.app.api.service.OAuthRestService
import com.fp.devfantasypowerxi.app.api.service.UserRestService
import com.fp.devfantasypowerxi.app.di.module.AppModule
import com.fp.devfantasypowerxi.common.api.CustomCallAdapter
import com.fp.devfantasypowerxi.common.api.LiveDataCallAdapterFactory
import com.fp.devfantasypowerxi.common.api.interceptor.NonAuthInterceptor
import com.fp.devfantasypowerxi.common.api.interceptor.UserAuthInterceptor
import com.fp.devfantasypowerxi.common.di.qualifiers.ClientRestServiceAuthOkHttpClient
import com.fp.devfantasypowerxi.common.di.qualifiers.RestServiceOkHttpClient
import com.fp.devfantasypowerxi.common.utils.Constants
import com.fp.devfantasypowerxi.common.utils.DateSerializer
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import okhttp3.Cache
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.*
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
class NetModule(private val baseUrl: String) {

    @Provides
    @Singleton
    fun provideHttpCache(application: MyApplication): Cache {
        val cacheSize = 10 * 1024 * 1024L
        return Cache(application.cacheDir, cacheSize)
    }

    @Provides
    @Singleton
    fun provideGson(): Gson {

        val gsonBuilder = GsonBuilder()
            .setDateFormat("yyyy-MM-dd'T'HH:mm:ss")
            .setLenient()
            .registerTypeAdapter(Date::class.java, DateSerializer())
        return gsonBuilder.create()
    }

    @Provides
    @Singleton
    fun provideHttpLoggingInterceptor(): HttpLoggingInterceptor {
        val logInterceptor = HttpLoggingInterceptor()
        // logInterceptor.level = Constants.HTTPLogLevel
        logInterceptor.level = Constants.HTTPLogLevel
        return logInterceptor
    }

    @Provides
    @Singleton
    fun provideOkhttpClient(
        cache: Cache,
        loggingInterceptor: HttpLoggingInterceptor?
    ): OkHttpClient {
        val logInterceptor = HttpLoggingInterceptor()
        logInterceptor.level = Constants.HTTPLogLevel
        val client = OkHttpClient.Builder().addInterceptor(loggingInterceptor!!)
            .addNetworkInterceptor(Interceptor { chain: Interceptor.Chain ->
                val originalRequest = chain.request()
                val jwttokenBearer =
                    "bearer " + MyApplication.preferenceDB!!.getString(Constants.SHARED_PREFERENCE_JWT_TOKEN)
              //  Log.e("token ",jwttokenBearer)
                val request = originalRequest.newBuilder()
                    .header("accept", "application/json")
                    .header("Authorization", jwttokenBearer)
                    .header("accept", Constants.ACCEPT_HEADER)
                    .header("X-app-os", "android")
                    /* .header(
                         "Authorization",
                         MyApplication.preferenceDBTwo!!.getString(Constants.HEADER_BEARER)!!
                     )*/

                    .build()
                val response = chain.proceed(request)
                response
            })
        /*   client.readTimeout(20, TimeUnit.SECONDS)
           client.writeTimeout(20, TimeUnit.SECONDS)
           client.connectTimeout(20, TimeUnit.SECONDS)*/

        client.readTimeout(1000, TimeUnit.SECONDS)
        client.writeTimeout(1000, TimeUnit.SECONDS)
        client.connectTimeout(1000, TimeUnit.SECONDS)
        client.cache(cache)
        return client.build()
    }

    @Provides
    @Singleton
    @RestServiceOkHttpClient
    fun provideRestServiceOkHttpClient(
        okHttpClient: OkHttpClient,
        appModule: AppModule?
    ): OkHttpClient {
        return okHttpClient.newBuilder().addInterceptor(UserAuthInterceptor(appModule!!)).build()
    }

    @Provides
    @Singleton
    @ClientRestServiceAuthOkHttpClient
    fun provideClientRestServiceAuthOkHttpClient(okHttpClient: OkHttpClient): OkHttpClient {
        return okHttpClient.newBuilder().addInterceptor(NonAuthInterceptor()).build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(gson: Gson): Retrofit {
        return Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create(gson)) //.addCallAdapterFactory(new LiveDataCallAdapterFactory())
            .baseUrl(baseUrl)
            .build()
    }

    @Provides
    fun provideOAuthRestService(
        @ClientRestServiceAuthOkHttpClient okHttpClient: OkHttpClient,
        context: Context
    ): OAuthRestService {
        val retrofit = Retrofit.Builder()

            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .addCallAdapterFactory(
                CustomCallAdapter.ErrorHandlingCallAdapterFactory(
                    CustomCallAdapter.MainThreadExecutor(),
                    context
                )
            )
            .build()
        return retrofit.create(OAuthRestService::class.java)
    }

    @Provides
    fun provideRestService(
        @RestServiceOkHttpClient okHttpClient: OkHttpClient,
        context: Context
    ): UserRestService {
        val retrofit = Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .addCallAdapterFactory( LiveDataCallAdapterFactory())
            .build()
        return retrofit.create(UserRestService::class.java)
    }
}