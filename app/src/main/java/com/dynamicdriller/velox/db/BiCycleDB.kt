package com.dynamicdriller.velox.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(entities = [BiCycle::class], version = 2)
@TypeConverters(Converters::class)
abstract class BiCycleDB : RoomDatabase(){
    abstract fun getBiCycleDao():BiCycleDao
}