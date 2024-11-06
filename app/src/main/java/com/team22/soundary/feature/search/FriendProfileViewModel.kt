package com.team22.soundary.feature.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.team22.soundary.core.data.dto.FriendRequestDto
import com.team22.soundary.core.domain.model.User
import com.team22.soundary.feature.search.data.repository.FriendRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FriendProfileViewModel @Inject constructor(
    private val friendRepository: FriendRepository
) : ViewModel() {

    private val _friendProfile = MutableStateFlow<User?>(null)
    val friendProfile: StateFlow<User?> get() = _friendProfile.asStateFlow()

    private val _isFriendAdded = MutableStateFlow<Boolean>(false)
    val isFriendAdded: StateFlow<Boolean> get() = _isFriendAdded.asStateFlow()

    // 친구 프로필 로드
    fun loadFriendProfile(friendId: String) {
        viewModelScope.launch {
            val profile = friendRepository.getFriendById(friendId)
            _friendProfile.value = profile
        }
    }

    fun addFriend(friendId: String) {
        viewModelScope.launch {
            // friendId를 사용하여 FriendRequestDto 객체 생성
            val friendRequestDto = FriendRequestDto(targetId = friendId)

            // friendRequestDto 객체를 전달
            val result = friendRepository.addFriend(friendRequestDto)

            _isFriendAdded.value = result
            loadFriendProfile(friendId)
            _isFriendAdded.value = false // 상태 초기화
        }
    }


    // 친구 요청 수락
    fun acceptFriendRequest(friendId: String) {
        viewModelScope.launch {
            friendRepository.updateFriendStatus(friendId, "accepted")
            loadFriendProfile(friendId)
        }
    }
}

