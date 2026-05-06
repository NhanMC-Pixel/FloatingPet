package com.floatingpet.data.repository

import com.floatingpet.data.dao.CustomAssetDao
import com.floatingpet.data.model.CustomAssetEntity
import com.floatingpet.domain.AssetPack
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AssetRepository @Inject constructor(
    private val dao: CustomAssetDao
) {
    fun getAllCustomAssets(): Flow<List<AssetPack>> {
        return dao.getAll().map { list ->
            list.map { entity ->
                AssetPack(
                    stateName = entity.stateName,
                    assetType = entity.assetType,
                    filePath = entity.filePath,
                    trimStartMs = entity.trimStartMs,
                    trimEndMs = entity.trimEndMs,
                    speedMultiplier = entity.speedMultiplier
                )
            }
        }
    }

    suspend fun getAssetForState(stateName: String): AssetPack? {
        return dao.getForState(stateName)?.let {
            AssetPack(
                it.stateName,
                it.assetType,
                it.filePath,
                it.trimStartMs,
                it.trimEndMs,
                it.speedMultiplier
            )
        }
    }

    suspend fun saveAsset(assetPack: AssetPack) {
        dao.insertOrUpdate(
            CustomAssetEntity(
                stateName = assetPack.stateName,
                assetType = assetPack.assetType,
                filePath = assetPack.filePath,
                trimStartMs = assetPack.trimStartMs,
                trimEndMs = assetPack.trimEndMs,
                speedMultiplier = assetPack.speedMultiplier
            )
        )
    }

    suspend fun deleteAsset(stateName: String) {
        val asset = dao.getForState(stateName)
        asset?.let { dao.delete(it) }
    }
}