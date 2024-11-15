package com.team22.soundary.feature.share.data.remote

import com.team22.soundary.core.data.dto.MostLikedTracksResponse
import com.team22.soundary.core.data.dto.MostSharedTracksResponse
import com.team22.soundary.core.data.dto.SearchTrackResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface MusicService {
    @GET("/api/v1/tracks")
    suspend fun requestMusicList(
        @Query("platform") platform: String = "SPOTIFY",
        @Query("query") query: String
    ): Response<SearchTrackResponse>

    @GET("/api/v1/shared-musics/statistics/most-shared-tracks")
    suspend fun requestMostSharedMusicList(): Response<MostSharedTracksResponse>

    @GET("/api/v1/shared-musics/statistics/most-liked-tracks")
    suspend fun requestMostLikedMusicList(): Response<MostLikedTracksResponse>

}