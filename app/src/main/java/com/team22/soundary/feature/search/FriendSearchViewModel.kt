package com.team22.soundary.feature.search

import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.team22.soundary.core.domain.model.User
import com.team22.soundary.feature.search.data.repository.FriendRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
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
    private val userId: String = "currentUserId" // TODO: 실제 로그인된 사용자 ID로 수정 필요

    init {
        loadFriends()
    }

    // 친구 신청 메서드
    fun requestFriend(user: User) {
        viewModelScope.launch {
            val isRequested = friendRepository.addFriend(userId, user.id)
            if (isRequested) {
                val updatedPendingFriends = _pendingFriends.value.toMutableList()
                updatedPendingFriends.add(user)
                _pendingFriends.value = updatedPendingFriends
            }
        }
    }

    // 친구 목록 로드
    private fun loadFriends() {
        viewModelScope.launch {
            val allFriends = friendRepository.getFriends(userId = userId)
            _myFriends.value = allFriends.filter { it.status == "accepted" }
            _newFriends.value = friendRepository.getReceivedRequests(userId = userId)
            _pendingFriends.value = friendRepository.getSentRequests(userId = userId)
        }
    }


    // 사용자가 현재 친구 목록에 포함되는지 확인하는 함수
    fun isFriend(user: User): Boolean {
        val isInMyFriends = _myFriends.value.any { it.id == user.id }
        val isInPendingFriends = _pendingFriends.value.any { it.id == user.id }
        val isInNewFriends = _newFriends.value.any { it.id == user.id }
        return isInMyFriends || isInPendingFriends || isInNewFriends
    }

    fun searchUser(searchName: String) {
        viewModelScope.launch {
            val searchResult = friendRepository.searchUserByDisplayId(searchName)

            Log.d("testt","in Viewmodel : "+searchResult+" ,"+searchName)
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
            friendRepository.updateFriendStatus(userId, friend.id, "accepted")
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
            val filteredNewFriends = friendRepository.getReceivedRequests(userId = userId).filter {
                it.name.contains(query, ignoreCase = true)
            }
            val filteredMyFriends = friendRepository.getFriends(userId = userId).filter {
                it.status == "accepted" && it.name.contains(query, ignoreCase = true)
            }
            val filteredPendingFriends = friendRepository.getSentRequests(userId = userId).filter {
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

