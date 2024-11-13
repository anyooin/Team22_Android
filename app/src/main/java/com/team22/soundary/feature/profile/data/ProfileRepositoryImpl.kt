package com.team22.soundary.feature.profile.data

import android.net.Uri
import android.util.Log
import com.google.firebase.messaging.FirebaseMessaging
import com.team22.soundary.core.IODispatcher
import com.team22.soundary.core.data.dto.LabelAddRequest
import com.team22.soundary.core.data.dto.UpdateDeviceRequest
import com.team22.soundary.core.data.dto.UserUpdateRequest
import com.team22.soundary.core.data.dto.toVO
import com.team22.soundary.core.domain.model.User
import com.team22.soundary.feature.profile.data.remote.ProfileApiService
import com.team22.soundary.feature.profile.domain.ProfileRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import okhttp3.MultipartBody
import javax.inject.Inject

class ProfileRepositoryImpl @Inject constructor(
    @IODispatcher private val dispatcher: CoroutineDispatcher,
    private val apiService: ProfileApiService
) : ProfileRepository {
    override suspend fun getProfiles(): Flow<User> = flow {
        val response = withContext(dispatcher) {
            apiService.getProfile()
        }

        if (response.isSuccessful) {
            emit(response.body()?.toVO() ?: User())
            if (response.body() == null) {
                Log.d("uin", "error")
            } else {
                Log.d("uin", "ok" + response.body()!!.description)
            }

        } else {
            throw Exception("Error: ${response.message()}")
        }
    }

    override suspend fun uploadImage(multipart: MultipartBody.Part): String {
        val response = withContext(dispatcher) {
            apiService.uploadImageToServer(multipart)
        }

        return when {
            response.isSuccessful -> {
                if (response.body() == null) {
                    Log.d("uin", "성공 : 바디가 비어있음" + response.body()?.imageId)
                } else {
                    Log.d("uin", "성공 : 바디값" + response.body()?.imageId)
                }
                response.body()?.imageId ?: ""
            }

            else -> {
                Log.d("uin", "취소")
                ""
            }
        }
    }

    override suspend fun editProfile(
        displayId: String,
        nickname: String,
        description: String?,
        imageId: String
    ) {
        // Uri를 String으로 변환하여 UserUpdateRequest 객체 생성
        val userUpdateRequest = UserUpdateRequest(
            displayId = displayId,
            nickname = nickname,
            description = description,
            profileImage = imageId
        )

        val response = withContext(dispatcher) {
            apiService.putProfile(userUpdateRequest)
        }

        Log.d("uin", "수정한 이미지 : " + imageId)

        if (!response.isSuccessful) {
            throw Exception("Error: ${response.message()}")
        }
    }

    override suspend fun deleteUserAccount() {
        val response = withContext(dispatcher) {
            apiService.deleteUserAccount()
        }
        if (!response.isSuccessful) {
            throw Exception("회원 탈퇴 실패: ${response.message()}")
        }
    }

    override suspend fun setLabels(labels: List<String>) {
        val response = withContext(dispatcher) {
            apiService.setLabels(LabelAddRequest(labels))
        }

        if (!response.isSuccessful) {
            throw Exception("Error: ${response.message()}")
        }
    }

    override suspend fun setDeviceToken() {
        var token = ""

        CoroutineScope(Dispatchers.IO).launch {
            try {
                // 기존 토큰 삭제
                FirebaseMessaging.getInstance().deleteToken().await()

                // 새로운 토큰 요청
                FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val newToken = task.result
                        token = newToken
                        println("새로운 FCM 토큰: $newToken")
                        println("새로운 FCM 토큰: $token")
                    } else {
                        println("토큰 갱신 실패: ${task.exception}")
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }

            val response = withContext(dispatcher) {
                apiService.setDeviceToken(UpdateDeviceRequest(token))
            }

            if (!response.isSuccessful) {
                Log.d("uin", "레포실패" + response.message())
                throw Exception("Error: ${response.message()}")
            }

        }
    }
}