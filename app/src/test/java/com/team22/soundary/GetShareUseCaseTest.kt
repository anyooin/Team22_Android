package com.team22.soundary

import android.net.Uri
import com.team22.soundary.core.domain.model.Share
import com.team22.soundary.core.domain.model.User
import com.team22.soundary.core.domain.model.Song
import com.team22.soundary.feature.main.domain.GetShareUseCase
import com.team22.soundary.feature.main.domain.ReceivedShareRepository
import com.team22.soundary.feature.main.domain.SentShareRepository
import com.team22.soundary.feature.main.domain.UserRepository
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.*
import org.mockito.kotlin.mock
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class GetShareUseCaseTest {

    private lateinit var getShareUseCase: GetShareUseCase
    private val sentShareRepository: SentShareRepository = mock()
    private val receivedShareRepository: ReceivedShareRepository = mock()
    private val userRepository: UserRepository = mock()

    @Before
    fun setup() {
        getShareUseCase = GetShareUseCase(sentShareRepository, receivedShareRepository, userRepository)
    }

    @Test
    fun `invoke combines data from all repositories`() = runTest {
        // Song 객체 생성 시 Uri.EMPTY 대신 문자열 사용
        val sampleSong1 = Song(
            id = "songId1",
            title = "Sample Song 1",
            artist = listOf("Artist1"),
            preview = Uri.parse("https://example.com/sample1.mp3"),
            coverImage = Uri.parse("https://example.com/sample1.jpg")
        )
        val sampleSong2 = Song(
            id = "songId2",
            title = "Sample Song 2",
            artist = listOf("Artist2"),
            preview = Uri.parse("https://example.com/sample2.mp3"),
            coverImage = Uri.parse("https://example.com/sample2.jpg")
        )

        // Share 객체에 Song을 명시적으로 설정
        val sentShares = listOf(Share(id = "1", song = sampleSong1))
        val receivedShares = listOf(Share(id = "2", song = sampleSong2))
        val user = User(id = "user1", name = "Friend") // 이름을 "Friend"로 설정

        `when`(sentShareRepository.getShareList()).thenReturn(flowOf(sentShares))
        `when`(receivedShareRepository.getShareList()).thenReturn(flowOf(receivedShares))
        `when`(userRepository.getMyInfo()).thenReturn(flowOf(user))

        val result = getShareUseCase.invoke()

        result.collect { combined ->
            println(combined)

        }
    }
}
