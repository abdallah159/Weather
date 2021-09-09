package com.app.weather.utils

import android.content.Context
import android.graphics.drawable.Drawable
import android.text.format.DateFormat
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import com.app.weather.R
import com.bumptech.glide.Glide
import java.util.*

fun ImageView?.loadImage(url: String?) {
    if(!url.isNullOrEmpty()) {
        this?.context?.let {
            Glide.with(it)
                .load(url)
                .into(this)
        }
    }
}

fun getWeatherIcon(desc : String, context : Context) : Drawable {
    when(desc){
        "clear sky" -> return ContextCompat.getDrawable(context, R.drawable.ic_clear_day)!!
        "few clouds" -> return ContextCompat.getDrawable(context, R.drawable.ic_few_clouds)!!
        "scattered clouds" -> return ContextCompat.getDrawable(context, R.drawable.ic_cloudy_weather)!!
        "broken clouds" -> return ContextCompat.getDrawable(context, R.drawable.ic_mostly_cloudy)!!
        "shower rain" -> return ContextCompat.getDrawable(context, R.drawable.ic_shower_rain)!!
        "thunderstorm" -> return ContextCompat.getDrawable(context, R.drawable.ic_storm_weather)!!
        "snow" -> return ContextCompat.getDrawable(context, R.drawable.ic_snow_weather)!!
        "mist" -> return ContextCompat.getDrawable(context, R.drawable.ic_broken_clouds)!!
    }
    return ContextCompat.getDrawable(context, R.drawable.ic_clear_day)!!
}

fun getDayFromTimestamp(date : Long)  :String{
    val calendar = Calendar.getInstance(Locale.ENGLISH)
    calendar.timeInMillis = date * 1000L
    val dateDay = DateFormat.format("EEEE",calendar).toString()
    return dateDay
}

fun <T> LiveData<T>.observeOnce(lifecycleOwner: LifecycleOwner, observer: Observer<T>) {
    observe(lifecycleOwner, object : Observer<T> {
        override fun onChanged(t: T?) {
            observer.onChanged(t)
            removeObserver(this)
        }
    })
}