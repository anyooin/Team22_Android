package com.team22.soundary

import com.team22.soundary.core.data.dto.FromUserResponse
import com.team22.soundary.core.domain.model.Share
import com.team22.soundary.feature.main.data.ReceivedShareRepositoryImpl
import com.team22.soundary.feature.main.data.remote.ShareService
import com.team22.soundary.core.data.dto.ReceivedShareListDto
import com.team22.soundary.core.data.dto.ReceivedShareDto
import com.team22.soundary.core.data.dto.TrackDto
import com.team22.soundary.core.data.dto.toVO
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import retrofit2.Response
import java.util.Date

class ReceivedShareRepositoryImplTest {

    private lateinit var repository: ReceivedShareRepositoryImpl
    private val shareService: ShareService = mock()

    @Before
    fun setup() {
        repository = ReceivedShareRepositoryImpl(Dispatchers.Unconfined, shareService)
    }

    @Test
    fun `getShareList emits share list from service response`() = runTest {
        // Mock된 User 및 TrackDto 생성
        val mockUserDto = FromUserResponse(
            id = "user1",
            displayName = "Test User",
            name = "Test Name",
            profileImageUrl = "https://example.com/profile.jpg"
        )

        val mockTrackDto = TrackDto(
            trackId = "track1",
            title = "Test Track",
            artist = listOf("Artist"),
            duration = 180,
            albumCoverUrl = "https://example.com/cover.jpg",
            previewMp3Url = "https://example.com/preview.mp3"
        )

        // 직접 생성한 ReceivedShareDto 리스트 사용
        val mockShareDto = ReceivedShareDto(
            id = "1",
            fromUser = mockUserDto,  // Mock된 User 객체
            track = mockTrackDto,     // Mock된 TrackDto 객체
            comment = "Test comment",
            sharedAt = Date(),
            isLiked = false
        )

        val mockShareList = listOf(mockShareDto)
        val receivedShareListDto = ReceivedShareListDto(
            total = 1,
            totalPage = 1,
            shareList = mockShareList
        )

        val response = Response.success(receivedShareListDto)

        // whenever 사용하여 mock 설정
        whenever(shareService.requestReceiveShare()).thenReturn(response)

        val result = repository.getShareList().first()

        // result가 기대하는 값으로 변환되었는지 확인
        assertEquals(mockShareList.map { it.toVO() }, result)
    }

}
