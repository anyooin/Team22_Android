package com.team22.soundary.feature.profile.domain

import android.net.Uri
import com.team22.soundary.core.domain.model.User
import kotlinx.coroutines.flow.Flow

interface ProfileRepository {
    suspend fun getProfiles() : Flow <User>
    suspend fun addLabels(labels: List<String>)
    suspend fun deleteLabel(label: String)

    suspend fun deleteUserAccount()

    suspend fun editedProfile(displayId: String, nickname: String, description: String?, profileUri: Uri?)
}
