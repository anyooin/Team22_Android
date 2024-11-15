package com.team22.soundary.feature.signup.domain

import com.team22.soundary.core.IODispatcher
import com.team22.soundary.core.data.dto.UserInfoInitRequestDto
import com.team22.soundary.core.domain.TokenRepository
import com.team22.soundary.core.domain.model.User
import com.team22.soundary.feature.signup.data.remote.UserService
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import okhttp3.MultipartBody
import javax.inject.Inject

class UserDetailUpdateUseCase @Inject constructor(
    @IODispatcher private val dispatcher: CoroutineDispatcher,
    private val userService: UserService,
    private val tokenRepository: TokenRepository
) {
    suspend fun uploadImage(multipart: MultipartBody.Part): String {
        val response = withContext(dispatcher) {
            userService.uploadImageToServer(multipart)
        }

        return when {
            response.isSuccessful -> {
                response.body()?.imageId ?: ""
            }

            else -> {
                ""
            }
        }
    }


    suspend fun updateUserInfo(token: String, user: User): Boolean {
        val response = withContext(dispatcher) {
            userService.updateMyInfo(
                UserInfoInitRequestDto(
                    user.label,
                    token,
                    user.displayId,
                    user.name,
                    user.statusMessage,
                    user.imageId
                )
            )
        }

        return when {
            response.isSuccessful -> {
                withContext(dispatcher) {
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