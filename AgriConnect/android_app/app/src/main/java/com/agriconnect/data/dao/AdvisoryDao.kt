package com.agriconnect.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.agriconnect.data.models.AdvisoryCategory
import com.agriconnect.data.models.AdvisoryContent
import kotlinx.coroutines.flow.Flow

@Dao
interface AdvisoryDao {

    @Query("SELECT * FROM advisory_content ORDER BY datePublished DESC")
    fun getAllAdvisoryContent(): Flow<List<AdvisoryContent>>

    @Query("SELECT * FROM advisory_content WHERE id = :contentId")
    fun getAdvisoryContentById(contentId: String): Flow<AdvisoryContent?>

    @Query("SELECT * FROM advisory_content WHERE category = :category ORDER BY datePublished DESC")
    fun getAdvisoryContentByCategory(category: AdvisoryCategory): Flow<List<AdvisoryContent>>

    @Query("SELECT * FROM advisory_content WHERE cropType = :cropType ORDER BY datePublished DESC")
    fun getAdvisoryContentByCrop(cropType: String): Flow<List<AdvisoryContent>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAdvisoryContent(content: AdvisoryContent)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllAdvisoryContent(contents: List<AdvisoryContent>)

    @Query("DELETE FROM advisory_content WHERE id = :contentId")
    suspend fun deleteAdvisoryContentById(contentId: String)

    @Query("DELETE FROM advisory_content")
    suspend fun deleteAllAdvisoryContent()
}