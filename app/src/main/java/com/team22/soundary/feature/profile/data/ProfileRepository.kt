package com.team22.soundary.feature.profile.data

import com.team22.soundary.core.data.dto.LabelAdd
import javax.inject.Inject

class ProfileRepository @Inject constructor(
    private val apiService: ProfileApiService
) {
    suspend fun getProfileData() = apiService.getProfile()
    suspend fun getLabels() = apiService.getLabels()
    suspend fun addLabels(labels: List<String>) = apiService.addLabels(LabelAdd(labels))
    suspend fun deleteLabel(label: String) = apiService.deleteLabel(label)
}
