package com.team22.soundary.feature.profile.data.di

import com.team22.soundary.di.OtherRetrofit
import com.team22.soundary.feature.profile.data.remote.ProfileApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ProfileServiceModule {
    @Provides
    @Singleton
    fun provideProfileService(@OtherRetrofit retrofit: Retrofit): ProfileApiService =
        retrofit.create(ProfileApiService::class.java)

}
