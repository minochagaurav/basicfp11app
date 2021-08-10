package com.fp.devfantasypowerxi.common.api

import android.util.Log
import com.google.gson.Gson
import retrofit2.Response
import java.io.IOException

class ApiResponse<T> {
    val code: Int
    var   body: T?
    val error: Throwable?

    constructor(error: Throwable?) {
        code = 500
        body = null
        this.error = error
    }

    constructor(response: Response<T>) {
        code = response.code()
        if (response.isSuccessful) {
            body = response.body()
            error = null
        } else {
            var message: String? = null
            if (response.errorBody() != null) {
                // try {
                message = getErrorModel(response).errorMessage //response.errorBody().string();
                //                } catch (IOException ignored) {
//                    Log.e("ERROR", "error while parsing response", ignored);
//                }
            }
            if (message == null || message.trim { it <= ' ' }.length == 0) {
                message = response.message()
            }
            error = IOException(message)
            body = null
        }
    }

    val isSuccessful: Boolean
        get() = code in 200..299

    private fun getErrorModel(response: Response<T>?): ApiErrorModel {
        return if (response?.errorBody() == null) {
            ApiErrorModel("unreachable")
        } else try {
            val gson = Gson()
            val apiErrorModel = gson.fromJson(
                response.errorBody()!!.string(), ApiErrorModel::class.java
            )
            apiErrorModel.prepareApiErrorMessage()
            Log.d(
                "APIException ------- : ",
                apiErrorModel.errorMessage + " " + apiErrorModel.errorCode
            )
            if (apiErrorModel.errorMessage == null && apiErrorModel.errorCode == null || apiErrorModel.errorMessage!!.isEmpty() && apiErrorModel.errorCode!!.isEmpty()) {
                ApiErrorModel("Sorry, seems Internet connection lost, we are facing problem with server connectivity")
            } else apiErrorModel
        } catch (e1: Exception) {
            e1.printStackTrace()
            ApiErrorModel("unreachable")
        }
    }
}