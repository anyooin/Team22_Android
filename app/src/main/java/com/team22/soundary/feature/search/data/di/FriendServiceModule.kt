package com.team22.soundary.feature.search.data.di

import com.team22.soundary.di.OtherRetrofit
import com.team22.soundary.feature.search.data.remote.FriendApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import retrofit2.Retrofit

@Module
@InstallIn(SingletonComponent::class)
object FriendServiceModule {

    @Provides
    @Singleton
    fun provideFriendService(@OtherRetrofit retrofit: Retrofit): FriendApiService =
        retrofit.create(FriendApiService::class.java)

}
