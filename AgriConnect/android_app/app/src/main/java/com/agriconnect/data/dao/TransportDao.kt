package com.agriconnect.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.agriconnect.data.models.TransportRequest
import com.agriconnect.data.models.TransportRequestStatus
import kotlinx.coroutines.flow.Flow

@Dao
interface TransportDao {

    @Query("SELECT * FROM transport_requests ORDER BY datePosted DESC")
    fun getAllTransportRequests(): Flow<List<TransportRequest>>

    @Query("SELECT * FROM transport_requests WHERE userId = :userId ORDER BY datePosted DESC")
    fun getRequestsByUser(userId: String): Flow<List<TransportRequest>>

    @Query("SELECT * FROM transport_requests WHERE status = :status ORDER BY datePosted DESC")
    fun getRequestsByStatus(status: TransportRequestStatus): Flow<List<TransportRequest>>

    @Query("SELECT * FROM transport_requests WHERE id = :requestId")
    fun getRequestById(requestId: String): Flow<TransportRequest?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRequest(request: TransportRequest)

    @Update
    suspend fun updateRequest(request: TransportRequest)

    @Query("DELETE FROM transport_requests WHERE id = :requestId")
    suspend fun deleteRequestById(requestId: String)

    @Query("DELETE FROM transport_requests")
    suspend fun deleteAllRequests()
}