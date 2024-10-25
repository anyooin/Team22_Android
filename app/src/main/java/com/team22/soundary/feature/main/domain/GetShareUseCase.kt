package com.team22.soundary.feature.main.domain

import android.util.Log
import com.team22.soundary.core.domain.model.Share
import dagger.Component
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.io.IOException
import javax.inject.Inject


class GetShareUseCase @Inject constructor(
    private val sentShareRepository: SentShareRepository,
    private val receivedShareRepository: ReceivedShareRepository,
    private val userRepository: UserRepository
) {
    suspend fun invoke(): Flow<Result<Map<String, List<Share>>>> =
        combine(
            sentShareRepository.getShareList(),
            receivedShareRepository.getShareList(),
            userRepository.getMyInfo()
        ) { sent, receive, me ->
            runCatching {
                if (sent.isFailure || receive.isFailure || me.isFailure) {
                    val errorMessages = mutableListOf<String>()
                    sent.exceptionOrNull()?.let { errorMessages.add(it.message ?: "Unknown error") }
                    receive.exceptionOrNull()?.let { errorMessages.add(it.message ?: "Unknown error") }
                    me.exceptionOrNull()?.let { errorMessages.add(it.message ?: "Unknown error") }

                    throw Exception("Errors occurred: ${errorMessages.joinToString(", ")}")
                }

                val sentShares = sent.getOrThrow()
                val myInfo = me.getOrThrow().copy(name="나")
                val updatedSentShares = sentShares.map {
                    it.copy(friend = myInfo)
                }
                val combinedShares = updatedSentShares + receive.getOrThrow()

                combinedShares.groupBy { it.friend.name }
            }
        }


}