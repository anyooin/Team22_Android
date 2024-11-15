package com.team22.soundary.feature.share.domain

interface ShareRepository {
    suspend fun shareMusic(
        platformTrackId: String,
        trackId: String,
        comment: String,
        userList: List<String>
    )
}