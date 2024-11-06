package com.team22.soundary.core.domain

import com.team22.soundary.core.domain.model.Token
import com.team22.soundary.core.domain.model.User
import kotlinx.coroutines.flow.Flow

interface TokenRepository {

    suspend fun getAccessToken() : Flow<Result<String>>
    suspend fun getRefreshToken() : Flow<Result<String>>
    suspend fun saveAccessToken(token: String)
    suspend fun saveRefreshToken(token: String)
    suspend fun loginWithKakao(kakaoToken: String) : Flow<Token>

    suspend fun refresh()

    suspend fun clear()

}