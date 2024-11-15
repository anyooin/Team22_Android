package com.team22.soundary.feature.main.data

import com.team22.soundary.core.IODispatcher
import com.team22.soundary.core.data.dto.toVO
import com.team22.soundary.core.domain.model.Share
import com.team22.soundary.feature.main.data.remote.ShareService
import com.team22.soundary.feature.main.domain.SentShareRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import javax.inject.Inject

internal class SentShareRepositoryImpl @Inject constructor(
    @IODispatcher private val dispatcher: CoroutineDispatcher,
    private val retrofitService: ShareService
) : SentShareRepository {
    override suspend fun getShareList(): Flow<List<Share>> = flow {
        val response = withContext(dispatcher) {
            retrofitService.requestSentShare()
        }

        if (response.isSuccessful) {
            emit(
                response.body()?.shareList?.map { it.toVO() } ?: emptyList()
            )
        } else {
            throw Exception("Error: ${response.code()} ${response.message()}")
        }

    }
}