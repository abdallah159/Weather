package com.app.weather.model.weather

import androidx.room.*
import com.google.gson.annotations.SerializedName
import java.io.Serializable

@Entity(tableName = "weather_table")
data class WeatherModel(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "placeId")
    var placeId: Int = 0,

    @ColumnInfo(name = "latitude")
    @SerializedName("lat")var latitude: Double = 0.0,

    @ColumnInfo(name = "longitude")
    @SerializedName("lon")var longitude: Double = 0.0,

    @ColumnInfo(name = "place_name")
    var placeName: String = "",

    @Ignore
    @SerializedName("daily") var daily: List<DailyWeather>? = null,
    @Ignore
    @SerializedName("current") var current: CurrentWeather? = null,
) : Serializable

data class DailyWeather(
    @SerializedName("dt") var date: Long = 0,
    @SerializedName("temp") var temp: Temperature? = null,
    @SerializedName("wind_speed") var windSpeed: Float = 0f,
    @SerializedName("humidity") var humidity: Float = 0f,
    @SerializedName("weather") var weather: List<WeatherStatus>? = null
) : Serializable

data class Temperature(
    @SerializedName("min") var min: Float = 0f,
    @SerializedName("max") var max: Float = 0f,
    @SerializedName("day") var day: Float = 0f
) : Serializable

data class CurrentWeather(
    @SerializedName("temp") var currentTemp: Float = 0f,
    @SerializedName("wind_speed") var currentWindSpeed: Float = 0f,
    @SerializedName("humidity") var currentHumidity: Float = 0f,
    @SerializedName("weather") var currentWeather: List<WeatherStatus>? = null
) : Serializable


data class WeatherStatus(
    @SerializedName("main") var main: String = "",
    @SerializedName("description") var description: String = "",
    @SerializedName("icon") var icon: String = ""
) : Serializable