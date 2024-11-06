package com.team22.soundary.feature.main.data.remote

import com.team22.soundary.core.data.dto.ReceivedShareListDto
import com.team22.soundary.core.data.dto.SentShareListDto
import retrofit2.Response
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface ShareService {
    @GET("/api/v1/shared-musics/sent")
    suspend fun requestSentShare(): Response<SentShareListDto>

    @GET("/api/v1/shared-musics/received")
    suspend fun requestReceiveShare(): Response<ReceivedShareListDto>

    @POST("/api/v1/shared-musics/received/{musicId}/likes")
    suspend fun requestMusicLike(
        @Path("musicId") musicId : String
    ) : Response<Unit>

    @DELETE("/api/v1/shared-musics/received/{musicId}/likes")
    suspend fun deleteMusicLike(
        @Path("musicId") musicId : String
    ) : Response<Unit>

}