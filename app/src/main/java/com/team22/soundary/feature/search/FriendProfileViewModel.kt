package com.team22.soundary.feature.search

import android.util.Log
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

    private val _isFriendAdded = MutableStateFlow(false)
    val isFriendAdded: StateFlow<Boolean> get() = _isFriendAdded.asStateFlow()

    fun loadFriendProfile(friendId: String) {
        viewModelScope.launch {
            try {
                val profile = friendRepository.getFriendById(friendId)
                _friendProfile.value = profile
                Log.d("testt", "FriendProfile eee"+_friendProfile.value!!.displayId)
            } catch (e: Exception) {
                Log.e("FriendProfileViewModel", "Error loading friend profile")
                _friendProfile.value = null // 실패 시 null 설정
            }
        }
    }

    fun addFriend(friendId: String) {
        viewModelScope.launch {
            try {
                val isAdded = friendRepository.addFriend(FriendRequestDto(friendId))
                _isFriendAdded.value = isAdded
            } catch (e: Exception) {
                Log.e("FriendProfileViewModel", "Error adding friend")
                _isFriendAdded.value = false
            }
        }
    }

    fun acceptFriendRequest(friendId: String) {
        viewModelScope.launch {
            try {
                friendRepository.updateFriendStatus(friendId, "accepted")
                _isFriendAdded.value = true
            } catch (e: Exception) {
                Log.e("FriendProfileViewModel", "Error accepting friend request")
                _isFriendAdded.value = false
            }
        }
    }
}
