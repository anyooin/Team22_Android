package com.team22.soundary.feature.share.presentation.share

import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.team22.soundary.core.domain.model.Category
import com.team22.soundary.core.domain.model.User
import com.team22.soundary.feature.search.data.repository.FriendRepository
import com.team22.soundary.feature.share.domain.ShareRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ShareViewModel @Inject constructor(
    private val shareRepository: ShareRepository,
    private val friendRepository: FriendRepository
) : ViewModel() {
    private val _userList = MutableStateFlow<List<User>>(emptyList())
    val userList: StateFlow<List<User>> = _userList.asStateFlow()

    private val _filteredUserList = MutableStateFlow<List<User>>(emptyList())
    val filteredUserList: StateFlow<List<User>> = _filteredUserList.asStateFlow()

    private val _selectedFriendIds = MutableStateFlow<Set<String>>(emptySet())
    val selectedFriendIds: StateFlow<Set<String>> = _selectedFriendIds.asStateFlow()

    private val _comment = MutableStateFlow("")
    val comment: StateFlow<String> = _comment.asStateFlow()

    private val _label = MutableStateFlow<String?>(null)

    init {
        viewModelScope.launch {
            initFriendList()
        }
    }

    fun setComment(updatedText: String) {
        _comment.value = updatedText
    }

    private suspend fun initFriendList() {
        friendRepository.getFriends().collectLatest {
            _userList.value = it
        }
        getFilteredFriendList(_label.value)
    }

    fun toggleFriendSelection(friendId: String) {
        _selectedFriendIds.update { currentSelectedIds ->
            if (currentSelectedIds.contains(friendId)) {
                currentSelectedIds - friendId
            } else {
                currentSelectedIds + friendId
            }
        }
    }

    fun setAllFriendsSelected(selected: Boolean) {
        if (selected) {
            _selectedFriendIds.value = _filteredUserList.value.map { it.id }.toSet()
        } else {
            _selectedFriendIds.value = emptySet()
        }
    }

    fun getFilteredFriendList(label: String?) {
        _label.value = label
        _filteredUserList.value = if (label == null) {
            _userList.value
        } else {
            _userList.value.filter { it.label.contains(label) }
        }
    }

    fun isAllFriendsSelected(): Boolean {
        for (element in _filteredUserList.value) {
            if (element.id !in _selectedFriendIds.value) {
                return false
            }
        }
        return true
    }

    fun getButtonText(): String {
        return "${_selectedFriendIds.value.size}명에게 보내기"
    }

    fun getSelectedFriends(): List<User> {
        return _userList.value.filter { _selectedFriendIds.value.contains(it.id) }
    }

    fun isAnyFriendSelected(): Boolean {
        return _selectedFriendIds.value.isNotEmpty()
    }

    fun shareSongToFriends(platformTrackId: String, trackId: String) {
        viewModelScope.launch {
            shareRepository.shareMusic(platformTrackId, trackId, _comment.value, _selectedFriendIds.value.toList())
        }
    }
}