package com.dynamicdriller.velox.repositories

import com.dynamicdriller.velox.db.BiCycle
import com.dynamicdriller.velox.db.BiCycleDao
import javax.inject.Inject

class MainRepository @Inject constructor(
    private val biCycleDao: BiCycleDao
) {
    suspend fun insertBiCycle(biCycle: BiCycle) = biCycleDao.insertBiCycle(biCycle)
    suspend fun deleteBiCycle(biCycle: BiCycle) = biCycleDao.deleteBiCycle(biCycle)

    fun getAllBicycleActivitySortedByDate() = biCycleDao.getAllBicycleActivitySortedByDate()
    fun getAllBicycleActivitySortedByTimeInMillis() = biCycleDao.getAllBicycleActivitySortedByTimeInMillis()
    fun getAllBicycleActivitySortedByCalories() = biCycleDao.getAllBicycleActivitySortedByCalories()
    fun getAllBicycleActivitySortedByAvgSpeedInKmh() = biCycleDao.getAllBicycleActivitySortedByAvgSpeedInKmh()
    fun getAllBicycleActivitySortedByDistance() = biCycleDao.getAllBicycleActivitySortedByDistance()

    fun getTotalTimeInMillis() = biCycleDao.getTotalTimeInMillis()
    fun getTotalCaloriesBurned() = biCycleDao.getTotalCaloriesBurned()
    fun getTotalDistance() = biCycleDao.getTotalDistance()
    fun getTotalAvgSpeed() = biCycleDao.getTotalAvgSpeed()


}