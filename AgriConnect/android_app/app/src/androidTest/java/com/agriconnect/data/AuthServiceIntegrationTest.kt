package com.agriconnect.data

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.agriconnect.data.repository.AuthRepository
import io.supabase.gotrue.GoTrue
import io.supabase.gotrue.types.Session
import io.supabase.gotrue.types.User
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking // Using runBlocking for simplicity in AndroidTest if not UI related
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.eq
import org.mockito.kotlin.mock

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
class AuthServiceIntegrationTest {

    @Mock
    private lateinit var mockSupabaseAuth: GoTrue

    private lateinit var authRepository: AuthRepository

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        authRepository = AuthRepository(mockSupabaseAuth)
    }

    @Test
    fun `IT_AUTH_003 Verify Supabase Auth correctly verifies a valid Google ID token`() = runBlocking {
        val googleIdToken = "valid-google-id-token"
        val mockSession = mock<Session>()
        val mockUser = mock<User>()

        `when`(mockUser.id).thenReturn("supabase-user-id-it")
        `when`(mockUser.email).thenReturn("user-it@example.com")
        val userMetadata = mapOf(
            "full_name" to "Test User IT",
            "avatar_url" to "http://example.com/avatar-it.jpg"
        )
        `when`(mockUser.userMetadata).thenReturn(userMetadata)
        `when`(mockSession.user).thenReturn(mockUser)

        `when`(mockSupabaseAuth.signInWithIdToken(provider = eq(io.supabase.gotrue.providers.Google), token = eq(googleIdToken)))
            .thenReturn(mockSession)

        val result = authRepository.signInWithGoogleToken(googleIdToken).first()

        assertTrue(result.isSuccess)
        val signInResult = result.getOrNull()
        assertNotNull(signInResult?.data)
        assertEquals("supabase-user-id-it", signInResult?.data?.userId)
        assertEquals("Test User IT", signInResult?.data?.username)
        assertEquals("user-it@example.com", signInResult?.data?.email)
        assertEquals("http://example.com/avatar-it.jpg", signInResult?.data?.profilePictureUrl)
        assertNull(signInResult?.errorMessage)
    }

    @Test
    fun `IT_AUTH_004 Verify Supabase Auth rejects an invalid or expired Google ID token`() = runBlocking {
        val invalidGoogleIdToken = "invalid-or-expired-google-id-token"
        val errorMessage = "Invalid ID token"
        // Simulate Supabase client throwing an exception for an invalid token
        val supabaseException = io.supabase.gotrue.errors.GoTrueError(errorMessage) // Or a more specific exception type
        `when`(mockSupabaseAuth.signInWithIdToken(provider = eq(io.supabase.gotrue.providers.Google), token = eq(invalidGoogleIdToken)))
            .thenThrow(supabaseException)

        val result = authRepository.signInWithGoogleToken(invalidGoogleIdToken).first()

        assertTrue(result.isFailure)
        val exception = result.exceptionOrNull()
        assertNotNull(exception)
        assertTrue(exception is io.supabase.gotrue.errors.GoTrueError)
        assertEquals(errorMessage, exception.message)
    }
}