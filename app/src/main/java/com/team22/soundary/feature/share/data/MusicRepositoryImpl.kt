package com.team22.soundary.feature.share.data

import com.team22.soundary.core.IODispatcher
import com.team22.soundary.core.data.dto.toVO
import com.team22.soundary.core.domain.model.Song
import com.team22.soundary.feature.share.data.remote.MusicService
import com.team22.soundary.feature.share.domain.MusicRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import javax.inject.Inject

class MusicRepositoryImpl @Inject constructor(
    @IODispatcher private val dispatcher: CoroutineDispatcher,
    private val retrofitService: MusicService
): MusicRepository {

    override suspend fun getMusicList(query: String): Flow<List<Song>> = flow {
        val response = withContext(dispatcher) {
            retrofitService.requestMusicList(query = query)
        }

        if (response.isSuccessful) {
            emit(response.body()?.trackList?.map { it.toVO() } ?: emptyList())
        } else {
            throw Exception("Error: ${response.message()}")
        }
    }

    override suspend fun getMostSharedMusicList(): Flow<List<Song>> = flow {
        val response = withContext(dispatcher) {
            retrofitService.requestMostSharedMusicList()
        }

        if (response.isSuccessful) {
            emit(response.body()?.trackList?.map { it.toVO() } ?: emptyList())
        } else {
            throw Exception("Error: ${response.message()}")
        }
    }

    override suspend fun getMostLikedMusicList(): Flow<List<Song>> = flow {
        val response = withContext(dispatcher) {
            retrofitService.requestMostLikedMusicList()
        }

        if (response.isSuccessful) {
            emit(response.body()?.trackList?.map { it.toVO() } ?: emptyList())
        } else {
            throw Exception("Error: ${response.message()}")
        }
    }
}