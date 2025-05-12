package com.agriconnect.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "produce_listings")
data class ProduceListing(
    @PrimaryKey val id: String, // Could be UUID generated locally or from backend
    val userId: String, // ID of the farmer who listed this
    val cropName: String,
    val quantity: Double, // e.g., 50.0
    val unit: String, // e.g., "kg", "quintal", "bags"
    val pricePerUnit: Double,
    val description: String?,
    val location: String?, // Could be more structured (lat/long) later
    val imageUrl: String?, // Link to image in Supabase Storage
    val datePosted: Long = System.currentTimeMillis(),
    val lastUpdated: Long = System.currentTimeMillis(),
    val isActive: Boolean = true
)