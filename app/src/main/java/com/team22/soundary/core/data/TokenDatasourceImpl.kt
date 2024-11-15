package com.team22.soundary.core.data

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import javax.inject.Inject

internal class TokenDatasourceImpl @Inject constructor(
    private val tokenDataStore: DataStore<Preferences>
) : TokenDatasource {
    private val accessTokenKey = stringPreferencesKey(ACCESS_TOKEN)
    private val refreshTokenKey = stringPreferencesKey(REFRESH_TOKEN)

    override fun getAccessToken(): Flow<Result<String>> {
        return tokenDataStore.data.map {
            it[accessTokenKey]?.let { token ->
                Result.success(token)
            } ?: Result.failure(IllegalStateException("Refresh token not found"))
        }.catch { exception ->
            emit(Result.failure(exception))
        }
    }


    override fun getRefreshToken(): Flow<Result<String>> =
        tokenDataStore.data.map {
            it[refreshTokenKey]?.let { token ->
                Result.success(token)
            } ?: Result.failure(IllegalStateException("Refresh token not found"))
        }.catch { exception ->
            emit(Result.failure(exception))
        }

    override suspend fun saveAccessToken(token: String) {
        tokenDataStore.edit {
            it[accessTokenKey] = token
        }
    }

    override suspend fun saveRefreshToken(token: String) {
        tokenDataStore.edit {
            it[refreshTokenKey] = token
        }
    }

    override suspend fun clearToken() {
        tokenDataStore.edit {
            it.clear()
        }
    }

    companion object {
        private const val ACCESS_TOKEN = "access"
        private const val REFRESH_TOKEN = "refresh"
    }
}