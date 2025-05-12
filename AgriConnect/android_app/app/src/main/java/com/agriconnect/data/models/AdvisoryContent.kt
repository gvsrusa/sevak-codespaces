package com.agriconnect.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class AdvisoryCategory {
    CROP_MANAGEMENT,
    POST_HARVEST,
    PEST_DISEASE,
    CLIMATE_SMART
}

@Entity(tableName = "advisory_content")
data class AdvisoryContent(
    @PrimaryKey val id: String, // Unique ID for the advisory article
    val title: String,
    val summary: String,
    val fullContent: String, // Can be HTML or Markdown
    val category: AdvisoryCategory,
    val cropType: String?, // e.g., "Wheat", "Rice", "Tomato", or null if general
    val region: String?, // e.g., "Punjab", "Maharashtra", or null if general
    val imageUrl: String?, // Link to an image in Supabase Storage
    val videoUrl: String?, // Link to a video
    val datePublished: Long,
    val lastUpdated: Long,
    val author: String? // Source or author of the advisory
)