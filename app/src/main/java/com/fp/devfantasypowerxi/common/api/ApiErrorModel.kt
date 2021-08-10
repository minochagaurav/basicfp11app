package com.fp.devfantasypowerxi.common.api

import com.google.gson.annotations.SerializedName

class ApiErrorModel(@SerializedName("error_message") var errorMessage: String?) {

    @SerializedName("error_code")
    var errorCode: String?

    //OAuth Keys
    var error: String? = null
    @JvmName("getErrorMessage1")
    fun getErrorMessage(): String {
        return "Unable to Authenticate\n Please wait a few minutes and try again."
    }
    fun prepareApiErrorMessage() {
        if (error != null && error!!.isNotEmpty()) {
            errorMessage = getErrorMessage()
            errorCode = error
        }
    }

    init {
        errorCode = ""
    }
}