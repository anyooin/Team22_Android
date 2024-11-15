package com.team22.soundary

import com.team22.soundary.core.data.dto.FriendRequestDto
import com.team22.soundary.feature.search.data.api.FriendApiService
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Before
import org.junit.Test
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import kotlinx.coroutines.test.runTest
import junit.framework.TestCase.assertEquals

class FriendApiServiceTest {

    private lateinit var mockWebServer: MockWebServer
    private lateinit var friendApiService: FriendApiService

    @Before
    fun setup() {
        mockWebServer = MockWebServer()
        mockWebServer.start()
        friendApiService = Retrofit.Builder()
            .baseUrl(mockWebServer.url("/"))
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(FriendApiService::class.java)
    }

    @After
    fun teardown() {
        mockWebServer.shutdown()
    }

    @Test
    fun `getFriends returns expected friends data`() = runTest {
        val mockResponse = MockResponse()
            .setResponseCode(200)
            .setBody(
                """
            {
                "friends": [
                    {
                        "id": "1",
                        "display_id": "alice1234",
                        "nickname": "Alice",
                        "profile_image_url": "https://example.com/alice.jpg",
                        "labels": ["pop"]
                    }
                ]
            }
            """
            )
        mockWebServer.enqueue(mockResponse)

        // API 호출
        val response = friendApiService.getFriends()

        // 응답 확인
        assert(response.isSuccessful)
        val friends = response.body()?.friends
        assertEquals(1, friends?.size)
        assertEquals("1", friends?.first()?.id)
        assertEquals("alice1234", friends?.first()?.displayId)
        assertEquals("Alice", friends?.first()?.name)
        assertEquals("https://example.com/alice.jpg", friends?.first()?.profile)
        assertEquals(listOf("pop"), friends?.first()?.labels)
    }

    @Test
    fun `addFriend sends friend request successfully`() = runTest {
        val mockResponse = MockResponse().setResponseCode(200)
        mockWebServer.enqueue(mockResponse)

        // 친구 요청 보내기
        val friendRequest = FriendRequestDto(displayId = "bob123")
        val response = friendApiService.addFriend(friendRequest)

        // 응답 확인
        assert(response.isSuccessful)
    }

    @Test
    fun `rejectReceivedRequest returns successful response`() = runTest {
        val mockResponse = MockResponse().setResponseCode(200)
        mockWebServer.enqueue(mockResponse)

        // 친구 요청 거절
        val response = friendApiService.rejectReceivedRequest("friend2")

        // 응답 확인
        assert(response.isSuccessful)
    }

    @Test
    fun `searchUser returns expected user info`() = runTest {
        val mockResponse = MockResponse()
            .setResponseCode(200)
            .setBody(
                """
            {
                "display_id": "charlie123",
                "nickname": "Charlie",
                "description": null,
                "profile_image_url": "https://example.com/charlie.jpg",
                "roles": null,
                "labels": ["pop"]
            }
            """
            )
        mockWebServer.enqueue(mockResponse)

        // 사용자 검색
        val response = friendApiService.searchUser("charlie123")

        // 응답 확인
        assert(response.isSuccessful)
        val user = response.body()
        assertEquals("charlie123", user?.displayId)
        assertEquals("Charlie", user?.name)
        assertEquals(null, user?.description)
        assertEquals("https://example.com/charlie.jpg", user?.profile)
        assertEquals(null, user?.roles)
        assertEquals(listOf("pop"), user?.labels)
    }
}