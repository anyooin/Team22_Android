package com.team22.soundary.feature.signup.data.remote

import com.team22.soundary.core.data.dto.LabelAdd
import com.team22.soundary.core.data.dto.UserInfoDto
import com.team22.soundary.core.data.dto.UserInfoInitRequestDto
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.PUT

interface UserService {
    @POST("/api/v1/me/default-info")
    suspend fun updateMyInfo(
        @Body userInfoInitRequestDto: UserInfoInitRequestDto
    ) : Response<Unit>

    @PUT("/api/v1/labels")
    suspend fun addLabels(@Body labels: LabelAdd) : Response <Void>
}


