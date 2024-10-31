package com.team22.soundary.core.data

import com.team22.soundary.core.IODispatcher
import com.team22.soundary.core.domain.TokenRepository
import com.team22.soundary.core.dto.LoginRequestDto
import com.team22.soundary.core.domain.model.Token
import com.team22.soundary.feature.signup.data.remote.LoginService
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import java.lang.IllegalStateException
import javax.inject.Inject

class TokenRepositoryImpl @Inject constructor(
    @IODispatcher private val dispatcher: CoroutineDispatcher,
    private val tokenDataStore: TokenDatasource,
    private val loginService: LoginService
) : TokenRepository {
    override suspend fun getAccessToken(): Flow<Result<String>> =
        tokenDataStore.getAccessToken()

    override suspend fun getRefreshToken(): Flow<Result<String>> =
        tokenDataStore.getRefreshToken()

    override suspend fun saveAccessToken(token: String) =
        tokenDataStore.saveAccessToken(token)

    override suspend fun saveRefreshToken(token: String) =
        tokenDataStore.saveRefreshToken(token)

    override suspend fun loginWithKakao(kakaoToken: String): Flow<Result<Token>> = flow {
        val response = withContext(dispatcher) {
            loginService.requestLogin(
                LoginRequestDto(token = kakaoToken)
            )
        }

        emit(runCatching {
            when {
                response.isSuccessful -> {
                    response.body()?.toVO() ?: throw IllegalStateException("token was null")
                }

                else -> {
                    throw IllegalStateException("login request failed")
                }
            }
        })
    }

}