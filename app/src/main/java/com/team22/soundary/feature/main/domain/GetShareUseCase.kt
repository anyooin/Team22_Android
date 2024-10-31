package com.team22.soundary.feature.main.domain

import com.team22.soundary.core.domain.model.Share
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject


class GetShareUseCase @Inject constructor(
    private val sentShareRepository: SentShareRepository,
    private val receivedShareRepository: ReceivedShareRepository,
    private val userRepository: UserRepository
) {
    suspend fun invoke(): Flow<Map<String, List<Share>>> =
        combine(
            sentShareRepository.getShareList(),
            receivedShareRepository.getShareList(),
            userRepository.getMyInfo()
        ) { sent, receive, me ->
            val modifiedSent : MutableList<Share> = mutableListOf()
            sent.forEach{
                modifiedSent.add(
                    it.copy(
                        friend = me
                    )
                )
            }

            val combinedShare = modifiedSent + receive
            combinedShare.groupBy {
                it.friend.name
            }
        }

}