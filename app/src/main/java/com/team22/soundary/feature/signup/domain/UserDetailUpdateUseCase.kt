package com.team22.soundary.feature.signup.domain

import android.util.Log
import com.team22.soundary.core.IODispatcher
import com.team22.soundary.core.data.dto.LabelAddRequest
import com.team22.soundary.core.data.dto.UserInfoInitRequestDto
import com.team22.soundary.core.domain.TokenRepository
import com.team22.soundary.core.domain.model.User
import com.team22.soundary.feature.signup.data.remote.UserService
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

class UserDetailUpdateUseCase @Inject constructor(
    @IODispatcher private val dispatcher : CoroutineDispatcher,
    private val userService: UserService,
    private val tokenRepository: TokenRepository
) {

    suspend fun updateUserInfo(token:String, user: User) : Boolean {
        val response = withContext(dispatcher){
            userService.updateMyInfo(
                UserInfoInitRequestDto(
                    user.label,
                    token,
                    user.displayId,
                    user.name,
                    user.statusMessage,
                    user.image.toString()
                )
            )
        }

        Log.d("uin", user.label[0])
        Log.d("uin", user.name)
        Log.d("uin", user.statusMessage)
        Log.d("uin", user.image.toString())

        return when{
            response.isSuccessful -> {
                withContext(dispatcher){
                    tokenRepository.refresh()
                }
                true
            }

            else -> {
                false
            }
        }
    }

}