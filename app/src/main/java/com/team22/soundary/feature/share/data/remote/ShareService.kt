package com.team22.soundary.feature.share.data.remote

import com.team22.soundary.core.data.dto.ShareMusicRequest
import com.team22.soundary.core.data.dto.ShareMusicResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface ShareService {
    @POST("/api/v1/shared-musics")
    suspend fun requestShareMusic(
        @Body shareMusicRequest: ShareMusicRequest
    ): Response<ShareMusicResponse>
}