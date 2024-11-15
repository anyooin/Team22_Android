package com.team22.soundary

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.team22.soundary.core.domain.model.User
import com.team22.soundary.feature.search.data.repository.FriendRepository
import com.team22.soundary.feature.search.presentation.friend.FriendSearchViewModel
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class FriendSearchViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = StandardTestDispatcher()

    private lateinit var viewModel: FriendSearchViewModel
    private lateinit var friendRepository: FriendRepository

    @Before
    fun setup() = runTest {
        Dispatchers.setMain(testDispatcher)

        friendRepository = mockk(relaxed = true)
        viewModel = FriendSearchViewModel(friendRepository)

        // 기본 mock 설정
        coEvery { friendRepository.getFriends() } returns flowOf(emptyList())
        coEvery { friendRepository.getReceivedRequests() } returns flowOf(emptyList())
        coEvery { friendRepository.getSentRequests() } returns flowOf(emptyList())
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `loadFriends should populate myFriends, newFriends, and pendingFriends`() = runTest {
        val myFriends = listOf(User(displayId = "friend1"), User(displayId = "friend2"))
        val newFriends = listOf(User(displayId = "newFriend"))
        val pendingFriends = listOf(User(displayId = "pendingFriend"))

        coEvery { friendRepository.getFriends() } returns flowOf(myFriends)
        coEvery { friendRepository.getReceivedRequests() } returns flowOf(newFriends)
        coEvery { friendRepository.getSentRequests() } returns flowOf(pendingFriends)

        viewModel.loadFriends()
        testDispatcher.scheduler.advanceUntilIdle() // 비동기 작업 완료 대기

        assertEquals(myFriends, viewModel.myFriends.value)
        assertEquals(newFriends, viewModel.newFriends.value)
        assertEquals(pendingFriends, viewModel.pendingFriends.value)
    }

    @Test
    fun `isFriend should return true if user is in any friend list`() = runTest {
        val user = User(displayId = "friend1")
        coEvery { friendRepository.getFriends() } returns flowOf(listOf(user))

        // Load friends to update _myFriends
        viewModel.loadFriends()
        testDispatcher.scheduler.advanceUntilIdle()

        val result = viewModel.isFriend(user)
        assertTrue(result)
    }

    @Test
    fun `searchUser should update searchResultList with found user`() = runTest {
        val user = User(displayId = "searchUser")
        coEvery { friendRepository.searchUserByDisplayId("searchUser") } returns user

        viewModel.searchUser("searchUser")
        testDispatcher.scheduler.advanceUntilIdle() // 비동기 작업 완료 대기

        assertEquals(listOf(user), viewModel.searchResultList.value)
    }

    @Test
    fun `acceptFriend should update friend status and reload friends`() = runTest {
        val friend = User(displayId = "friend1")
        coEvery { friendRepository.updateFriendStatus("friend1") } returns true

        viewModel.acceptFriend(friend)
        testDispatcher.scheduler.advanceUntilIdle() // 비동기 작업 완료 대기

        coVerify { friendRepository.updateFriendStatus("friend1") }
        coVerify { friendRepository.getFriends() } // 친구 목록 갱신 확인
    }

    @Test
    fun `declineFriend should reject friend request and reload friends`() = runTest {
        val friend = User(id = "1", displayId = "friend2")
        coEvery { friendRepository.rejectReceivedRequest("1") } returns true

        viewModel.declineFriend(friend)
        testDispatcher.scheduler.advanceUntilIdle()

        coVerify { friendRepository.rejectReceivedRequest("1") }
        coVerify { friendRepository.getFriends() }
    }

    @Test
    fun `deleteFriend should remove friend and reload friends`() = runTest {
        val friend = User(id = "2", displayId = "friend3")
        coEvery { friendRepository.removeFriend("2") } returns true

        viewModel.deleteFriend(friend)
        testDispatcher.scheduler.advanceUntilIdle()

        coVerify { friendRepository.removeFriend("2") }
        coVerify { friendRepository.getFriends() }
    }
}
