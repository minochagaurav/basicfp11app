package com.fp.devfantasypowerxi.common.api

import android.content.Context
import android.util.Log
import com.fp.devfantasypowerxi.R
import com.fp.devfantasypowerxi.common.utils.NetworkUtils
import com.google.gson.Gson
import retrofit2.Response
import java.io.IOException

// This is RetrofitError converted to Retrofit 2
class ApiException(
    context: Context, message: String?,
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
) :
    RuntimeException(message, exception) {

    private var errorModel: ApiErrorModel? = null
    private val context: Context

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
        //TODO: Check for null if null Send a mock ApiErrorModel saying unexpected happen
        if (errorModel != null) {
            return errorModel!!
        }
        return if (response?.errorBody() == null) {
            ApiErrorModel(context.getString(R.string.server_unreachable))
        } else try {
            val gson = Gson()
            val apiErrorModel = gson.fromJson(
                response.errorBody()!!.string(),
                ApiErrorModel::class.java
            )
            apiErrorModel.prepareApiErrorMessage()
            Log.d(
                "APIException ------- : ",
                apiErrorModel.errorMessage.toString() + " " + apiErrorModel.errorCode
            )
            if (apiErrorModel.errorMessage == null && apiErrorModel.errorCode == null || apiErrorModel.errorMessage!!.isEmpty() && apiErrorModel.errorCode!!.isEmpty()) {
                //TODO: Report this incident somewhere
                ApiErrorModel("Sorry, seems Internet connection lost, we are facing problem with server connectivity.")
            } else apiErrorModel
        } catch (e1: Exception) {
            e1.printStackTrace()
            ApiErrorModel(context.getString(R.string.server_unreachable))
        }
    }

    /**
     * Identifies the event kind which triggered a [ApiException].
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
        fun httpError(url: String?, response: Response<*>, context: Context): ApiException {
            val message = response.code().toString() + " " + response.message()
            return ApiException(context, message, url, response, Kind.HTTP, null, false)
        }

        fun networkError(exception: IOException?, context: Context): ApiException {
            return if (NetworkUtils.isNetworkAvailable(context)) {
                ApiException(
                    context,
                    context.getString(R.string.time_out),
                    null,
                    null,
                    Kind.NETWORK,
                    exception,
                    true
                )
            } else ApiException(
                context,
                context.getString(R.string.internet_off),
                null,
                null,
                Kind.NETWORK,
                exception,
                true
            )
        }

        fun unexpectedError(exception: Throwable, context: Context): ApiException {
            return ApiException(
                context,
                exception.message,
                null,
                null,
                Kind.UNEXPECTED,
                exception,
                false
            )
        }
    }

    init {
        if (isNetworkError) {
            errorModel = ApiErrorModel(message)
        }
        this.context = context
    }
}