package com.team22.soundary.core.data

import android.util.Log
import com.team22.soundary.core.IODispatcher
import com.team22.soundary.core.data.dto.LoginRequestDto
import com.team22.soundary.core.data.dto.RefreshRequestDto
import com.team22.soundary.core.data.dto.UserInfoInitRequestDto
import com.team22.soundary.core.data.dto.toVO
import com.team22.soundary.core.domain.TokenRepository
import com.team22.soundary.core.domain.model.Token
import com.team22.soundary.core.domain.model.User
import com.team22.soundary.feature.signup.data.remote.LoginService
import com.team22.soundary.feature.signup.data.remote.UserService
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import java.lang.IllegalStateException
import javax.inject.Inject

internal class TokenRepositoryImpl @Inject constructor(
    @IODispatcher private val dispatcher: CoroutineDispatcher,
    private val tokenDataStore: TokenDatasource,
    private val loginService: LoginService
) : TokenRepository {
    override suspend fun getAccessToken(): Flow<Result<String>> {
        Log.d("akuby21",""+tokenDataStore.getAccessToken().first().getOrNull())
        return tokenDataStore.getAccessToken()
    }

    override suspend fun getRefreshToken(): Flow<Result<String>> =
        tokenDataStore.getRefreshToken()

    override suspend fun saveAccessToken(token: String) =
        tokenDataStore.saveAccessToken(token)

    override suspend fun saveRefreshToken(token: String) =
        tokenDataStore.saveRefreshToken(token)

    override suspend fun loginWithKakao(kakaoToken: String): Flow<Token> = flow {
        val response = withContext(dispatcher) {
            loginService.requestLogin(
                LoginRequestDto(token = kakaoToken)
            )
        }

        emit(
            when {
                response.isSuccessful -> {
                    response.body()?.toVO() ?: throw IllegalStateException("token was null")
                }

                else -> {
                    throw IllegalStateException("login request failed")
                }
            }
        )
    }

    override suspend fun refresh(){
        val refreshToken = getRefreshToken().first()

        val response = withContext(dispatcher){
            loginService.requestRefresh(
                RefreshRequestDto(refreshToken.getOrThrow())
            )
        }

        if(response.isSuccessful){
            response.body()?.accessToken?.let{
                saveAccessToken(it)
            }

            response.body()?.refreshToken?.let{
                saveRefreshToken(it)
            }
        }
    }

}