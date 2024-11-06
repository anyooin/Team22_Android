package com.team22.soundary.feature.profile.data

import android.util.Log
import com.team22.soundary.core.IODispatcher
import com.team22.soundary.core.data.dto.LabelAdd
import com.team22.soundary.core.data.dto.toVO
import com.team22.soundary.core.domain.model.User
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import javax.inject.Inject

class ProfileRepositoryImpl @Inject constructor(
    @IODispatcher private val dispatcher: CoroutineDispatcher,
    private val apiService: ProfileApiService
): ProfileRepository {
    override suspend fun getProfiles(): Flow<User> = flow{
        val response = withContext(dispatcher) {
            apiService.getProfile()
        }

        if (response.isSuccessful) {
            emit(response.body()?.toVO() ?: User())
            if(response.body() == null) {
                Log.d("uin", "error")
            }
            else {
                Log.d("uin", "ok" + response.body()!!.description)
            }

        } else {
            throw Exception("Error: ${response.message()}")
        }
    }

    override suspend fun addLabels(labels: List<String>) {
        val response = withContext(dispatcher) {
            apiService.addLabels(LabelAdd(labels))
        }

        if (!response.isSuccessful) {
            throw Exception("Error: ${response.message()}")
        }
    }

    override suspend fun deleteLabel(label: String) {
        val response = withContext(dispatcher) {
            apiService.deleteLabel(label)
        }

        if (!response.isSuccessful) {
            throw Exception("Error: ${response.message()}")
        }
    }

}