package com.team22.soundary.feature.signup.data.remote

import com.team22.soundary.core.data.dto.ImageUpLoadResponse
import com.team22.soundary.core.data.dto.LabelAddRequest
import com.team22.soundary.core.data.dto.UserInfoInitRequestDto
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part

interface UserService {
    @POST("/api/v1/me/default-info")
    suspend fun updateMyInfo(
        @Body userInfoInitRequestDto: UserInfoInitRequestDto
    ): Response<Unit>

    @Multipart
    @POST("/api/v1/images")
    suspend fun uploadImageToServer(
        @Part image: MultipartBody.Part
    ): Response<ImageUpLoadResponse>

    @POST("/api/v1/images")
    suspend fun uploadImageToServer(
        @Body image: String
    ): Response<ImageUpLoadResponse>


}


