package com.agriconnect.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.agriconnect.data.models.ProduceListing
import kotlinx.coroutines.flow.Flow

@Dao
interface ProduceDao {

    @Query("SELECT * FROM produce_listings WHERE isActive = 1 ORDER BY datePosted DESC")
    fun getAllActiveListings(): Flow<List<ProduceListing>>

    @Query("SELECT * FROM produce_listings WHERE id = :listingId")
    fun getListingById(listingId: String): Flow<ProduceListing?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertListing(listing: ProduceListing)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllListings(listings: List<ProduceListing>)

    @Update
    suspend fun updateListing(listing: ProduceListing)

    @Query("DELETE FROM produce_listings WHERE id = :listingId")
    suspend fun deleteListingById(listingId: String)

    @Query("DELETE FROM produce_listings")
    suspend fun deleteAllListings()
}