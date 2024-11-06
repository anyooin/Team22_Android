package com.team22.soundary.feature.share.data

import com.team22.soundary.core.IODispatcher
import com.team22.soundary.core.data.dto.ShareMusicRequest
import com.team22.soundary.core.data.dto.ShareMusicResponse
import com.team22.soundary.core.data.dto.TrackIdentifierDto
import com.team22.soundary.feature.share.data.remote.ShareService
import com.team22.soundary.feature.share.domain.ShareRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import java.lang.IllegalStateException
import javax.inject.Inject

class ShareRepositoryImpl @Inject constructor(
    @IODispatcher private val dispatcher: CoroutineDispatcher,
    private val retrofitService: ShareService
) : ShareRepository {
    override suspend fun shareMusic(
        platformTrackId: String,
        comment: String,
        userList: List<String>
    ): Flow<Result<ShareMusicResponse>> = flow {
        val response = withContext(dispatcher) {
            retrofitService.requestShareMusic(
                ShareMusicRequest(
                    track = TrackIdentifierDto("SPOTIFY", platformTrackId),
                    comment = comment,
                    userList = userList
                )
            )
        }

        emit(runCatching {
            when {
                response.isSuccessful -> {
                    response.body() ?: throw IllegalStateException("share music failed")
                }

                else -> {
                    throw IllegalStateException("share music failed")
                }
            }
        })
    }
}