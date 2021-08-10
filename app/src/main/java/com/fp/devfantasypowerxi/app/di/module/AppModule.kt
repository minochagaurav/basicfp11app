package com.fp.devfantasypowerxi.app.di.module

import android.content.Context
import com.fp.devfantasypowerxi.app.api.MyRestHelper
import com.fp.devfantasypowerxi.common.api.RestHelper
import com.fp.devfantasypowerxi.MyApplication
import dagger.Module
import dagger.Provides
import javax.inject.Singleton


@Module
class AppModule(private val application: MyApplication) {

    @Provides
    @Singleton
    fun provideMyApplication(): MyApplication {
        return application
    }

    @Provides
    fun provideContext(): Context {
        return application.applicationContext
    }
    @Provides
    fun provideAppModule(): AppModule {
        return this
    }

    @Provides
    fun provideMyRestHelper(appModule: AppModule?): RestHelper {
        return MyRestHelper(appModule)
    }
}