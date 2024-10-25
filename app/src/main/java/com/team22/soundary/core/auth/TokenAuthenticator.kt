package com.team22.soundary.core.auth

import com.team22.soundary.core.domain.TokenRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import javax.inject.Inject

class TokenAuthenticator @Inject constructor(
    private val tokenRepository: TokenRepository
) : Authenticator {

    override fun authenticate(route: Route?, response: Response): Request? {
        val token : String = runBlocking {
            tokenRepository.getRefreshToken().first().getOrNull()
        } ?: ""

        return Request.Builder().addHeader(AUTH, TOKEN_PREFIX+token).build()
    }

    companion object{
        const val AUTH = "Authorization"
        const val TOKEN_PREFIX = "Bearer "
    }
}