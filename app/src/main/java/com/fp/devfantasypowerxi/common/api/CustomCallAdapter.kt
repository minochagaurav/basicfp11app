package com.fp.devfantasypowerxi.common.api

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.Log
import com.fp.devfantasypowerxi.R
import com.fp.devfantasypowerxi.common.api.ApiException.Companion.httpError
import com.fp.devfantasypowerxi.common.api.ApiException.Companion.networkError
import com.fp.devfantasypowerxi.common.api.ApiException.Companion.unexpectedError
import retrofit2.*
import java.io.IOException
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type
import java.util.concurrent.Executor

class CustomCallAdapter {
    interface CustomCallback<T> {
        fun success(response: Response<T>)
        fun failure(e: ApiException?)
    }

    interface CustomCall<T> {
        fun cancel()
        fun enqueue(callback: CustomCallback<T>)
         fun clone(): CustomCall<T>

        @Throws(IOException::class)
        fun execute(): Response<T>
    }

    class ErrorHandlingCallAdapterFactory(
        private val callbackExecutor: Executor,
        var context: Context
    ) : CallAdapter.Factory() {
        override fun get(
            returnType: Type,
            annotations: Array<Annotation>,
            retrofit: Retrofit
        ): CallAdapter<*, CustomCall<*>>? {
            if (getRawType(returnType) != CustomCall::class.java) {
                return null
            }
            check(returnType is ParameterizedType) { "MyCall must have generic type (e.g., MyCall<ResponseBody>)" }
            val responseType = getParameterUpperBound(0, returnType)
            return object : CallAdapter<R, CustomCall<*>> {
                override fun responseType(): Type {
                    return responseType
                }

                override fun adapt(call: Call<R>): CustomCall<*> {
                    return MyCallAdapter(context, call, callbackExecutor)
                }
            }
        }

        internal class MyCallAdapter<T>(
            var context: Context,
            private val call: Call<T>,
            private val callbackExecutor: Executor
        ) : CustomCall<T> {
            override fun cancel() {
                call.cancel()
            }

            override fun enqueue(callback: CustomCallback<T>) {
                call.enqueue(object : Callback<T> {
                    override fun onResponse(call: Call<T>, response: Response<T>) {
                        if (response.isSuccessful) {
                       // Log.e("url", response.raw().request.url.toString())
                            callbackExecutor.execute { callback.success(response) }

                        } else {
                            callbackExecutor.execute {
                             //   Log.e("url", response.raw().request.url.toString())
                                callback.failure(
                                    httpError(
                                        response.raw().request.url.toString(),
                                        response,
                                        context
                                    )
                                )    
                            }
                        }
                    }

                    override fun onFailure(call: Call<T>, t: Throwable) {
                        if (t is IOException) {
                            callbackExecutor.execute { callback.failure(networkError(t, context)) }
                        } else {
                          //  Log.e("getName",t.)

                            callbackExecutor.execute {
                                callback.failure(
                                    unexpectedError(
                                        t,
                                        context
                                    )
                                )
                            }
                        }
                    }
                })
            }

            override fun clone(): CustomCall<T> {
                return MyCallAdapter(context, call.clone(), callbackExecutor)
            }

            @Throws(IOException::class)
            override fun execute(): Response<T> {
                return call.execute()
            }
        }
    }

    class MainThreadExecutor : Executor {
        private val handler = Handler(Looper.getMainLooper())
        override fun execute(r: Runnable) {
            handler.post(r)
        }
    }
}