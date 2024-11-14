package com.team22.soundary.feature.main.domain.di

import com.team22.soundary.feature.main.data.ReceivedShareRepositoryImpl
import com.team22.soundary.feature.main.data.SentShareRepositoryImpl
import com.team22.soundary.feature.main.domain.ReceivedShareRepository
import com.team22.soundary.feature.main.domain.SentShareRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal abstract class ShareRepositoryModule {

    @Binds
    @Singleton
    abstract fun bindSentShareRepository(impl: SentShareRepositoryImpl): SentShareRepository

    @Binds
    @Singleton
    abstract fun bindReceivedShareRepository(impl: ReceivedShareRepositoryImpl): ReceivedShareRepository
}
