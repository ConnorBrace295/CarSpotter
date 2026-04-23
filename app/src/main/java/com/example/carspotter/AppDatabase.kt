package com.example.carspotter

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [CarEntity::class],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun carDao(): CarDao
}