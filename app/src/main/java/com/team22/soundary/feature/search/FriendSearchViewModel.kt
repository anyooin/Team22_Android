package com.team22.soundary.feature.search

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.team22.soundary.core.data.dto.FriendRequestDto
import com.team22.soundary.core.domain.model.User
import com.team22.soundary.feature.search.data.repository.FriendRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
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
        loadPendingFriends()
        startPeriodicFriendsUpdate()
    }

    // 친구 신청 메서드
    fun requestFriend(user: User) {
        if (isFriend(user)) {
            return
        }
        viewModelScope.launch {
            val isRequested = friendRepository.addFriend(FriendRequestDto(targetId = user.displayId))
            if (isRequested) {
                loadPendingFriends()  // 서버의 친구 요청 목록으로 업데이트
            }
        }
    }

    // 친구 목록을 주기적으로 갱신하는 함수
    private fun startPeriodicFriendsUpdate() {
        viewModelScope.launch {
            while (true) {
                loadFriends()
                loadPendingFriends()
                delay(10000) // 10초마다 갱신 (필요에 따라 조정)
            }
        }
    }

    // 친구 목록 로드
    private fun loadFriends() {
        viewModelScope.launch {
            friendRepository.getFriends().collectLatest {
                _myFriends.value = it
            }
            _newFriends.value = friendRepository.getReceivedRequests()
            _pendingFriends.value = friendRepository.getSentRequests()
        }
    }

    // 서버에서 PendingFriends 목록을 다시 불러오는 메서드
    private fun loadPendingFriends() {
        viewModelScope.launch {
            val sentRequests = friendRepository.getSentRequests()
            _pendingFriends.value = sentRequests
            Log.d("testt", ""+_pendingFriends.value.size)
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

    // 검색어에 따라 친구 목록 필터링
    fun filterFriends(query: String) {
        viewModelScope.launch {
            val filteredNewFriends = friendRepository.getReceivedRequests().filter {
                it.name.contains(query, ignoreCase = true)
            }
            val filteredMyFriends = _myFriends.value.filter {
                it.status == "accepted" && it.name.contains(query, ignoreCase = true)
            }
            val filteredPendingFriends = friendRepository.getSentRequests().filter {
                it.name.contains(query, ignoreCase = true)
            }

            _newFriends.value = filteredNewFriends
            _myFriends.value = filteredMyFriends
            _pendingFriends.value = filteredPendingFriends
        }
    }

    // 필터 초기화
    fun resetFilters() {
        loadFriends()
    }
}
