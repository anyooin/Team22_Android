package com.team22.soundary.feature.share.domain

import com.team22.soundary.core.data.dto.ShareMusicResponse
import kotlinx.coroutines.flow.Flow

interface ShareRepository {
    suspend fun shareMusic(
        platformTrackId: String,
        comment: String,
        userList: List<String>
    ) : Flow<Result<ShareMusicResponse>>
}