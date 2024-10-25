package com.team22.soundary.feature.signup.domain

import com.team22.soundary.core.domain.TokenRepository
import com.team22.soundary.core.domain.model.Token
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class LoginUseCase @Inject constructor(
    private val tokenRepository: TokenRepository
) {
    suspend fun invoke(kakaoToken: String): Flow<Result<Token>> =
        tokenRepository.loginWithKakao(kakaoToken).map { result ->
            result.onSuccess {token ->
                tokenRepository.saveAccessToken(token.accessToken)
                tokenRepository.saveRefreshToken(token.refreshToken)
            }
        }
}