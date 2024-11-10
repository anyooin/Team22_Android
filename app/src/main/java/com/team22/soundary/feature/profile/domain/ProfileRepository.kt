package com.team22.soundary.feature.profile.domain

import android.net.Uri
import com.team22.soundary.core.domain.model.User
import kotlinx.coroutines.flow.Flow
import okhttp3.MultipartBody

interface ProfileRepository {
    suspend fun getProfiles(): Flow<User>

    suspend fun uploadImage(multipart : MultipartBody.Part) : String

    suspend fun editProfile(
        displayId: String,
        nickname: String,
        description: String?,
        imageId: String
    )

    suspend fun setLabels(labels: List<String>)

    suspend fun deleteUserAccount()
}
