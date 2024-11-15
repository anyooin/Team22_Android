package com.team22.soundary

import com.team22.soundary.core.data.dto.FriendInfoDto
import com.team22.soundary.core.data.dto.FriendProfileDto
import com.team22.soundary.core.data.dto.FriendRequestDto
import com.team22.soundary.core.data.dto.FriendsResponse
import com.team22.soundary.core.data.dto.ReceivedRequestsResponse
import com.team22.soundary.core.data.dto.SentRequestsResponse
import com.team22.soundary.core.data.dto.UserInfoDto
import com.team22.soundary.core.data.dto.toVO
import com.team22.soundary.feature.search.data.remote.FriendApiService
import com.team22.soundary.feature.search.data.repository.FriendRepository
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import retrofit2.Response

class FriendRepositoryTest {

    private lateinit var repository: FriendRepository
    private val friendApiService: FriendApiService = mock()

    @Before
    fun setup() {
        repository = FriendRepository(friendApiService)
    }

    @Test
    fun `getFriends emits friend list from service response`() = runTest {
        // Mock FriendProfileDto list
        val mockFriendList = listOf(
            FriendProfileDto(
                id = "user1",
                displayId = "user1_display",
                name = "Friend 1",
                profile = "https://example.com/profile1.jpg",
                labels = listOf("label1")
            ),
            FriendProfileDto(
                id = "user2",
                displayId = "user2_display",
                name = "Friend 2",
                profile = "https://example.com/profile2.jpg",
                labels = listOf("label2")
            )
        )
        val response = Response.success(FriendsResponse(friends = mockFriendList))

        whenever(friendApiService.getFriends()).thenReturn(response)

        val result = repository.getFriends().take(1).toList().first()

        assertEquals(mockFriendList.map { it.toVO() }, result)
    }


    @Test
    fun `addFriend returns true when successful`() = runTest {
        val friendRequestDto = FriendRequestDto("targetDisplayId")
        val response = Response.success(Unit)

        whenever(friendApiService.addFriend(friendRequestDto)).thenReturn(response)

        val result = repository.addFriend(friendRequestDto)

        assertTrue(result)
    }

    @Test
    fun `getSentRequests emits sent friend requests from service response`() = runTest {
        val mockSentRequests = listOf(
            FriendInfoDto(
                id = "user3",
                displayId = "user3_display",
                name = "Sent Friend 1",
                profile = "https://example.com/profile3.jpg"
            )
        )

        val response = Response.success(SentRequestsResponse(sentRequests = mockSentRequests))

        whenever(friendApiService.getSentRequests()).thenReturn(response)

        val result = repository.getSentRequests().take(1).toList().first()

        assertEquals(mockSentRequests.map { it.toVO() }, result)
    }

    @Test
    fun `getReceivedRequests emits received friend requests from service response`() = runTest {
        val mockReceivedRequests = listOf(
            FriendInfoDto(
                id = "user4",
                displayId = "user4_display",
                name = "Received Friend 1",
                profile = "https://example.com/profile4.jpg"
            )
        )

        val response = Response.success(ReceivedRequestsResponse(receivedRequests = mockReceivedRequests))

        whenever(friendApiService.getReceivedRequests()).thenReturn(response)

        val result = repository.getReceivedRequests().take(1).toList().first()

        assertEquals(mockReceivedRequests.map { it.toVO() }, result)
    }
    @Test
    fun `getFriendById returns user when successful`() = runTest {
        val mockUserInfo = UserInfoDto(
            displayId = "user5",
            name = "Friend 5",
            description = "Description 5",
            profile = "https://example.com/profile5.jpg",
            roles = listOf("role5"),
            labels = listOf("label5")
        )
        val response = Response.success(mockUserInfo)

        whenever(friendApiService.searchUser("user5")).thenReturn(response)

        val result = repository.getFriendById("user5")

        assertEquals(mockUserInfo.toVO(), result)
    }

    @Test
    fun `removeFriend returns true when successful`() = runTest {
        val response = Response.success(Unit)

        whenever(friendApiService.removeFriend("user6")).thenReturn(response)

        val result = repository.removeFriend("user6")

        assertTrue(result)
    }

    @Test
    fun `rejectReceivedRequest returns true when successful`() = runTest {
        val response = Response.success(Unit)

        whenever(friendApiService.rejectReceivedRequest("user7")).thenReturn(response)

        val result = repository.rejectReceivedRequest("user7")

        assertTrue(result)
    }

    @Test
    fun `updateFriendStatus returns true when successful`() = runTest {
        val friendRequestDto = FriendRequestDto("user8")
        val response = Response.success(Unit)

        whenever(friendApiService.addFriend(friendRequestDto)).thenReturn(response)

        val result = repository.updateFriendStatus("user8")

        assertTrue(result)
    }

    @Test
    fun `searchUserByDisplayId returns user when successful`() = runTest {
        val mockUserInfo = UserInfoDto(
            displayId = "user9",
            name = "Search Result",
            description = "Description 9",
            profile = "https://example.com/profile9.jpg",
            roles = listOf("role9"),
            labels = listOf("label9")
        )
        val response = Response.success(mockUserInfo)

        whenever(friendApiService.searchUser("user9")).thenReturn(response)

        val result = repository.searchUserByDisplayId("user9")

        assertEquals(mockUserInfo.toVO(), result)
    }
}
