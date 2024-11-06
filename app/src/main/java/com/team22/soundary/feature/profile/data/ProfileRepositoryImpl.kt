package com.team22.soundary.feature.profile.data

import android.net.Uri
import android.util.Log
import com.team22.soundary.core.IODispatcher
import com.team22.soundary.core.data.dto.LabelAdd
import com.team22.soundary.core.data.dto.UserUpdateRequest
import com.team22.soundary.core.data.dto.toVO
import com.team22.soundary.core.domain.model.User
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import javax.inject.Inject

class ProfileRepositoryImpl @Inject constructor(
    @IODispatcher private val dispatcher: CoroutineDispatcher,
    private val apiService: ProfileApiService
): ProfileRepository {
    override suspend fun getProfiles(): Flow<User> = flow{
        val response = withContext(dispatcher) {
            apiService.getProfile()
        }

        if (response.isSuccessful) {
            emit(response.body()?.toVO() ?: User())
            if(response.body() == null) {
                Log.d("uin", "error")
            }
            else {
                Log.d("uin", "ok" + response.body()!!.description)
            }

        } else {
            throw Exception("Error: ${response.message()}")
        }
    }

    override suspend fun addLabels(labels: List<String>) {
        val response = withContext(dispatcher) {
            apiService.addLabels(LabelAdd(labels))
        }

        if (!response.isSuccessful) {
            throw Exception("Error: ${response.message()}")
        }
    }

    override suspend fun deleteLabel(label: String) {
        val response = withContext(dispatcher) {
            apiService.deleteLabel(label)
        }

        if (!response.isSuccessful) {
            throw Exception("Error: ${response.message()}")
        }
    }

    override suspend fun editedProfile(displayId: String, nickname: String, description: String?, profileUri: Uri?) {
        // Uri를 String으로 변환하여 UserUpdateRequest 객체 생성
        val userUpdateRequest = UserUpdateRequest(
            displayId = displayId,
            nickcname = nickname,
            description = description,
            profileImage = profileUri?.toString()
        )

        val response = withContext(dispatcher) {
            apiService.putProfile(userUpdateRequest)
        }

        if (!response.isSuccessful) {
            throw Exception("Error: ${response.message()}")
        }
    }

    override suspend fun deleteUserAccount() {
        val response = withContext(dispatcher) {
            apiService.deleteUserAccount()
        }
        if(!response.isSuccessful) {
            throw Exception("회원 탈퇴 실패: ${response.message()}")
        }
    }

}