package com.fp.devfantasypowerxi.common.api

import com.fp.devfantasypowerxi.common.MDApiException

class Resource<T> private constructor(val status: Status, data: T?, exception: MDApiException?) {
    enum class Status {
        SUCCESS, ERROR, LOADING
    }

    val data: T?
    val exception: MDApiException?

    companion object {
        @JvmStatic
        fun <T> success(data: T?): Resource<T> {
            return Resource(Status.SUCCESS, data, null)
        }

        @JvmStatic
        fun <T> error(exception: MDApiException?, data: T?): Resource<T> {
            return Resource(Status.ERROR, data, exception)
        }
        @JvmStatic
        fun <T> loading( data : T?): Resource<T> {
            return Resource(Status.LOADING, data, null)
        }
    }

    init {
        this.data = data
        this.exception = exception
    }
}