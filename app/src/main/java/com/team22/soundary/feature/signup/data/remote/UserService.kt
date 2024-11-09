package com.team22.soundary.feature.signup.data.remote

import com.team22.soundary.core.data.dto.LabelAddRequest
import com.team22.soundary.core.data.dto.UserInfoInitRequestDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.PUT

interface UserService {
    @POST("/api/v1/me/default-info")
    suspend fun updateMyInfo(
        @Body userInfoInitRequestDto: UserInfoInitRequestDto
    ): Response<Unit>
}


