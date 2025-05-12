package com.agriconnect.data.repository

import com.agriconnect.data.models.UserProfile
import io.supabase.gotrue.GoTrue
import io.supabase.postgrest.Postgrest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify

@ExperimentalCoroutinesApi
class UserRepositoryTest {

    @Mock
    private lateinit var mockSupabaseClientAuth: GoTrue

    @Mock
    private lateinit var mockSupabaseClientDb: Postgrest

    private lateinit var userRepository: UserRepository

    private val testUserId = "test-user-id"
    private val testUserProfile = UserProfile(testUserId, "Test User", "test@example.com", "Test Farm")

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)

        // Mock GoTrue user response
        val mockUser = mock<io.supabase.gotrue.types.User>()
        `when`(mockUser.id).thenReturn(testUserId)
        `when`(mockUser.email).thenReturn("test@example.com")
        // Assuming user_metadata might contain name, or it's fetched differently.
        // For simplicity, let's assume name is part of UserProfile fetched from 'profiles' table.

        `when`(mockSupabaseClientAuth.currentUserOrNull()).thenReturn(mockUser)

        userRepository = UserRepository(mockSupabaseClientAuth, mockSupabaseClientDb)
    }

    @Test
    fun `UT_REPO_001 Test UserRepository function for fetching profile from Supabase (mocked client)`() = runTest {
        // Mock Postgrest response for fetching profile
        val mockPostgrestBuilder = mock<Postgrest.SelectBuilder>()
        val mockPostgrestResponse = mock<Postgrest.Response>() // Simplified, actual response structure is complex

        `when`(mockSupabaseClientDb.from("profiles")).thenReturn(mockPostgrestBuilder)
        `when`(mockPostgrestBuilder.select(any())).thenReturn(mockPostgrestBuilder) // columns not specified for simplicity
        `when`(mockPostgrestBuilder.eq("id", testUserId)).thenReturn(mockPostgrestBuilder)
        `when`(mockPostgrestBuilder.single()).thenReturn(mockPostgrestBuilder) // Assuming single() is used

        // This is a simplification. Actual deserialization is complex.
        // We'll assume execute() returns a structure that can be mapped to UserProfile.
        // For a real test, you'd mock the exact Postgrest response and how it's deserialized.
        // Here, we'll assume a successful fetch returns a map that the repository can convert.
        val profileDataMap = mapOf(
            "id" to testUserId,
            "username" to "Test User",
            "email" to "test@example.com", // Email might come from auth or be stored here too
            "farm_location" to "Test Farm",
            // Supabase often includes "created_at", "updated_at"
            "created_at" to "2023-01-01T00:00:00Z"
        )
        // `when`(mockPostgrestBuilder.execute()).thenReturn(mockPostgrestResponse) // More realistic
        // `when`(mockPostgrestResponse.data).thenReturn("[${Klaxon().toJsonString(profileDataMap)}]") // Example with Klaxon

        // Simplified: Assume repository has a way to convert a successful Postgrest result
        // For this test, let's assume the repository's internal logic correctly parses a successful Postgrest response.
        // We'll directly mock the successful outcome of that parsing for simplicity of this example.
        // A more robust test would involve mocking Postgrest's execute() and its response structure.
        // For now, let's assume the repository's `getUserProfile` directly uses a Postgrest call that,
        // upon success, can be mapped. We'll simulate this success.

        // To properly test this, we need to know how `Postgrest.SelectBuilder.executeAndGetSingle<UserProfile>()` (or similar) behaves.
        // Let's assume it throws an exception on failure and returns UserProfile on success.
        // This part is tricky to mock without knowing the exact Supabase Kotlin client library's extension functions.
        // For the purpose of this example, we'll assume a simplified successful path.
        // A real implementation would need to mock the Postgrest execute() and its data.

        // Let's refine the mocking based on a hypothetical direct call within the repository:
        // `supabaseDb.from("profiles").select().eq("id", userId).executeAndGetSingle<UserProfile>()`
        // We need to mock this chain.
        `when`(mockPostgrestBuilder.executeAndGetSingle<UserProfile>()).thenReturn(testUserProfile)


        val result = userRepository.getUserProfile().first()

        assertTrue(result.isSuccess)
        assertEquals(testUserProfile, result.getOrNull())
        verify(mockSupabaseClientDb).from("profiles")
    }

    @Test
    fun `UT_REPO_002 Test UserRepository function for updating profile in Supabase (mocked client)`() = runTest {
        val newFarmLocation = "New Test Farm"

        // Mock Postgrest response for updating profile
        val mockPostgrestBuilderUpdate = mock<Postgrest.UpdateBuilder>()
        // val mockPostgrestResponseUpdate = mock<Postgrest.Response>() // Simplified

        `when`(mockSupabaseClientDb.from("profiles")).thenReturn(mockPostgrestBuilderUpdate) // Assuming from returns a general builder
        `when`(mockPostgrestBuilderUpdate.update(mapOf("farm_location" to newFarmLocation), any())).thenReturn(mockPostgrestBuilderUpdate)
        `when`(mockPostgrestBuilderUpdate.eq("id", testUserId)).thenReturn(mockPostgrestBuilderUpdate)

        // Assume execute() on update returns a response that indicates success/failure
        // For simplicity, we'll assume a successful update doesn't throw an exception.
        // `when`(mockPostgrestBuilderUpdate.execute()).thenReturn(mockPostgrestResponseUpdate)
        // `when`(mockPostgrestResponseUpdate.error).thenReturn(null) // Indicate success

        // A more direct way if the client library supports it:
        `when`(mockPostgrestBuilderUpdate.execute()).thenReturn(Unit) // Assuming execute on update returns Unit or similar on success


        val result = userRepository.updateFarmLocation(newFarmLocation).first()

        assertTrue(result.isSuccess)
        verify(mockSupabaseClientDb).from("profiles")
        // verify(mockPostgrestBuilderUpdate).update(mapOf("farm_location" to newFarmLocation), any()) // This verify might be too specific depending on how `from` is mocked
        verify(mockPostgrestBuilderUpdate).eq("id", testUserId)
        verify(mockPostgrestBuilderUpdate).execute()
    }

    @Test
    fun `UT_REPO_001 Test UserRepository getUserProfile handles error`() = runTest {
        val exception = RuntimeException("Supabase DB error")
        `when`(mockSupabaseClientDb.from("profiles")).thenReturn(mock()) // Basic mock for the chain start
        `when`(mockSupabaseClientDb.from("profiles").select(any())).thenReturn(mock())
        `when`(mockSupabaseClientDb.from("profiles").select(any()).eq("id", testUserId)).thenReturn(mock())
        `when`(mockSupabaseClientDb.from("profiles").select(any()).eq("id", testUserId).single()).thenReturn(mock())
        `when`(mockSupabaseClientDb.from("profiles").select(any()).eq("id", testUserId).single().executeAndGetSingle<UserProfile>())
            .thenThrow(exception)

        val result = userRepository.getUserProfile().first()

        assertTrue(result.isFailure)
        assertEquals(exception, result.exceptionOrNull())
    }

    @Test
    fun `UT_REPO_002 Test UserRepository updateFarmLocation handles error`() = runTest {
        val newFarmLocation = "New Location"
        val exception = RuntimeException("Supabase DB error on update")
        `when`(mockSupabaseClientDb.from("profiles")).thenReturn(mock())
        `when`(mockSupabaseClientDb.from("profiles").update(mapOf("farm_location" to newFarmLocation), any())).thenReturn(mock())
        `when`(mockSupabaseClientDb.from("profiles").update(mapOf("farm_location" to newFarmLocation), any()).eq("id", testUserId)).thenReturn(mock())
        `when`(mockSupabaseClientDb.from("profiles").update(mapOf("farm_location" to newFarmLocation), any()).eq("id", testUserId).execute())
            .thenThrow(exception)

        val result = userRepository.updateFarmLocation(newFarmLocation).first()

        assertTrue(result.isFailure)
        assertEquals(exception, result.exceptionOrNull())
    }
}