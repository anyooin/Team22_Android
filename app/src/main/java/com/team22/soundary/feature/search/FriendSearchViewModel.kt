package com.team22.soundary.feature.search

import android.net.Uri
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

    init {
        loadFriends("currentUserId")
    }

    private fun loadFriends(userId: String) {
        viewModelScope.launch {
            val allFriends = friendRepository.getFriends(userId = userId)
            _myFriends.value = allFriends.filter { it.status == "accepted" }

            _newFriends.value = friendRepository.getReceivedRequests(userId = userId)
            _pendingFriends.value = friendRepository.getSentRequests(userId = userId)
        }
    }


    // 친구 수락 메서드
    fun acceptFriend(userId: String, friend: User) {
        viewModelScope.launch {
            friendRepository.updateFriendStatus(userId, friend.id, "accepted")
            loadFriends(userId)
        }
    }


    fun declineFriend(userId: String, friend: User) {
        viewModelScope.launch {
            friendRepository.rejectReceivedRequest(friend.id)
            loadFriends(userId)
        }
    }

    fun deleteFriend(userId: String, friend: User) {
        viewModelScope.launch {
            friendRepository.removeFriend(friend.id)
            loadFriends(userId)
        }
    }


    // 검색어에 따라 친구 목록 필터링
    fun filterFriends(userId: String, query: String) {
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
    fun resetFilters(userId: String) {
        loadFriends(userId)
    }
}
