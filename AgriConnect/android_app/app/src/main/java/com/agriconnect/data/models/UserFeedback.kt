package com.agriconnect.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_feedback")
data class UserFeedback(
    @PrimaryKey val id: String, // Unique ID for the feedback entry
    val userId: String?, // Optional: if the user is logged in
    val feedbackText: String,
    val rating: Int?, // Optional: e.g., 1-5 stars
    val category: String?, // Optional: e.g., "Bug Report", "Feature Request", "General"
    val appVersion: String?,
    val deviceModel: String?,
    val dateSubmitted: Long = System.currentTimeMillis()
)