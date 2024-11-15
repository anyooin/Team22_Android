package com.team22.soundary.feature.share.data.di

import com.team22.soundary.di.OtherRetrofit
import com.team22.soundary.feature.share.data.remote.ShareService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class ShareServiceModule {
    @Singleton
    @Provides
    fun provideShareApiService(@OtherRetrofit retrofit: Retrofit): ShareService =
        retrofit.create(ShareService::class.java)
}