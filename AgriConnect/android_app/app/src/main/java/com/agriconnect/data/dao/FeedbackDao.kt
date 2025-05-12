package com.agriconnect.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.agriconnect.data.models.UserFeedback
import kotlinx.coroutines.flow.Flow

@Dao
interface FeedbackDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFeedback(feedback: UserFeedback)

    // Maybe not necessary to query feedback locally, but good to have for caching/offline
    @Query("SELECT * FROM user_feedback ORDER BY dateSubmitted DESC")
    fun getAllFeedback(): Flow<List<UserFeedback>>

    @Query("SELECT * FROM user_feedback WHERE userId = :userId ORDER BY dateSubmitted DESC")
    fun getFeedbackByUser(userId: String): Flow<List<UserFeedback>>

    @Query("DELETE FROM user_feedback WHERE id = :feedbackId")
    suspend fun deleteFeedbackById(feedbackId: String)

    @Query("DELETE FROM user_feedback")
    suspend fun deleteAllFeedback()
}