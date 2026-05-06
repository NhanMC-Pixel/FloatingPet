package com.floatingpet.domain

data class AssetPack(
    val stateName: String,
    val assetType: String, // "IMAGE", "GIF", "VIDEO"
    val filePath: String,
    val trimStartMs: Long = 0,
    val trimEndMs: Long = 0,
    val speedMultiplier: Float = 1.0f
)