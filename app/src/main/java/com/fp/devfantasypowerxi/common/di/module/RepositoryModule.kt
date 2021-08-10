package com.fp.cricbull.common.di.module

import dagger.Module
import dagger.Provides
import java.util.concurrent.Executor
import javax.inject.Singleton

@Module
class RepositoryModule {
    @Provides
    @Singleton
    fun provideExecutor(): Executor {
        return Executor { r -> Thread(r).start() }
    }

}