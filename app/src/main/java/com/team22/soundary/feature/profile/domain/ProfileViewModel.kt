package com.team22.soundary.feature.profile.domain

import android.provider.ContactsContract.Profile
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.team22.soundary.core.data.dto.UserInfoResponse
import com.team22.soundary.core.domain.model.User
import com.team22.soundary.feature.profile.data.ProfileRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val profileRepository: ProfileRepository
) : ViewModel() {
    private val _userInfo = MutableStateFlow<User>(User())
    val userInfo: StateFlow<User> = _userInfo.asStateFlow()

    init {
        getProfile()
    }
    fun getProfile() {
        viewModelScope.launch {
             profileRepository.getProfiles().collect{
                 Log.d("uin", "" + it.statusMessage)
                 _userInfo.value = it
                 Log.d("uin", "" + _userInfo.value.statusMessage)

            }
        }
    }

    // 선택된 카테고리 라벨을 추가
    fun addLabel(label: String) {
        viewModelScope.launch {
            profileRepository.addLabels(listOf(label))
        }
    }

    // 선택된 카테고리 라벨을 삭제
    fun deleteLabel(label: String) {
        viewModelScope.launch {
            profileRepository.deleteLabel(label)
        }
    }


}
