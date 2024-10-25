package com.team22.soundary.core.data

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class TokenDatasource @Inject constructor(
    private val tokenDataStore: DataStore<Preferences>
) {

    private val accessTokenKey = stringPreferencesKey(ACCESS_TOKEN)
    private val refreshTokenKey = stringPreferencesKey(REFRESH_TOKEN)

    fun getAccessToken() : Flow<Result<String>> =
        tokenDataStore.data.map{
            it[accessTokenKey]?.let{ token ->
                Result.success(token)
            } ?: Result.failure(IllegalStateException("Refresh token not found"))
        }.catch{exception ->
            emit(Result.failure(exception))
        }


    fun getRefreshToken(): Flow<Result<String>> =
        tokenDataStore.data.map{
            it[refreshTokenKey]?.let{ token ->
                Result.success(token)
            } ?: Result.failure(IllegalStateException("Refresh token not found"))
        }.catch{exception ->
            emit(Result.failure(exception))
        }

    suspend fun saveAccessToken(token : String) {
        tokenDataStore.edit{
            it[accessTokenKey] = token
        }
    }

    suspend fun saveRefreshToken(token: String) {
        tokenDataStore.edit{
            it[refreshTokenKey] = token
        }
    }

    companion object{
        private const val ACCESS_TOKEN = "access"
        private const val REFRESH_TOKEN = "refresh"
    }
}