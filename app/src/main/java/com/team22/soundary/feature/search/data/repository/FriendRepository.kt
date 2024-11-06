package com.team22.soundary.feature.search.data.repository

import android.util.Log
import com.team22.soundary.core.data.dto.FriendRequestDto
import com.team22.soundary.core.data.dto.toVO
import com.team22.soundary.core.domain.model.User
import com.team22.soundary.feature.search.data.api.FriendApiService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FriendRepository @Inject constructor(
    private val friendApiService: FriendApiService
) {

    // 친구 목록 가져오기
    suspend fun getFriends(): Flow<List<User>> {
        return flow {
            try {
                val response = friendApiService.getFriends()
                if (response.isSuccessful) {
                    Log.d("testt", "response.body()" + response.body()?.friends?.size)
                    emit(response.body()?.friends?.map { it.toVO() } ?: emptyList())
                } else {
                    Log.d("testt", "response fail")
                }
            } catch (e: Exception) {
                Log.d("testt", "response fail")
                e.printStackTrace()
            }
        }
    }

    // 친구 추가 요청 보내기
    suspend fun addFriend(targetDisplayId: FriendRequestDto): Boolean {
        return try {
            // API 호출 시, 전달된 FriendRequestDto 객체를 그대로 사용
            val response = friendApiService.addFriend(targetDisplayId)
            Log.d("testt", "targetDisplayId : " + targetDisplayId.targetId)
            if (response.isSuccessful) {
                notifyDataChange()
                Log.d("testt", "success")
                true
            } else {
                Log.d("testt", "error: ${response.code()} - ${response.message()}")
                false
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Log.e("testt", "error on addFriend: $e")
            false
        }
    }


    // 보낸 친구 요청 목록 가져오기
    suspend fun getSentRequests(): Flow<List<User>> {
        return flow {
            try {
                val response = friendApiService.getSentRequests()
                Log.d("testt","getSentResponse:"+response.code()+" "+response.message())
                Log.d("testt","getSent2" + response.body()?.sentRequests)
                if (response.isSuccessful) {
                    emit(response.body()?.sentRequests?.map { it.toVO() }?: emptyList())
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    // 받은 친구 요청 목록 가져오기
    suspend fun getReceivedRequests(): Flow<List<User>> {
        return flow {
            try {
                val response = friendApiService.getReceivedRequests()
                if (response.isSuccessful) {
                    emit(response.body()?.receivedRequests?.map {it.toVO() } ?: emptyList())
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    // 친구 프로필 가져오기
    suspend fun getFriendById(friendId: String): User? {
        return try {
            val response = friendApiService.searchUser(friendId)
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
    suspend fun updateFriendStatus(friendId: String, newStatus: String): Boolean {
        return try {
            if (newStatus == "accepted") {
                Log.d("aaaaa","invoke")
                val res = friendApiService.addFriend(FriendRequestDto(friendId))
                Log.d("aaaaa",""+res.errorBody())
                res.isSuccessful
            } else {
                false
            }
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
    // 사용자 검색
    suspend fun searchUserByDisplayId(displayId: String): User? {
        return try {
            val response = friendApiService.searchUser(displayId)

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

    // 데이터 변경 알림을 위한 SharedFlow
    private val _dataChanged = MutableSharedFlow<Unit>()
    val dataChanged = _dataChanged.asSharedFlow()

    // 데이터 변경 알림 메서드
    private suspend fun notifyDataChange() {
        _dataChanged.emit(Unit)
    }
}

