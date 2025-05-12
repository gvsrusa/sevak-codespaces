package com.agriconnect.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class TransportRequestStatus {
    PENDING,
    ACCEPTED,
    IN_TRANSIT,
    COMPLETED,
    CANCELLED
}

@Entity(tableName = "transport_requests")
data class TransportRequest(
    @PrimaryKey val id: String, // Unique ID for the request
    val userId: String, // ID of the farmer requesting transport
    val pickupLocation: String, // Could be more structured (lat/long, address)
    val destination: String, // Could be more structured
    val loadDetails: String, // e.g., "50 bags of potatoes, approx 2.5 tons"
    val preferredDate: Long, // Timestamp for preferred pickup
    val contactNumber: String,
    val status: TransportRequestStatus = TransportRequestStatus.PENDING,
    val datePosted: Long = System.currentTimeMillis(),
    val acceptedByTransporterId: String? = null, // ID of the transporter who accepted
    val notes: String? = null
)