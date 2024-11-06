package com.team22.soundary.feature.profile.data

import javax.inject.Inject

class ProfileRepository @Inject constructor(
    private val apiService: ProfileApiService
) {
    suspend fun getProfileData() = apiService.getProfile()
    suspend fun getLabels() = apiService.getLabels()
    suspend fun addLabels(labels: List<String>) = apiService.addLabels(LabelsRequest(labels))
    suspend fun deleteLabel(label: String) = apiService.deleteLabel(label)
}
