package com.team22.soundary.feature.search.presentation.friend

import android.util.Log
import androidx.lifecycle.SavedStateHandle
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
    private val friendRepository: FriendRepository,
    private val savedStateHandle: SavedStateHandle
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
        loadPendingFriends()
    }

    // 친구 신청 메서드
    fun requestFriend(user: User) {
        if (isFriend(user)) {
            return
        }
        viewModelScope.launch {
            val isRequested = friendRepository.addFriend(FriendRequestDto(user.displayId))
            if (isRequested) {
                loadPendingFriends()  // 서버의 친구 요청 목록으로 업데이트
            }
        }
    }

    // 친구 목록 로드
    fun loadFriends() {
        viewModelScope.launch {
            friendRepository.getFriends().collectLatest {
                savedStateHandle["myFriends"] = it
            }
            friendRepository.getReceivedRequests().collectLatest {
                savedStateHandle["newFriends"] = it
            }
            friendRepository.getSentRequests().collectLatest {
                savedStateHandle["pendingFriends"] = it
            }
        }
    }

    // 서버에서 PendingFriends 목록을 다시 불러오는 메서드
    private fun loadPendingFriends() {
        viewModelScope.launch {
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
        if(isInMyFriends || isInPendingFriends || isInNewFriends) {
            Log.d("testt", "true")
        } else {
            Log.d("testt", "false")
        }
        return isInMyFriends || isInPendingFriends || isInNewFriends
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
            friendRepository.updateFriendStatus(friend.displayId, "accepted")
            _newFriends.value = _newFriends.value.filter { it.id != friend.id }
            _myFriends.value = _myFriends.value + friend.copy(status = "accepted")
        }
    }

    // 친구 요청 거절 메서드
    fun declineFriend(friend: User) {
        viewModelScope.launch {
            friendRepository.rejectReceivedRequest(friend.id)
            _newFriends.value = _newFriends.value.filter { it.id != friend.id }
        }
    }

    // 친구 삭제 메서드
    fun deleteFriend(friend: User) {
        viewModelScope.launch {
            friendRepository.removeFriend(friend.id)
            _myFriends.value = _myFriends.value.filter { it.id != friend.id }
        }
    }
}
