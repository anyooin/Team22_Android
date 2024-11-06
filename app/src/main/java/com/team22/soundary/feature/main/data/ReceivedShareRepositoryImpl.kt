package com.team22.soundary.feature.main.data

import android.util.Log
import com.team22.soundary.core.IODispatcher
import com.team22.soundary.core.data.dto.toVO
import com.team22.soundary.core.domain.model.Share
import com.team22.soundary.feature.main.data.remote.ShareService
import com.team22.soundary.feature.main.domain.ReceivedShareRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import javax.inject.Inject

internal class ReceivedShareRepositoryImpl @Inject constructor(
    @IODispatcher private val dispatcher: CoroutineDispatcher,
    private val shareService: ShareService
) : ReceivedShareRepository {
    override suspend fun getShareList(): Flow<List<Share>> = flow {
        val response = withContext(dispatcher) {
            shareService.requestReceiveShare()
        }

        if (response.isSuccessful) {
            emit(
                response.body()?.shareList?.map { it.toVO() } ?: emptyList()
            )
        } else {
            Log.d("akuby21",""+response)
            throw Exception("Error: ${response.message()}")
        }


    }

    override suspend fun likeMusic(musicId: String){
        val response = shareService.requestMusicLike(musicId)

        if(!response.isSuccessful) throw Exception("Error: ${response.message()}")
    }

    override suspend fun deleteLikeMusic(musicId: String) {
        val response = shareService.deleteMusicLike(musicId)
        if(!response.isSuccessful) throw Exception("Error: ${response.message()}")
    }

}

