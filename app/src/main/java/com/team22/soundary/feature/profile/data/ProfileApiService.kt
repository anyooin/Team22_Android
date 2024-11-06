package com.team22.soundary.feature.profile.data

import com.team22.soundary.core.data.dto.LabelAdd
import com.team22.soundary.core.data.dto.LabelView
import com.team22.soundary.core.data.dto.UserInfoResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface ProfileApiService {
    @GET("profile")
    suspend fun getProfile(): UserInfoResponse // profile 받기

    //label 목록 조회
    @GET("/api/v1/labels")
    suspend fun getLabels(): Response<LabelView>

    //label 추가
    @POST("/api/v1/labels")
    suspend fun addLabels(@Body labels: LabelAdd): Response<LabelAdd>

    //label 삭제
    @DELETE("/api/v1/labels/{label}")
    suspend fun deleteLabel(@Path("label") label: String) : Response<Void>
}



