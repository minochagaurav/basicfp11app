package com.fp.devfantasypowerxi.common

import com.fp.devfantasypowerxi.common.api.ApiErrorModel
import com.fp.devfantasypowerxi.common.utils.NetworkUtils.isNetworkAvailable
import com.google.gson.Gson
import retrofit2.Response
import java.io.IOException

// This is RetrofitError converted to Retrofit 2
class MDApiException(
    message: String?,
    /**
     * The request URL which produced the error.
     */
    val url: String?,
    /**
     * CheckVersionCodeResponse object containing status code, headers, body, etc.
     */
    val response: Response<*>?,
    /**
     * The event kind which triggered this error.
     */
    val kind: Kind, exception: Throwable?, isNetworkError: Boolean
) : RuntimeException(message, exception) {
    private var errorModel: ApiErrorModel? = null

    /**
     * HTTP response body converted to specified `type`. `null` if there is no
     * response.
     *
     * @throws IOException if unable to convert the body to the specified `type`.
     */
    /*
    public <T> T getErrorBodyAs(Class<T> type, ResponseBody responseBody) throws IOException, RuntimeException {
        Gson gson = new Gson();
        ApiErrorModel errorModel = gson.fromJson(responseBody.toString(), type);
        Converter<ResponseBody, T> converter =  GsonConverterFactory.create().responseBodyConverter(type, new Annotation[0]);//MyApplication.getRestClient().getRetrofitInstance().responseBodyConverter(type, new Annotation[0]);
        try {
            return converter.convert(responseBody);
        } catch (Exception e) {
            throw new RuntimeException();
        }
    }
*/
    fun getErrorModel(): ApiErrorModel {
      /*  TODO: Check for null if null Send a mock ApiErrorModel saying unexpected happen*/
        if (errorModel != null) {
            return errorModel as ApiErrorModel
        }
        return if (response == null || response.errorBody() == null) {
            ApiErrorModel("Unable to connect. The app can not connect to the server. Please try again")
        } else try {
            val gson = Gson()
            val apiErrorModel = gson.fromJson(
                response.errorBody()!!.string(), ApiErrorModel::class.java
            )
            apiErrorModel.prepareApiErrorMessage()
            if (apiErrorModel.errorMessage == null && apiErrorModel.errorCode == null || apiErrorModel.errorMessage!!.isEmpty() && apiErrorModel.errorCode!!.isEmpty()) {
                //TODO: Report this incident somewhere
                ApiErrorModel("Sorry, seems Internet connection lost, we are facing problem with server connectivity")
            } else apiErrorModel
        } catch (e1: Exception) {
            e1.printStackTrace()
            ApiErrorModel("Unable to connect. The app can not connect to the server")
        }
    }

    val errorMessage: String
        get() = if (kind == Kind.HTTP || kind == Kind.NETWORK) {
            val apiErrorModel = getErrorModel()
            apiErrorModel.prepareApiErrorMessage()
            apiErrorModel.getErrorMessage()
        } else {
            "Unexpected Error"
        }

    /**
     * Identifies the event kind which triggered a [MDApiException].
     */
    enum class Kind {
        /**
         * An [IOException] occurred while communicating to the server.
         */
        NETWORK,

        /**
         * A non-200 HTTP status code was received from the server.
         */
        HTTP,

        /**
         * An internal error occurred while attempting to execute a request. It is best practice to
         * re-throw this exception so your application crashes.
         */
        UNEXPECTED
    }

    companion object {
        fun httpError(url: String?, response: Response<*>): MDApiException {
            val message = response.code().toString() + " " + response.message()
            return MDApiException(message, url, response, Kind.HTTP, null, false)
        }

        fun networkError(exception: IOException?): MDApiException {
            return if (isNetworkAvailable) {
                MDApiException(
                    "Connection timeout! Please try again and check your internet connectivity.",
                    null,
                    null,
                    Kind.NETWORK,
                    exception,
                    true
                )
            } else MDApiException(
                "Internet connection appears to be offline, Please check your connection",
                null,
                null,
                Kind.NETWORK,
                exception,
                true
            )
        }

        fun unexpectedError(exception: Throwable): MDApiException {
            return MDApiException(exception.message, null, null, Kind.UNEXPECTED, exception, false)
        }
    }

    init {
        if (isNetworkError) {
            errorModel = ApiErrorModel(message)
        }
    }
}