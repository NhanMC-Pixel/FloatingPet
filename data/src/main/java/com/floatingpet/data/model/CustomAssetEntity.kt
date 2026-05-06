package com.floatingpet.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "custom_assets")
data class CustomAssetEntity(
    @PrimaryKey val stateName: String,   // IDLE, WALK, SLEEP, etc.
    val assetType: String,               // IMAGE, GIF, VIDEO
    val filePath: String,
    val trimStartMs: Long = 0,
    val trimEndMs: Long = 0,
    val speedMultiplier: Float = 1.0f
)