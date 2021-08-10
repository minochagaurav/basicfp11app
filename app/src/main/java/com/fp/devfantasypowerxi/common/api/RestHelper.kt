package com.fp.devfantasypowerxi.common.api


import com.fp.devfantasypowerxi.app.api.service.OAuthRestService
import com.fp.devfantasypowerxi.app.api.service.UserRestService
import com.fp.devfantasypowerxi.app.di.module.AppModule
import javax.inject.Inject

abstract class RestHelper(appModule: AppModule) {


    @Inject
    lateinit var oAuthRestService: OAuthRestService

    @Inject
    lateinit var userRestService: UserRestService


    abstract fun logoutUser()
    init {
        appModule.provideMyApplication().getComponent()?.inject(this)
      //  appModule.provideMyApplication().getAppComponentSecond()?.inject(this)
    }
}