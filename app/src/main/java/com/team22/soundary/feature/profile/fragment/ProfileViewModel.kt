package com.team22.soundary.feature.profile.fragment

import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.team22.soundary.core.domain.TokenRepository
import com.team22.soundary.core.domain.model.Share
import com.team22.soundary.core.domain.model.User
import com.team22.soundary.feature.main.domain.SentShareRepository
import com.team22.soundary.feature.profile.domain.ProfileRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val profileRepository: ProfileRepository,
    private val sentShareRepository: SentShareRepository,
    private val tokenRepository: TokenRepository
) : ViewModel() {
    private val _userInfo = MutableStateFlow<User>(User())
    val userInfo: StateFlow<User> = _userInfo.asStateFlow()

    private val _sentShare = MutableStateFlow<List<Share>>(emptyList())
    val sentShare : StateFlow<List<Share>> = _sentShare.asStateFlow()

    init {
        getProfile()
        getSentShare()
    }

    fun getProfile() {
        viewModelScope.launch {
             profileRepository.getProfiles().collectLatest{
                 _userInfo.value = it
            }
        }
    }

    private fun getSentShare(){
        viewModelScope.launch {
            sentShareRepository.getShareList().collectLatest{
                _sentShare.value = it
            }
        }
    }

    // 선택된 카테고리 라벨을 추가
    suspend fun setLabel(label: List<String>) {
        Log.d("uin","라벨"+label)
        viewModelScope.launch {
            profileRepository.setLabels(label)
        }
    }

    suspend fun deleteUserAccount() {
        viewModelScope.launch {
            profileRepository.deleteUserAccount()
        }
    }

    suspend fun clearToken(){
        viewModelScope.launch {
            tokenRepository.clear()
        }
    }

    suspend fun editProfile(nickname: String, intro: String, profile: Uri) {
        val displayId = _userInfo.value.displayId

        viewModelScope.launch {
            try {
                profileRepository.editProfile(
                    displayId = displayId,
                    nickname = nickname,
                    description = intro,
                    profileUri = profile
                )
            } catch (e: Exception) {
                Log.e("uin", "Error updating profile: ${e.message}")
            }
        }
    }
}
