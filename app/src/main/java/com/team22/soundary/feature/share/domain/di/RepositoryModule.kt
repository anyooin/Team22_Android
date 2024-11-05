package com.team22.soundary.feature.share.domain.di

import com.team22.soundary.feature.share.data.MusicRepositoryImpl
import com.team22.soundary.feature.share.data.ShareRepositoryImpl
import com.team22.soundary.feature.share.domain.MusicRepository
import com.team22.soundary.feature.share.domain.ShareRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped

@Module
@InstallIn(ViewModelComponent::class)
abstract class RepositoryModule {

    @Binds
    @ViewModelScoped
    abstract fun bindMusicRepository(
        musicRepositoryImpl: MusicRepositoryImpl
    ): MusicRepository

    @Binds
    @ViewModelScoped
    abstract fun bindShareRepository(
        shareRepositoryImpl: ShareRepositoryImpl
    ): ShareRepository

}