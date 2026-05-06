package com.floatingpet.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.floatingpet.data.dao.CustomAssetDao
import com.floatingpet.data.model.CustomAssetEntity

@Database(entities = [CustomAssetEntity::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun customAssetDao(): CustomAssetDao
}