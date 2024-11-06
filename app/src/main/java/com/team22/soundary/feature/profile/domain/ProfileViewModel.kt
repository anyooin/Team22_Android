package com.team22.soundary.feature.profile.domain

import android.net.Uri
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

    private val _selectedCategories = MutableStateFlow<List<String>>(emptyList())
    val selectedCategories: StateFlow<List<String>> = _selectedCategories.asStateFlow()


    private val _editedName = MutableStateFlow("")
    val editedName: StateFlow<String> = _editedName.asStateFlow()

    private val _editedIntro = MutableStateFlow("")
    val editedIntro: StateFlow<String> = _editedIntro.asStateFlow()

    private val _image = MutableStateFlow<Uri>(Uri.EMPTY)
    val image: StateFlow<Uri> = _image.asStateFlow()

    init {
        getProfile()
        Log.d("uin","vm init")
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
            _selectedCategories.value = _selectedCategories.value + label
        }
    }

    // 선택된 카테고리 라벨을 삭제
    fun deleteLabel(label: String) {
        viewModelScope.launch {
            profileRepository.deleteLabel(label)
            _selectedCategories.value = _selectedCategories.value - label
        }
    }

    fun editedProfile(displayId: String, nickname: String, description: String?, profileUri: Uri?) {
        viewModelScope.launch {
            profileRepository.editedProfile(
                displayId = displayId,
                nickname = nickname,
                description = description,
                profileUri = profileUri
            )
        }
    }


    fun setProfile(name: String, intro: String, profile: Uri) {
        val displayId = _userInfo.value.id

        viewModelScope.launch {
            try {
                // 서버에 업데이트 요청
                profileRepository.editedProfile(
                    displayId = displayId,
                    nickname = name,
                    description = intro,
                    profileUri = profile
                )
                // 업데이트 성공 시 _userInfo 상태 갱신
                _userInfo.value = _userInfo.value.copy(
                    name = name,
                    statusMessage = intro,
                    image = profile
                )
            } catch (e: Exception) {
                Log.e("ProfileViewModel", "Error updating profile: ${e.message}")
            }
        }
    }




}
