package com.team22.soundary.feature.share.data.di

import com.team22.soundary.di.OtherRetrofit
import com.team22.soundary.feature.share.data.remote.MusicService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object MusicServiceModule {
    @Singleton
    @Provides
    fun provideMusicApiService(@OtherRetrofit retrofit: Retrofit): MusicService =
        retrofit.create(MusicService::class.java)
}