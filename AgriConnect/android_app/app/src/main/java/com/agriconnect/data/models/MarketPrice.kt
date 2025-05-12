package com.agriconnect.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "market_prices")
data class MarketPrice(
    @PrimaryKey val id: String, // Composite key of commodityId + marketId + date might be better
    val commodityId: String, // Reference to a commodity (e.g., "wheat", "potato")
    val commodityName: String,
    val marketId: String, // Reference to a market location
    val marketName: String,
    val price: Double,
    val unit: String, // e.g., "per quintal", "per kg"
    val date: Long, // Timestamp of the price information
    val source: String? // e.g., "Agmarknet", "Local Survey"
)