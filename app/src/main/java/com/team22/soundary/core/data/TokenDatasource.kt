package com.team22.soundary.core.data

import kotlinx.coroutines.flow.Flow

interface TokenDatasource {
    fun getAccessToken() : Flow<Result<String>>
    fun getRefreshToken() : Flow<Result<String>>
    suspend fun saveAccessToken(token: String)
    suspend fun saveRefreshToken(token: String)
}