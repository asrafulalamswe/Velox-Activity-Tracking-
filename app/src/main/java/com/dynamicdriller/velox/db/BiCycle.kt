package com.dynamicdriller.velox.db

import android.graphics.Bitmap
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "cycling_table")
data class BiCycle(
    @PrimaryKey(autoGenerate = true)
    var id:Int? = null,
    var title:String = "",
    var image: Bitmap? = null,
    var timestamp: Long = 0L,
    var avgSpeedInKm: Float = 0f,
    var distanceInMeters:Int = 0,
    var timeInMillis:Long = 0L,
    var caloriesBurn: Int = 0,
) : Serializable