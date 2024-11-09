package com.team22.soundary.feature.profile.data.remote

import com.team22.soundary.core.data.dto.LabelAddRequest
import com.team22.soundary.core.data.dto.UserInfoDto
import com.team22.soundary.core.data.dto.UserUpdateRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PUT

interface ProfileApiService {
    // profile 받기
    @GET("/api/v1/me")
    suspend fun getProfile(): Response<UserInfoDto>

    //프로필 정보 수정
    @PUT("api/v1/me")
    suspend fun putProfile(
        @Body userUpdateRequest: UserUpdateRequest
    ): Response<Unit>

    //profile 탈퇴
    @DELETE("/api/v1/me")
    suspend fun deleteUserAccount(): Response<Unit>

    //label 설정
    @PUT("/api/v1/labels")
    suspend fun setLabels(@Body labels: LabelAddRequest): Response<Unit>
}



