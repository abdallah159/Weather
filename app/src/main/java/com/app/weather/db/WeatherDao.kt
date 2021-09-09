package com.app.weather.db

import androidx.lifecycle.LiveData
import androidx.room.*
import com.app.weather.model.weather.WeatherModel

@Dao
interface WeatherDao {
    @Query("SELECT * FROM weather_table")
    fun getAll(): LiveData<List<WeatherModel>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun savePlace(weather: WeatherModel)

    @Query("Delete FROM weather_table where latitude =:lat and longitude=:lon")
    fun deletePlace(lat : Double, lon :Double)

}