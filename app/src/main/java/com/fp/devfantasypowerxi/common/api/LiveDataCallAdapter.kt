package com.fp.devfantasypowerxi.common.api

import androidx.lifecycle.LiveData
import com.fp.devfantasypowerxi.common.MDApiException
import retrofit2.Call
import retrofit2.CallAdapter
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException
import java.lang.reflect.Type
import java.util.concurrent.atomic.AtomicBoolean

/**
 * A Retrofit adapterthat converts the Call into a LiveData of ApiResponse.
 * @param <R>
</R> */
internal class LiveDataCallAdapter<R>(private val responseType: Type) :
    CallAdapter<R, LiveData<ApiResponse<R>>> {
    override fun responseType(): Type {
        return responseType
    }

    override fun adapt(call: Call<R>): LiveData<ApiResponse<R>> {
        return object : LiveData<ApiResponse<R>>() {
            var started = AtomicBoolean(false)
            override fun onActive() {
                super.onActive()
                if (started.compareAndSet(false, true)) {
                    call.enqueue(object : Callback<R> {
                        override fun onResponse(call: Call<R>, response: Response<R>) {
                            if (response.isSuccessful) {
                                postValue(ApiResponse(response))
                            }
                        }

                        override fun onFailure(call: Call<R>, throwable: Throwable) {
                            if (throwable is IOException) {
                                postValue(ApiResponse(MDApiException.networkError(throwable)))
                            } else {
                                postValue(ApiResponse(MDApiException.unexpectedError(throwable)))
                            }
                        }
                    })
                }
            }
        }
    }
}