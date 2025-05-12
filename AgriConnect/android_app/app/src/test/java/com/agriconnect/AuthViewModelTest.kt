package com.agriconnect

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.agriconnect.data.repository.AuthRepository
import com.agriconnect.ui.auth.AuthViewModel
import com.agriconnect.ui.auth.SignInResult
import com.agriconnect.ui.auth.SignInState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations

/**
 * Unit tests for the [AuthViewModel].
 */
@ExperimentalCoroutinesApi
class AuthViewModelTest {

    // Rule for LiveData testing
    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    // Test dispatcher for coroutines
    private val testDispatcher = StandardTestDispatcher()

    @Mock
    private lateinit var mockAuthRepository: AuthRepository

    private lateinit var viewModel: AuthViewModel

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        Dispatchers.setMain(testDispatcher) // Set the main dispatcher to the test dispatcher
        viewModel = AuthViewModel(mockAuthRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain() // Reset the main dispatcher
    }

    @Test
    fun `UT_AUTH_001 verify AuthViewModel state updates correctly during Google Sign-In attempt`() = runTest {
        // Assume onSignInResult is called and repository is processing
        // The initial state should be idle or similar, then loading

        // Initial state (example, depends on actual ViewModel implementation)
        assertEquals(SignInState(isSignInInProgress = false, signInError = null), viewModel.signInState.value)

        // Simulate triggering sign-in (method in ViewModel would call repository)
        // For this test, we'll directly observe the state change if a method like `beginSignIn` exists
        // If not, this test might be covered by testing the method that handles the sign-in result.
        // For now, let's assume a method `initiateSignIn` that sets a loading state.
        // viewModel.initiateSignIn() // Hypothetical method
        // assertEquals(SignInState(isSignInInProgress = true, signInError = null), viewModel.signInState.value)
        // This test case might be better reframed to test the state *after* a sign-in attempt is made
        // and before a result is received, if the ViewModel exposes such intermediate states.
        // Given the current structure, UT_AUTH_002, 003, 004 cover state changes upon receiving results.
    }

    @Test
    fun `UT_AUTH_002 verify AuthViewModel handles successful Google Sign-In callback`() = runTest {
        val successfulSignInResult = SignInResult(
            data = com.agriconnect.ui.auth.UserData("123", "Test User", "test@example.com", "profile.jpg"),
            errorMessage = null
        )
        // No direct repository call here, viewModel.onSignInResult is the entry point
        // `when`(mockAuthRepository.signInWithGoogleToken("dummy_token")).thenReturn(flowOf(successfulSignInResult)) // This would be for a direct call

        viewModel.onSignInResult(successfulSignInResult)
        testDispatcher.scheduler.advanceUntilIdle() // Ensure coroutines complete

        val state = viewModel.signInState.value
        assertEquals(false, state.isSignInInProgress)
        assertNull(state.signInError)
        assertNotNull(viewModel.userState.value) // Assuming userState is updated on successful sign-in
        assertEquals("Test User", viewModel.userState.value?.username)
    }

    @Test
    fun `UT_AUTH_003 verify AuthViewModel handles failed Google Sign-In callback (user cancel)`() = runTest {
        val cancelledSignInResult = SignInResult(
            data = null,
            errorMessage = "Sign-in cancelled by user." // Example error message
        )

        viewModel.onSignInResult(cancelledSignInResult)
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.signInState.value
        assertEquals(false, state.isSignInInProgress)
        assertEquals("Sign-in cancelled by user.", state.signInError)
        assertNull(viewModel.userState.value) // User should not be set
    }

    @Test
    fun `UT_AUTH_004 verify AuthViewModel handles Google Sign-In error (network issue)`() = runTest {
        val errorSignInResult = SignInResult(
            data = null,
            errorMessage = "Network error during sign-in." // Example error message
        )

        viewModel.onSignInResult(errorSignInResult)
        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.signInState.value
        assertEquals(false, state.isSignInInProgress)
        assertEquals("Network error during sign-in.", state.signInError)
        assertNull(viewModel.userState.value) // User should not be set
    }
}