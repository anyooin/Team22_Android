package com.team22.soundary.feature.signup.domain

import android.util.Log
import com.team22.soundary.core.domain.TokenRepository
import com.team22.soundary.feature.main.domain.UserRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.util.Base64
import javax.inject.Inject

class CheckTokenUseCase @Inject constructor(
    private val tokenRepository: TokenRepository,
    private val userRepository: UserRepository,
    private val dispatcher: CoroutineDispatcher
) {
    suspend fun invoke(): Boolean =
        try {
            val token = tokenRepository.getAccessToken().firstOrNull()
                ?: throw Exception("token was null")

            if (isTokenExpired(token.getOrThrow())) {
                throw Exception("token has expired")
            }
            true
        } catch (e: Exception) {
            false
        }

    private fun isTokenExpired(token: String): Boolean {
        return try {
            val actualToken = if (token.startsWith(TOKEN_TYPE)) {
                token.substring(TOKEN_TYPE.length)
            } else {
                token
            }

            val split = actualToken.split(".")
            if (split.size != 3) return true

            val decoder = Base64.getUrlDecoder()
            val payload = String(decoder.decode(split[1]))
            val json = JSONObject(payload)

            val expirationTime = json.optLong("exp", 0) * 1000
            val currentTime = System.currentTimeMillis()

            currentTime >= expirationTime
        } catch (e: Exception) {
            true
        }
    }

    companion object{
        const val TOKEN_TYPE = "Bearer "
    }
}
