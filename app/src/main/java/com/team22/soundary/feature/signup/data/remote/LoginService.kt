package com.team22.soundary.feature.signup.data.remote

import com.team22.soundary.core.data.dto.LoginRequestDto
import com.team22.soundary.core.data.dto.LoginResponse
import com.team22.soundary.core.data.dto.RefreshRequestDto
import com.team22.soundary.core.data.dto.RefreshResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface LoginService {
    @POST("/api/login")
    suspend fun requestLogin(
        @Body loginRequestDto: LoginRequestDto
    ) : Response<LoginResponse>


    @POST("/api/refresh")
    suspend fun requestRefresh(
        @Body refreshRequestDto: RefreshRequestDto
    ) : Response<RefreshResponse>
}