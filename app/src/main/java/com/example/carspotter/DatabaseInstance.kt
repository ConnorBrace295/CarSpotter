package com.example.carspotter

import android.content.Context
import androidx.room.Room

object DatabaseInstance {

    @Volatile
    private var INSTANCE: AppDatabase? = null

    fun getDatabase(context: Context): AppDatabase {
        return INSTANCE ?: synchronized(this) {
            val instance = Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                "car_database"
            ).build()

            INSTANCE = instance
            instance
        }
    }
}