package com.team22.soundary.feature.search.data.repository

import android.net.Uri
import com.team22.soundary.core.data.dto.FromUserResponse
import com.team22.soundary.core.data.dto.toVO
import com.team22.soundary.core.domain.model.User
import com.team22.soundary.feature.search.data.FriendRequest
import com.team22.soundary.feature.search.data.api.FriendApiService
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FriendRepository @Inject constructor(
    private val friendApiService: FriendApiService
) {

    // 친구 목록 가져오기
    suspend fun getFriends(userId: String, label: List<String>? = null): List<User> {
        return try {
            val response = friendApiService.getFriends(userId, label)
            if (response.isSuccessful) {
                response.body()?.friends?.map { it.toVO() }?:emptyList()
            } else {
                emptyList()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    // 친구 추가 요청 보내기
    suspend fun addFriend(userId: String, targetDisplayId: String): Boolean {
        return try {
            val requestBody = mapOf("target_display_id" to targetDisplayId)
            friendApiService.addFriend(userId, requestBody).isSuccessful.also { success ->
                if (success) notifyDataChange()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    // 보낸 친구 요청 목록 가져오기
    suspend fun getSentRequests(userId: String): List<User> {
        return try {
            val response = friendApiService.getSentRequests(userId)
            if (response.isSuccessful) {
                response.body()?.sentRequests?.map { it.toVO() }?: emptyList()
            } else {
                emptyList()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    // 받은 친구 요청 목록 가져오기
    suspend fun getReceivedRequests(userId: String): List<User> {
        return try {
            val response = friendApiService.getReceivedRequests(userId)
            if (response.isSuccessful) {
                response.body()?.receivedRequests?.map {it.toVO() } ?: emptyList()
            } else {
                emptyList()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    // 친구 프로필 가져오기
    suspend fun getFriendById(friendId: String): User? {
        return try {
            val response = friendApiService.getFriendProfile(friendId)
            if (response.isSuccessful) {
                response.body()?.toVO()
            } else {
                null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    suspend fun removeFriend(targetUserId: String): Boolean {
        return try {
            val response = friendApiService.removeFriend(targetUserId)
            response.isSuccessful
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    // 친구 프로필 가져오기
    /*suspend fun getFriendProfile(friendId: String): User? {
        return try {
            val response = friendApiService.getFriendProfile(friendId)
            if (response.isSuccessful) response.body() else null
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }*/

    // 받은 친구 요청 거절 메서드
    suspend fun rejectReceivedRequest(targetUserId: String): Boolean {
        return try {
            val response = friendApiService.rejectReceivedRequest(targetUserId)
            response.isSuccessful
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
    // 친구 상태 업데이트 메서드 (친구 수락 시에만 적용)
    suspend fun updateFriendStatus(userId: String, friendId: String, newStatus: String): Boolean {
        return try {
            if (newStatus == "accepted") {
                val requestBody = mapOf("target_display_id" to friendId)
                friendApiService.addFriend(userId, requestBody).isSuccessful
            } else {
                false
            }
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    // 데이터 변경 알림을 위한 SharedFlow
    private val _dataChanged = MutableSharedFlow<Unit>()
    val dataChanged = _dataChanged.asSharedFlow()

    // 데이터 변경 알림 메서드
    private suspend fun notifyDataChange() {
        _dataChanged.emit(Unit)
    }
}
