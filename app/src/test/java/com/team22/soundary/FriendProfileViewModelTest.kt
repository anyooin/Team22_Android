package com.team22.soundary

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.team22.soundary.core.domain.model.User
import com.team22.soundary.feature.search.data.repository.FriendRepository
import com.team22.soundary.feature.search.presentation.profile.FriendProfileViewModel
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import kotlin.test.assertNull

@OptIn(ExperimentalCoroutinesApi::class)
class FriendProfileViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = StandardTestDispatcher()

    private lateinit var viewModel: FriendProfileViewModel
    private lateinit var friendRepository: FriendRepository

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        friendRepository = mockk()
        viewModel = FriendProfileViewModel(friendRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `loadFriendProfile should update friendProfile with user data when successful`() = runTest {
        // Given
        val friendId = "friend123"
        val user = User(id = friendId, displayId = "Friend Display Name")
        coEvery { friendRepository.getFriendById(friendId) } returns user

        // When
        viewModel.loadFriendProfile(friendId)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        val result = viewModel.friendProfile.first()
        assertEquals(user, result)
        coVerify { friendRepository.getFriendById(friendId) }
    }

    @Test
    fun `loadFriendProfile should set friendProfile to null when exception occurs`() = runTest {
        // Given
        val friendId = "friend123"
        coEvery { friendRepository.getFriendById(friendId) } throws Exception("Profile not found")

        // When
        viewModel.loadFriendProfile(friendId)
        testDispatcher.scheduler.advanceUntilIdle()

        // Then
        val result = viewModel.friendProfile.first()
        assertNull(result)
        coVerify { friendRepository.getFriendById(friendId) }
    }
}
