package com.team22.soundary.feature.profile.data

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface ProfileApiService {
    @GET("profile")
    suspend fun getProfile(): ProfileResponse // profile 받기

    //label 목록 조회
    @GET("/api/v1/labels")
    suspend fun getLabels(): Response<LabelsResponse>

    //label 추가
    @POST("/api/v1/labels")
    suspend fun addLabels(@Body labels: LabelsRequest): Response<LabelsResponse>

    //label 삭제
    @DELETE("/api/v1/labels/{label}")
    suspend fun deleteLabel(@Path("label") label: String) : Response<Void>
}


//데이터 정의
data class ProfileResponse(val name: String, val email: String, val bio: String)
data class LabelsResponse(val labels: List<String>)
data class LabelsRequest(val labels: List<String>)
