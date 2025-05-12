package com.agriconnect

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.agriconnect.data.models.UserProfile
import com.agriconnect.data.repository.UserRepository
import com.agriconnect.ui.profile.ProfileLoadState
import com.agriconnect.ui.profile.ProfileUpdateState
import com.agriconnect.ui.profile.ProfileViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.Mockito.verify
import org.mockito.MockitoAnnotations

@ExperimentalCoroutinesApi
class ProfileViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = StandardTestDispatcher()

    @Mock
    private lateinit var mockUserRepository: UserRepository

    private lateinit var viewModel: ProfileViewModel

    private val testUserProfile = UserProfile("uid123", "Test User", "test@example.com", "Test Farm Location")

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        Dispatchers.setMain(testDispatcher)
        viewModel = ProfileViewModel(mockUserRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `UT_PROFILE_001 verify ProfileViewModel correctly loads user profile data`() = runTest {
        `when`(mockUserRepository.getUserProfile()).thenReturn(flowOf(Result.success(testUserProfile)))

        viewModel.loadUserProfile()
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.profileLoadState.value
        assertTrue(state is ProfileLoadState.Success)
        assertEquals(testUserProfile, (state as ProfileLoadState.Success).userProfile)
        assertNull(viewModel.profileUpdateState.value.error) // Ensure update state is not affected
    }

    @Test
    fun `UT_PROFILE_002 verify ProfileViewModel handles profile data loading error`() = runTest {
        val errorMessage = "Failed to load profile"
        `when`(mockUserRepository.getUserProfile()).thenReturn(flowOf(Result.failure(Exception(errorMessage))))

        viewModel.loadUserProfile()
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.profileLoadState.value
        assertTrue(state is ProfileLoadState.Error)
        assertEquals(errorMessage, (state as ProfileLoadState.Error).message)
    }

    @Test
    fun `UT_PROFILE_003 verify ProfileViewModel correctly initiates farm location update`() = runTest {
        val newFarmLocation = "New Test Farm Location"
        // This test primarily verifies that the repository method is called.
        // Success/failure is handled by UT_PROFILE_004 and UT_PROFILE_005.
        `when`(mockUserRepository.updateFarmLocation(newFarmLocation)).thenReturn(flowOf(Result.success(Unit)))

        viewModel.updateFarmLocation(newFarmLocation)
        // No need to advance scheduler if updateFarmLocation is not a suspend function directly launching a coroutine
        // that updates the state immediately. If it launches a coroutine internally for the update, then advance.
        // Assuming it does:
        testDispatcher.scheduler.advanceUntilIdle()


        verify(mockUserRepository).updateFarmLocation(newFarmLocation)
        // Initial state before result comes back might be loading
        assertTrue(viewModel.profileUpdateState.value.isLoading)
    }

    @Test
    fun `UT_PROFILE_004 verify ProfileViewModel handles farm location update success`() = runTest {
        val newFarmLocation = "Updated Farm Location"
        `when`(mockUserRepository.updateFarmLocation(newFarmLocation)).thenReturn(flowOf(Result.success(Unit)))
        // Assume profile is loaded first or mock the current profile state if update relies on it
        `when`(mockUserRepository.getUserProfile()).thenReturn(flowOf(Result.success(testUserProfile.copy(farmLocation = newFarmLocation))))


        viewModel.updateFarmLocation(newFarmLocation)
        testDispatcher.scheduler.advanceUntilIdle()

        val updateState = viewModel.profileUpdateState.value
        assertFalse(updateState.isLoading)
        assertNull(updateState.error)
        assertTrue(updateState.isSuccess) // Check for a success flag

        // Optionally, verify that the profileLoadState is refreshed or updated
        // This depends on the ViewModel's logic (e.g., does it reload profile after update?)
        // For this test, we'll assume it reloads or the success state implies the data is now current.
        // If it reloads:
        testDispatcher.scheduler.advanceUntilIdle() // for the potential reload
        val loadState = viewModel.profileLoadState.value
        if (loadState is ProfileLoadState.Success) {
            assertEquals(newFarmLocation, loadState.userProfile.farmLocation)
        }
    }

    @Test
    fun `UT_PROFILE_005 verify ProfileViewModel handles farm location update failure`() = runTest {
        val newFarmLocation = "Failed Update Location"
        val errorMessage = "Failed to update location"
        `when`(mockUserRepository.updateFarmLocation(newFarmLocation)).thenReturn(flowOf(Result.failure(Exception(errorMessage))))

        viewModel.updateFarmLocation(newFarmLocation)
        testDispatcher.scheduler.advanceUntilIdle()

        val updateState = viewModel.profileUpdateState.value
        assertFalse(updateState.isLoading)
        assertEquals(errorMessage, updateState.error)
        assertFalse(updateState.isSuccess)
    }

    @Test
    fun `UT_PROFILE_006 test input validation for farm location (client-side)`() {
        // Assuming ProfileViewModel has a method like `isValidFarmLocation(location: String): Boolean`
        // Or that updateFarmLocation performs validation before calling repository

        // Example: Test empty location
        val isValidEmpty = viewModel.isValidFarmLocation("") // Hypothetical validation method
        assertFalse(isValidEmpty)

        // Example: Test location too long (if there's a max length)
        val longLocation = "a".repeat(256) // Assuming max 255
        val isValidLong = viewModel.isValidFarmLocation(longLocation)
        assertFalse(isValidLong)

        // Example: Test valid location
        val validLocation = "My Valid Farm"
        val isValidNormal = viewModel.isValidFarmLocation(validLocation)
        assertTrue(isValidNormal)

        // If validation is part of updateFarmLocation:
        viewModel.updateFarmLocation("") // Empty location
        testDispatcher.scheduler.advanceUntilIdle()
        var updateState = viewModel.profileUpdateState.value
        assertNotNull(updateState.error) // Expect an error due to validation
        assertTrue(updateState.error!!.contains("valid", ignoreCase = true) || updateState.error!!.contains("empty", ignoreCase = true) ) // Or specific validation message

        viewModel.updateFarmLocation("a".repeat(300)) // Too long
        testDispatcher.scheduler.advanceUntilIdle()
        updateState = viewModel.profileUpdateState.value
        assertNotNull(updateState.error)
        assertTrue(updateState.error!!.contains("long", ignoreCase = true) || updateState.error!!.contains("length", ignoreCase = true) )
    }
}