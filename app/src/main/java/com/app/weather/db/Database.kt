package com.app.weather.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.app.weather.BuildConfig
import com.app.weather.model.weather.WeatherModel

@Database(
    entities = [WeatherModel::class],
    version = BuildConfig.DB_VERSION,
    exportSchema = false
)
abstract class Database : RoomDatabase() {
    abstract fun weatherDao(): WeatherDao
}