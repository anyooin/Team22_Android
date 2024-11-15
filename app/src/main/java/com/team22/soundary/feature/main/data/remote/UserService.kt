package com.team22.soundary.feature.main.data.remote

import com.team22.soundary.core.data.dto.UserInfoDto
import retrofit2.Response
import retrofit2.http.GET

interface UserService {
    @GET("/api/v1/me")
    suspend fun requestMyInfo(): Response<UserInfoDto>

}