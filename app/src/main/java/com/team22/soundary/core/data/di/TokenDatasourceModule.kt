package com.team22.soundary.core.data.di

import com.team22.soundary.core.data.TokenDatasource
import com.team22.soundary.core.data.TokenDatasourceImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal abstract class TokenDatasourceModule {

    @Binds
    @Singleton
    abstract fun bindTokenDatasource(impl: TokenDatasourceImpl) : TokenDatasource
}