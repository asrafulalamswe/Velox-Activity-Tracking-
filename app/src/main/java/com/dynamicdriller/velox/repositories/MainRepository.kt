package com.dynamicdriller.velox.repositories

import com.dynamicdriller.velox.db.Activity
import com.dynamicdriller.velox.db.BiCycleDao
import javax.inject.Inject

class MainRepository @Inject constructor(
    private val biCycleDao: BiCycleDao
) {
    suspend fun insertBiCycle(activity: Activity) = biCycleDao.insertBiCycle(activity)
    suspend fun deleteBiCycle(activity: Activity) = biCycleDao.deleteBiCycle(activity)

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