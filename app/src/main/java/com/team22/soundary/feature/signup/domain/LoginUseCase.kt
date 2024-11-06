package com.team22.soundary.feature.signup.domain

import android.util.Log
import com.team22.soundary.core.domain.TokenRepository
import com.team22.soundary.core.domain.model.Token
import com.team22.soundary.core.domain.model.User
import com.team22.soundary.feature.main.domain.UserRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class LoginUseCase @Inject constructor(
    private val tokenRepository: TokenRepository,
    private val userRepository: UserRepository
) {
    suspend fun invoke(kakaoToken: String): Flow<User> {
        val token = tokenRepository.loginWithKakao(kakaoToken).first()
        tokenRepository.saveRefreshToken(token.accessToken)
        tokenRepository.saveAccessToken(token.refreshToken)
        Log.d("testt","token = "+token)

        try{
            userRepository.getMyInfo()
        } catch (e : Exception){
            Log.d("testt",""+e)
        }

        return userRepository.getMyInfo()
    }
}