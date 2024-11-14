package com.team22.soundary.feature.search.presentation.friend

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.team22.soundary.core.data.dto.FriendRequestDto
import com.team22.soundary.core.domain.model.User
import com.team22.soundary.feature.search.data.repository.FriendRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FriendSearchViewModel @Inject constructor(
    private val friendRepository: FriendRepository
) : ViewModel() {

    private val _newFriends = MutableStateFlow<List<User>>(emptyList())
    val newFriends: StateFlow<List<User>> get() = _newFriends.asStateFlow()

    private val _myFriends = MutableStateFlow<List<User>>(emptyList())
    val myFriends: StateFlow<List<User>> get() = _myFriends.asStateFlow()

    private val _pendingFriends = MutableStateFlow<List<User>>(emptyList())
    val pendingFriends: StateFlow<List<User>> get() = _pendingFriends.asStateFlow()

    private val _searchResultList = MutableStateFlow<List<User>>(emptyList())
    val searchResultList: StateFlow<List<User>> get() = _searchResultList.asStateFlow()

    init {
        loadFriends()
    }

    // 친구 신청 메서드
    fun requestFriend(user: User) {
        if (!isFriend(user)) {
            viewModelScope.launch {
                friendRepository.addFriend(FriendRequestDto(user.displayId))
            }
        }
    }

    // 친구 목록 로드
    fun loadFriends() {
        viewModelScope.launch {
            friendRepository.getFriends().collectLatest {
                _myFriends.value = it
            }
            friendRepository.getReceivedRequests().collectLatest {
                _newFriends.value = it
            }
            friendRepository.getSentRequests().collectLatest {
                _pendingFriends.value = it
            }
        }
    }

    // 사용자가 현재 친구 목록에 포함되는지 확인하는 함수
    fun isFriend(user: User): Boolean {
        val isInMyFriends = _myFriends.value.any { it.displayId == user.displayId }
        val isInPendingFriends = _pendingFriends.value.any { it.displayId == user.displayId }
        val isInNewFriends = _newFriends.value.any { it.displayId == user.displayId }
        return isInMyFriends || isInPendingFriends || isInNewFriends
    }

    fun isInMyFriend(user: User): Boolean {
        return _myFriends.value.any { it.displayId == user.displayId }
    }

    fun searchUser(searchName: String) {
        viewModelScope.launch {
            val searchResult = friendRepository.searchUserByDisplayId(searchName)
            searchResult?.let {
                _searchResultList.value = listOf(it)
            } ?: run {
                _searchResultList.value = emptyList()
            }
        }
    }

    // 친구 수락 메서드
    fun acceptFriend(friend: User) {
        viewModelScope.launch {
            val isAccepted = friendRepository.updateFriendStatus(friend.displayId)
            if (isAccepted) {
                loadFriends()
            }
        }
    }

    // 친구 요청 거절 메서드
    fun declineFriend(friend: User) {
        viewModelScope.launch {
            val isDeclined = friendRepository.rejectReceivedRequest(friend.id)
            if (isDeclined) {
                loadFriends()
            }
        }
    }

    // 친구 삭제 메서드
    fun deleteFriend(friend: User) {
        viewModelScope.launch {
            val isDeleted = friendRepository.removeFriend(friend.id)
            if (isDeleted) {
                loadFriends()
            }
        }
    }
}

