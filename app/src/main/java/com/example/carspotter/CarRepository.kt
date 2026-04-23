package com.example.carspotter

import kotlinx.coroutines.flow.Flow

class CarRepository(private val carDao: CarDao) {

    fun getAllCars(): Flow<List<CarEntity>> = carDao.getAllCars()

    suspend fun insertCar(car: CarEntity) {
        carDao.insertCar(car)
    }

    suspend fun deleteCar(car: CarEntity) {
        carDao.deleteCar(car)
    }
}