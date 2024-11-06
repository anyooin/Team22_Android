package com.team22.soundary.feature.main.domain

import javax.inject.Inject

class LikeSongUseCase @Inject constructor(
    private val receivedShareRepository: ReceivedShareRepository
) {
    suspend fun like(musicId: String) {
        receivedShareRepository.likeMusic(musicId)
    }

    suspend fun dislike(musicId: String){
        receivedShareRepository.deleteLikeMusic(musicId)
    }
}