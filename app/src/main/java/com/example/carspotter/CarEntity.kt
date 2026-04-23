package com.example.carspotter

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "cars",
    indices = [Index(value = ["plate"], unique = true)]
)
data class CarEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val plate: String,
    val model: String,
    val year: Int,
    val colour: String,
    val imageUrl: String
)