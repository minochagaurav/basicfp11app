package com.fp.devfantasypowerxi.app.api

import com.fp.devfantasypowerxi.app.di.module.AppModule
import com.fp.devfantasypowerxi.common.api.RestHelper


class MyRestHelper(appModule: AppModule?) : RestHelper(appModule!!) {
    override fun logoutUser() {}
}