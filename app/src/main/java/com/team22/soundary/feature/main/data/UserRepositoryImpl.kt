package com.team22.soundary.feature.main.data

import com.team22.soundary.core.data.dto.toVO
import com.team22.soundary.core.domain.model.Share
import com.team22.soundary.core.domain.model.User
import com.team22.soundary.feature.main.data.remote.ShareService
import com.team22.soundary.feature.main.data.remote.UserService
import com.team22.soundary.feature.main.domain.UserRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val dispatcher: CoroutineDispatcher,
    private val retrofitService: UserService
): UserRepository {
    override suspend fun getMyInfo(): Flow<Result<User>> = flow{
        val response = withContext(dispatcher){
            retrofitService.requestMyInfo()
        }

        emit(
            runCatching {
                if(response.isSuccessful){
                    response.body()?.toVO() ?: throw Exception("Empty User")
                } else {
                    throw Exception("Error : ${response.message()}")
                }
            }
        )
    }

}