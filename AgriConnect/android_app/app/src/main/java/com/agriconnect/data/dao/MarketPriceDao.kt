package com.agriconnect.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.agriconnect.data.models.MarketPrice
import kotlinx.coroutines.flow.Flow

@Dao
interface MarketPriceDao {

    @Query("SELECT * FROM market_prices ORDER BY date DESC, commodityName ASC")
    fun getAllMarketPrices(): Flow<List<MarketPrice>>

    @Query("SELECT * FROM market_prices WHERE commodityId = :commodityId ORDER BY date DESC")
    fun getPricesForCommodity(commodityId: String): Flow<List<MarketPrice>>

    @Query("SELECT * FROM market_prices WHERE marketId = :marketId ORDER BY date DESC, commodityName ASC")
    fun getPricesForMarket(marketId: String): Flow<List<MarketPrice>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPrice(price: MarketPrice)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllPrices(prices: List<MarketPrice>)

    @Query("DELETE FROM market_prices")
    suspend fun deleteAllPrices()

    @Query("DELETE FROM market_prices WHERE date < :timestamp")
    suspend fun deleteOldPrices(timestamp: Long)
}