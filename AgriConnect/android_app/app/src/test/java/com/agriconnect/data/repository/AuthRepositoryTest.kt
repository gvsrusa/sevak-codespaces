package com.agriconnect.data.repository

import com.agriconnect.ui.auth.SignInResult
import com.agriconnect.ui.auth.UserData
import io.supabase.gotrue.GoTrue
import io.supabase.gotrue.types.Session
import io.supabase.gotrue.types.User
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.any
import org.mockito.kotlin.mock

@ExperimentalCoroutinesApi
class AuthRepositoryTest {

    @Mock
    private lateinit var mockSupabaseAuth: GoTrue

    private lateinit var authRepository: AuthRepository

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        authRepository = AuthRepository(mockSupabaseAuth)
    }

    @Test
    fun `UT_REPO_003 Test AuthRepository function for signing in with Google token (mocked client)`() = runTest {
        val googleIdToken = "test-google-id-token"
        val mockSession = mock<Session>()
        val mockUser = mock<User>()

        `when`(mockUser.id).thenReturn("supabase-user-id")
        `when`(mockUser.email).thenReturn("user@example.com")
        // Supabase User object has `userMetadata` which is a Map<String, Any>.
        // Google Sign-In often provides 'name', 'full_name', 'picture', etc.
        // Let's assume 'full_name' and 'avatar_url' are standard keys from Google via Supabase.
        val userMetadata = mapOf(
            "full_name" to "Test User FullName",
            "avatar_url" to "http://example.com/avatar.jpg"
        )
        `when`(mockUser.userMetadata).thenReturn(userMetadata)
        `when`(mockSession.user).thenReturn(mockUser)

        // Mock the signInWithIdToken function
        `when`(mockSupabaseAuth.signInWithIdToken(provider = eq(io.supabase.gotrue.providers.Google), token = eq(googleIdToken)))
            .thenReturn(mockSession) // Assuming it returns Session directly on success

        val result = authRepository.signInWithGoogleToken(googleIdToken).first()

        assertTrue(result.isSuccess)
        val signInResult = result.getOrNull()
        assertNotNull(signInResult)
        assertNotNull(signInResult?.data)
        assertEquals("supabase-user-id", signInResult?.data?.userId)
        assertEquals("Test User FullName", signInResult?.data?.username) // Ensure mapping is correct
        assertEquals("user@example.com", signInResult?.data?.email)
        assertEquals("http://example.com/avatar.jpg", signInResult?.data?.profilePictureUrl)
        assertNull(signInResult?.errorMessage)
    }

    @Test
    fun `UT_REPO_003 Test AuthRepository signInWithGoogleToken handles error`() = runTest {
        val googleIdToken = "test-google-id-token-failure"
        val errorMessage = "Invalid Google token"
        val exception = RuntimeException(errorMessage)

        `when`(mockSupabaseAuth.signInWithIdToken(provider = eq(io.supabase.gotrue.providers.Google), token = eq(googleIdToken)))
            .thenThrow(exception)

        val result = authRepository.signInWithGoogleToken(googleIdToken).first()

        assertTrue(result.isFailure)
        val signInResult = result.exceptionOrNull() // This will be the exception itself
        assertNotNull(signInResult)
        assertTrue(signInResult is RuntimeException)
        assertEquals(errorMessage, signInResult.message)

        // If we want to check the SignInResult structure on failure:
        // We need to modify the repository to catch exceptions and return Result<SignInResult>
        // For now, the test reflects that the flow emits Result.failure(exception)
    }

    @Test
    fun `UT_REPO_003 Test AuthRepository signInWithGoogleToken handles null session`() = runTest {
        val googleIdToken = "test-google-id-token-null-session"

        `when`(mockSupabaseAuth.signInWithIdToken(provider = eq(io.supabase.gotrue.providers.Google), token = eq(googleIdToken)))
            .thenReturn(null) // Simulate null session return

        val result = authRepository.signInWithGoogleToken(googleIdToken).first()

        assertTrue(result.isFailure) // Or however the repository handles null session
                                     // Assuming it translates to a failure with a specific message
        val exception = result.exceptionOrNull()
        assertNotNull(exception)
        // Check for a specific type of exception or message if the repository creates one
        // For example, if it throws IllegalStateException("Sign in failed: Session is null")
        assertTrue(exception?.message?.contains("Sign in failed", ignoreCase = true) == true)
    }


    @Test
    fun `Test AuthRepository signOut success`() = runTest {
        `when`(mockSupabaseAuth.signOut()).thenReturn(Unit) // Assuming signOut returns Unit

        val result = authRepository.signOut().first()

        assertTrue(result.isSuccess)
        assertEquals(Unit, result.getOrNull())
    }

    @Test
    fun `Test AuthRepository signOut failure`() = runTest {
        val exception = RuntimeException("Sign out failed")
        `when`(mockSupabaseAuth.signOut()).thenThrow(exception)

        val result = authRepository.signOut().first()

        assertTrue(result.isFailure)
        assertEquals(exception, result.exceptionOrNull())
    }

    @Test
    fun `Test AuthRepository getCurrentUser`() {
        val mockUser = mock<User>()
        `when`(mockUser.id).thenReturn("current-user-id")
        `when`(mockUser.email).thenReturn("current@example.com")
        val userMetadata = mapOf("full_name" to "Current User", "avatar_url" to "current_avatar.jpg")
        `when`(mockUser.userMetadata).thenReturn(userMetadata)
        `when`(mockSupabaseAuth.currentUserOrNull()).thenReturn(mockUser)

        val userData = authRepository.getCurrentUser()

        assertNotNull(userData)
        assertEquals("current-user-id", userData?.userId)
        assertEquals("Current User", userData?.username)
        assertEquals("current@example.com", userData?.email)
        assertEquals("current_avatar.jpg", userData?.profilePictureUrl)
    }

    @Test
    fun `Test AuthRepository getCurrentUser when no user`() {
        `when`(mockSupabaseAuth.currentUserOrNull()).thenReturn(null)
        val userData = authRepository.getCurrentUser()
        assertNull(userData)
    }
}