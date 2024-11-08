package com.team22.soundary.feature.profile.data.remote

import com.team22.soundary.core.data.dto.LabelAddRequest
import com.team22.soundary.core.data.dto.LabelView
import com.team22.soundary.core.data.dto.UserInfoResponse
import com.team22.soundary.core.data.dto.UserUpdateRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PUT
import retrofit2.http.Path

interface ProfileApiService {
    @GET("/api/v1/me")
    suspend fun getProfile(): Response<UserInfoResponse> // profile 받기

    //프로필 정보 수정
    @PUT("api/v1/me")
    suspend fun putProfile(
        @Body userUpdateRequest: UserUpdateRequest
    ) : Response<Void>

    //profile 탈퇴
    @DELETE("/api/v1/me")
    suspend fun deleteUserAccount() : Response<Void>

    //label 목록 조회
    @GET("/api/v1/labels")
    suspend fun getLabels(): Response<LabelView>

    //label 추가
    @PUT("/api/v1/labels")
    suspend fun addLabels(@Body labels: LabelAddRequest) : Response<Void>

    //label 삭제
    @DELETE("/api/v1/labels/{label}")
    suspend fun deleteLabel(@Path("label") label: String) : Response<Void>
}



