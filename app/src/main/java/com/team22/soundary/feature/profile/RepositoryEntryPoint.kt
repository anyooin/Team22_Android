package com.team22.soundary.feature.profile

import com.team22.soundary.feature.main.domain.ReceivedShareRepository
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@InstallIn(SingletonComponent::class)
@EntryPoint
interface RepositoryEntryPoint {
    fun receivedShareRepository(): ReceivedShareRepository
}