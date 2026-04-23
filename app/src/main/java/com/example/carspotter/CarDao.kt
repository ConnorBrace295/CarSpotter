package com.example.carspotter

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface CarDao {

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertCar(car: CarEntity)

    @Update
    suspend fun updateCar(car: CarEntity)

    @Delete
    suspend fun deleteCar(car: CarEntity)

    @Query("SELECT * FROM cars")
    fun getAllCars(): Flow<List<CarEntity>>

    @Query("SELECT * FROM cars WHERE plate = :plate LIMIT 1")
    suspend fun getCarByPlate(plate: String): CarEntity?
}