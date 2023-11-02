package com.napa.foodstorechallengech3.data.local.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.napa.foodstorechallengech3.data.local.database.dao.CartDao
import com.napa.foodstorechallengech3.data.local.database.entity.CartEntity

@Database(
    entities = [CartEntity::class],
    version = 3,
    exportSchema = true
)

abstract class AppDatabase : RoomDatabase() {
    abstract fun cartDao(): CartDao

    companion object {
        private const val DB_NAME = "FoodStoreChallengeCh3.db"
        fun getInstance(context: Context): AppDatabase {
            return Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                DB_NAME
            ).fallbackToDestructiveMigration().build()
        }
    }
}
