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

    private val _selectedCategories = MutableStateFlow<List<String>>(emptyList())
    val selectedCategories: StateFlow<List<String>> = _selectedCategories.asStateFlow()


    private val _editedName = MutableStateFlow("")
    val editedName: StateFlow<String> = _editedName.asStateFlow()

    private val _editedIntro = MutableStateFlow("")
    val editedIntro: StateFlow<String> = _editedIntro.asStateFlow()

    private val _image = MutableStateFlow<Uri>(Uri.EMPTY)
    val image: StateFlow<Uri> = _image.asStateFlow()

    private val _sentShare = MutableStateFlow<List<Share>>(emptyList())
    val sentShare = _sentShare.asStateFlow()




    init {
        getProfile()
        getSentShare()
        Log.d("uin","vm init")
    }

    fun getSentShare(){
        viewModelScope.launch {
            sentShareRepository.getShareList().collect{
                _sentShare.value = it
            }
        }
    }
    fun getProfile() {
        viewModelScope.launch {
             profileRepository.getProfiles().collect{
                 _userInfo.value = it
                 Log.d("uin", "new" + _userInfo.value.statusMessage)
                 Log.d("uin", "new" + _userInfo.value.label)

            }
        }
    }

    // 선택된 카테고리 라벨을 추가
    fun addLabel(label: List<String>) {
        Log.d("dsdddd",""+label)
        viewModelScope.launch {
            profileRepository.addLabels(label)
            _selectedCategories.value += label
        }
    }

    fun getLabel(label: String) {

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

    fun deleteUserAccount() {
        viewModelScope.launch {
            profileRepository.deleteUserAccount()
        }
    }

    fun clearToken(){
        viewModelScope.launch {
            tokenRepository.clear()
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
