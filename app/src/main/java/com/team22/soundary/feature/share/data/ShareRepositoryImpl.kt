package com.team22.soundary.feature.share.data

import android.util.Log
import com.team22.soundary.core.IODispatcher
import com.team22.soundary.core.data.dto.ShareMusicRequest
import com.team22.soundary.core.data.dto.PlatformTrackIdentifierDto
import com.team22.soundary.feature.share.data.remote.ShareService
import com.team22.soundary.feature.share.domain.ShareRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import java.lang.IllegalStateException
import javax.inject.Inject

class ShareRepositoryImpl @Inject constructor(
    @IODispatcher private val dispatcher: CoroutineDispatcher,
    private val retrofitService: ShareService
) : ShareRepository {
    override suspend fun shareMusic(
        platformTrackId: String,
        trackId: String,
        comment: String,
        userList: List<String>
    ) {
        val response = withContext(dispatcher) {
            if (platformTrackId != "") {
                retrofitService.requestShareMusic(
                    ShareMusicRequest(
                        track = PlatformTrackIdentifierDto("SPOTIFY", platformTrackId),
                        comment = comment,
                        userList = userList
                    )
                )
            } else {
                retrofitService.requestShareMusic(
                    ShareMusicRequest(
                        trackId = trackId,
                        comment = comment,
                        userList = userList
                    )
                )
            }
        }

        when {
            response.isSuccessful -> {
                Log.d("uin", "노래공유성공!")
                response.body() ?: throw IllegalStateException("share music failed")
            }

            else -> {
                Log.d("uin", "노래공유실패!")
                throw IllegalStateException("share music failed")
            }
        }

    }
}