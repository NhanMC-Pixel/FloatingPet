package com.floatingpet.data.di

import android.content.Context
import androidx.room.Room
import com.floatingpet.data.db.AppDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "floating_pet_db"
        ).build()
    }

    @Provides
    fun provideCustomAssetDao(db: AppDatabase) = db.customAssetDao()
}