package com.floatingpet.data.dao

import androidx.room.*
import com.floatingpet.data.model.CustomAssetEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CustomAssetDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(asset: CustomAssetEntity)

    @Delete
    suspend fun delete(asset: CustomAssetEntity)

    @Query("SELECT * FROM custom_assets WHERE stateName = :state LIMIT 1")
    suspend fun getForState(state: String): CustomAssetEntity?

    @Query("SELECT * FROM custom_assets")
    fun getAll(): Flow<List<CustomAssetEntity>>
}