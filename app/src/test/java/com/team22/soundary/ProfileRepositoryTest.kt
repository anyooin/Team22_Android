package com.team22.soundary

import com.team22.soundary.core.data.dto.UserInfoDto
import com.team22.soundary.core.data.dto.toVO
import com.team22.soundary.core.domain.model.User
import com.team22.soundary.feature.profile.data.ProfileRepositoryImpl
import com.team22.soundary.feature.profile.data.remote.ProfileApiService
import junit.framework.TestCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import retrofit2.Response

class ProfileRepositoryTest {

    private lateinit var repository: ProfileRepositoryImpl
    private val retrofitService: ProfileApiService = mock()

    @Before
    fun setup() {
        repository = ProfileRepositoryImpl(Dispatchers.Unconfined, retrofitService)
    }

    @Test
    fun `getProfile emits user from service response`() = runTest {
        // Mock된 User 및 TrackDto 생성
        val mockUserDto = UserInfoDto(
            displayId = "user1",
            name = "Test User",
            description = "Test description",
            profile = "https://example.com/cover.jpg",
            roles = listOf("user"),
            labels = listOf("pop")
        )

        val response = Response.success(mockUserDto)

        // whenever 사용하여 mock 설정
        whenever(retrofitService.getProfile()).thenReturn(response)

        var result = User()
        repository.getProfiles().collect {
            result = it
        }

        // result가 기대하는 값으로 변환되었는지 확인
        TestCase.assertEquals(mockUserDto.toVO(), result)
    }
}