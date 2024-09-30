package com.dynamicdriller.velox.di

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import androidx.room.Room
import com.dynamicdriller.velox.db.BiCycleDB
import com.dynamicdriller.velox.db.BiCycleDao
import com.dynamicdriller.velox.other.Constants.BICYCLE_DB_NAME
import com.dynamicdriller.velox.other.Constants.KEY_FIRST_TIME_TOGGLE
import com.dynamicdriller.velox.other.Constants.KEY_NAME
import com.dynamicdriller.velox.other.Constants.KEY_WEIGHT
import com.dynamicdriller.velox.other.Constants.SHARED_PREFERENCES_NAME
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun provideBiCycleDatabase(
        @ApplicationContext app:Context
    ) = Room.databaseBuilder(
        app,
        BiCycleDB::class.java,
        BICYCLE_DB_NAME
    ).fallbackToDestructiveMigration()
        .build()

    @Singleton
    @Provides
    fun provideBiCycleDao(db: BiCycleDB) = db.getBiCycleDao()

    @Singleton
    @Provides
    fun provideSharedPreferences(@ApplicationContext app:Context) =
        app.getSharedPreferences(SHARED_PREFERENCES_NAME, MODE_PRIVATE)

    @Singleton
    @Provides
    fun provideName(sharedPref:SharedPreferences) = sharedPref.getString(KEY_NAME,"") ?: ""

    @Singleton
    @Provides
    fun provideWeight(sharedPref:SharedPreferences) = sharedPref.getFloat(KEY_WEIGHT,80f)

    @Singleton
    @Provides
    fun provideFirstTimeToggle(sharedPref:SharedPreferences) =
        sharedPref.getBoolean(KEY_FIRST_TIME_TOGGLE,true)
}