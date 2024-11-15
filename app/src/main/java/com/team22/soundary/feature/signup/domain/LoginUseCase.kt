package com.team22.soundary.feature.signup.domain

import android.util.Log
import com.team22.soundary.core.domain.TokenRepository
import com.team22.soundary.core.domain.model.User
import com.team22.soundary.feature.main.domain.UserRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import javax.inject.Inject

class LoginUseCase @Inject constructor(
    private val tokenRepository: TokenRepository,
    private val userRepository: UserRepository
) {
    suspend fun invoke(kakaoToken: String): Flow<User> {
        val token = tokenRepository.loginWithKakao(kakaoToken).first()
        tokenRepository.saveRefreshToken(token.refreshToken)
        tokenRepository.saveAccessToken(token.accessToken)

        //Log.d("testt", "1: " + token.accessToken)
        //Log.d("testt", "2: " + tokenRepository.getAccessToken().firstOrNull())

        try {
            val result = userRepository.getMyInfo()
            //Log.d("testt", "role : " + result.firstOrNull()?.role)
        } catch (e: Exception) {
            Log.e("testt", "" + e)
        }

        return userRepository.getMyInfo()
    }
}