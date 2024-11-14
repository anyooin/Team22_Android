package com.team22.soundary

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.team22.soundary.core.UiState
import com.team22.soundary.core.domain.model.Share
import com.team22.soundary.core.domain.model.User
import com.team22.soundary.core.domain.model.Song
import com.team22.soundary.feature.main.domain.GetShareUseCase
import com.team22.soundary.feature.main.domain.LikeSongUseCase
import com.team22.soundary.feature.main.domain.ReceivedShareRepository
import com.team22.soundary.feature.main.domain.SentShareRepository
import com.team22.soundary.feature.main.domain.UserRepository
import com.team22.soundary.feature.main.presentation.MainUiState
import com.team22.soundary.feature.main.presentation.MainViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.kotlin.mock
import android.net.Uri
import junit.framework.TestCase.assertTrue
import org.robolectric.RobolectricTestRunner
import org.junit.runner.RunWith
import org.mockito.Mockito.`when`

@RunWith(RobolectricTestRunner::class)
@OptIn(ExperimentalCoroutinesApi::class)
class MainViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = StandardTestDispatcher()

    private lateinit var viewModel: MainViewModel
    private lateinit var getShareUseCase: GetShareUseCase
    private lateinit var likeSongUseCase: LikeSongUseCase

    private val sentShareRepository: SentShareRepository = mock()
    private val receivedShareRepository: ReceivedShareRepository = mock()
    private val userRepository: UserRepository = mock()

    @Before
    fun setup() = runTest {
        Dispatchers.setMain(testDispatcher)

        // UseCase와 ViewModel 초기화
        getShareUseCase = GetShareUseCase(sentShareRepository, receivedShareRepository, userRepository)
        likeSongUseCase = LikeSongUseCase(receivedShareRepository)
        viewModel = MainViewModel(getShareUseCase, likeSongUseCase)

        // Flow 기본값 설정
        `when`(sentShareRepository.getShareList()).thenReturn(flowOf(emptyList()))
        `when`(receivedShareRepository.getShareList()).thenReturn(flowOf(emptyList()))
        `when`(userRepository.getMyInfo()).thenReturn(flowOf(User(id = "default_user")))
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `likeMusic toggles like state`() = runTest {
        // 임시로 대체할 Uri 설정
        val sampleSong = Song(
            id = "song1",
            title = "Test Song",
            artist = listOf("Test Artist"),
            preview = Uri.parse("mock-uri"),
            coverImage = Uri.parse("mock-uri")
        )

        // Share 객체 생성
        val share = Share(
            id = "1",
            song = sampleSong,
            isLike = false,
            friend = User(id = "user1")
        )

        // Flow 기본값 설정
        `when`(sentShareRepository.getShareList()).thenReturn(flowOf(listOf(share)))
        `when`(receivedShareRepository.getShareList()).thenReturn(flowOf(emptyList()))
        `when`(userRepository.getMyInfo()).thenReturn(flowOf(User(id = "user1")))

        // 테스트 환경에서 ViewModel의 초기 상태를 설정
        viewModel = MainViewModel(getShareUseCase, likeSongUseCase)

        // 모든 비동기 작업 완료 대기
        testDispatcher.scheduler.advanceUntilIdle()

        // likeMusic 함수 호출
        viewModel.likeMusic()

        // UI 상태의 변경 확인
        val state = viewModel.uiState.value
        if (state is UiState.Success) {
            assertTrue(state.data.share.isLike)
        } else {
            throw AssertionError("Expected UiState.Success but found $state")
        }
    }

}
