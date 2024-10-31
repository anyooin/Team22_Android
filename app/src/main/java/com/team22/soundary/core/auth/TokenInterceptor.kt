package com.team22.soundary.core.auth

import com.team22.soundary.core.domain.TokenRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

class TokenInterceptor @Inject constructor(
    private val tokenRepository: TokenRepository
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val token : String = runBlocking {
            tokenRepository.getAccessToken().first().getOrNull()
        } ?: ""

        val request = chain.request().newBuilder().addHeader(AUTH, TOKEN_PREFIX+token).build()

        return chain.proceed(request)
    }

    companion object{
        const val AUTH = "Authorization"
        const val TOKEN_PREFIX = "Bearer "
    }
}