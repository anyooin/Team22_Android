package com.team22.soundary.core.auth.di

import com.team22.soundary.core.data.TokenRepositoryImpl
import com.team22.soundary.core.domain.TokenRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class TokenRepositoryModule {

    @Binds
    @Singleton
    abstract fun bindTokenRepository(impl: TokenRepositoryImpl) : TokenRepository
}