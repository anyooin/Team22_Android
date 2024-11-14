package com.team22.soundary

import com.team22.soundary.core.data.dto.SearchTrackResponse
import com.team22.soundary.core.data.dto.SearchTrackResponseDto
import com.team22.soundary.core.data.dto.toVO
import com.team22.soundary.feature.share.data.MusicRepositoryImpl
import com.team22.soundary.feature.share.data.remote.MusicService
import junit.framework.TestCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import org.robolectric.RobolectricTestRunner
import retrofit2.Response

@RunWith(RobolectricTestRunner::class)
class MusicRepositoryTest {

    private lateinit var repository: MusicRepositoryImpl
    private val retrofitService: MusicService = mock()

    @Before
    fun setup() {
        repository = MusicRepositoryImpl(Dispatchers.Unconfined, retrofitService)
    }

    @Test
    fun `getMusicList emits music list from service response`() = runTest {
        // Mock된 User 및 TrackDto 생성
        val mockTrackDto = SearchTrackResponseDto(
            platformTrackId = "track1",
            title = "Test Track",
            artist = listOf("Artist"),
            duration = 180,
            albumCoverUrl = "https://example.com/cover.jpg",
            previewMp3Url = "https://example.com/preview.mp3"
        )

        val mockMusicList = listOf(mockTrackDto)
        val musicListDto = SearchTrackResponse(
            trackList = mockMusicList
        )

        val response = Response.success(musicListDto)

        // whenever 사용하여 mock 설정
        whenever(retrofitService.requestMusicList(query = "test")).thenReturn(response)

        val result = repository.getMusicList("test").first()

        // result가 기대하는 값으로 변환되었는지 확인
        TestCase.assertEquals(mockMusicList.map { it.toVO() }, result)
        TestCase.assertEquals(mockMusicList.map { it.toVO() }, result)
        TestCase.assertEquals(mockMusicList.map { it.toVO() }, result)
    }
}