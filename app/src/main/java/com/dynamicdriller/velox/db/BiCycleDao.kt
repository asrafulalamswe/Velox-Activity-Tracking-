package com.dynamicdriller.velox.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface BiCycleDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertBiCycle(activity: Activity)

    @Delete
    fun deleteBiCycle(activity: Activity)

    @Query("SELECT * FROM cycling_table ORDER BY timestamp DESC")
    fun getAllBicycleActivitySortedByDate() : LiveData<List<Activity>>

    @Query("SELECT * FROM cycling_table ORDER BY timeInMillis DESC")
    fun getAllBicycleActivitySortedByTimeInMillis() : LiveData<List<Activity>>

    @Query("SELECT * FROM cycling_table ORDER BY caloriesBurn DESC")
    fun getAllBicycleActivitySortedByCalories() : LiveData<List<Activity>>

    @Query("SELECT * FROM cycling_table ORDER BY avgSpeedInKm DESC")
    fun getAllBicycleActivitySortedByAvgSpeedInKmh() : LiveData<List<Activity>>

    @Query("SELECT * FROM cycling_table ORDER BY distanceInMeters DESC")
    fun getAllBicycleActivitySortedByDistance() : LiveData<List<Activity>>

    @Query("SELECT SUM(timeInMillis) FROM cycling_table")
    fun getTotalTimeInMillis() : LiveData<Long>

    @Query("SELECT SUM(caloriesBurn) FROM cycling_table")
    fun getTotalCaloriesBurned() : LiveData<Int>

    @Query("SELECT SUM(distanceInMeters) FROM cycling_table")
    fun getTotalDistance() : LiveData<Int>

    @Query("SELECT AVG(avgSpeedInKm) FROM cycling_table")
    fun getTotalAvgSpeed() : LiveData<Float>
}