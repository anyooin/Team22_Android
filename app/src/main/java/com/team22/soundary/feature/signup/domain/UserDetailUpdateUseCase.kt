package com.team22.soundary.feature.signup.domain

import android.util.Log
import com.team22.soundary.core.IODispatcher
import com.team22.soundary.core.data.dto.LabelAdd
import com.team22.soundary.core.data.dto.RefreshRequestDto
import com.team22.soundary.core.data.dto.UserInfoInitRequestDto
import com.team22.soundary.core.domain.TokenRepository
import com.team22.soundary.core.domain.model.Category
import com.team22.soundary.core.domain.model.User
import com.team22.soundary.feature.signup.data.remote.LoginService
import com.team22.soundary.feature.signup.data.remote.UserService
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import javax.inject.Inject

class UserDetailUpdateUseCase @Inject constructor(
    @IODispatcher private val dispatcher : CoroutineDispatcher,
    private val userService: UserService,
    private val tokenRepository: TokenRepository
) {

    suspend fun updateUserInfo(user: User) {
        val response = withContext(dispatcher){
            userService.updateMyInfo(
                UserInfoInitRequestDto(
                    user.category,
                    "",
                    user.id,
                    user.name,
                    user.statusMessage,
                    user.image.toString()
                )
            )
        }

        Log.d("akuby",""+response)

        when{
            response.isSuccessful -> {
                val res = withContext(dispatcher){
                    tokenRepository.refresh()
                }
                Log.d("kkkkkkk",""+res)
            }
            else -> {
                Log.d("gi","bye")
            }
        }
    }

    suspend fun updateCategoryInfo(category: List<String>) {
        val response = withContext(dispatcher){
            userService.addLabels(
                LabelAdd(category)
            )
        }

        when{
            response.isSuccessful -> {
                val res = withContext(dispatcher){
                    tokenRepository.refresh()
                }
            }
            else -> {
                Log.d("gi","bye")
            }
        }
    }

}