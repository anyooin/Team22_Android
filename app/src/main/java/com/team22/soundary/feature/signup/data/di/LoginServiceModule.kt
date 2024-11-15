package com.team22.soundary.feature.signup.data.di

import com.team22.soundary.di.AuthRetrofit
import com.team22.soundary.feature.signup.data.remote.LoginService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object LoginServiceModule {

    @Provides
    @Singleton
    fun provideLoginService(@AuthRetrofit retrofit: Retrofit): LoginService =
        retrofit.create(LoginService::class.java)
}