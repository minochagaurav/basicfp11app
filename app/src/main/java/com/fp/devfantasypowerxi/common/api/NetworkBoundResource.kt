package com.fp.devfantasypowerxi.common.api

import android.annotation.SuppressLint
import android.os.AsyncTask
import androidx.annotation.MainThread
import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import com.fp.devfantasypowerxi.common.MDApiException
import com.fp.devfantasypowerxi.common.api.Resource.Companion.loading
import com.fp.devfantasypowerxi.common.api.Resource.Companion.success

abstract class NetworkBoundResource<ResultType, RequestType> @MainThread constructor() {
    private val result = MediatorLiveData<Resource<ResultType>>()

    // Called to save the result of the API response into the database
    @WorkerThread
    protected abstract fun saveCallResult(item: RequestType)

    // Called with the data in the database to decide whether it should be
    // fetched from the network.
    @MainThread
    protected abstract fun shouldFetch(data: ResultType?): Boolean

    // Called to get the cached data from the database
    @MainThread
    protected abstract fun loadFromDb(): LiveData<ResultType>

    // Called to create the API call.
    @MainThread
    protected abstract fun createCall(): LiveData<ApiResponse<RequestType>>

    // Called when the fetch fails. The child class may want to reset components
    // like rate limiter.
    @MainThread
    protected fun onFetchFailed() {
    }

    // returns a LiveData that represents the resource
    val asLiveData: LiveData<Resource<ResultType>>
        get() = result

    private fun fetchFromNetwork(dbSource: LiveData<ResultType>) {
        val apiResponse = createCall()
        // we re-attach dbSource as a new source,
        // it will dispatch its latest value quickly
        result.addSource(dbSource) { newData -> result.value = loading(newData) }
        result.addSource(
            apiResponse
        ) { response ->
            result.removeSource(apiResponse)
            result.removeSource(dbSource)
            if (response!!.isSuccessful) {
                saveResultAndReInit(response)
            } else {
                onFetchFailed()
                val apiException = response.error as MDApiException
                result.addSource(
                    dbSource
                ) { newData: ResultType ->
                    result.setValue(
                        Resource.error(apiException, newData)
                    )
                }
            }
        }
    }
    @SuppressLint("StaticFieldLeak")
    @MainThread
    private fun saveResultAndReInit(response: ApiResponse<RequestType>?) {
        object : AsyncTask<Void, Void, Void>() {
            protected override fun doInBackground(vararg voids: Void): Void? {
                saveCallResult(response!!.body!!)
              return null
            }


            override fun onPostExecute(aVoid: Void?) {
                // we specially request a new live data,
                // otherwise we will get immediately last cached value,
                // which may not be updated with latest results received from network.
                result.addSource(loadFromDb()) { newData -> result.value = success(newData) }
            }
        }.execute()
    }

    init {
        result.value = loading(null)
        val dbSource = loadFromDb()
        result.addSource(dbSource) { data ->
            result.removeSource(dbSource)
            if (shouldFetch(data)) {
                fetchFromNetwork(dbSource)
            } else {
                result.addSource(dbSource) { newData -> result.value = success(newData) }
            }
        }
    }

}