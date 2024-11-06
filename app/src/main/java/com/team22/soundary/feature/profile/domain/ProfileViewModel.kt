package com.team22.soundary.feature.profile.domain

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.team22.soundary.feature.profile.data.ProfileRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val profileRepository: ProfileRepository
) : ViewModel() {

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

    // 현재 라벨 목록을 조회
    fun loadLabels() {
        viewModelScope.launch {
            val response = profileRepository.getLabels()
            if (response.isSuccessful) {
                // 조회된 라벨 목록을 처리 (필요에 따라 UI 업데이트)
            }
        }
    }
}
