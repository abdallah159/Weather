package com.app.weather.ui.main.presntation.adapter

import android.graphics.drawable.Drawable
import android.text.format.DateFormat
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.app.weather.R
import com.app.weather.model.weather.DailyWeather
import com.app.weather.utils.getDayFromTimestamp
import com.app.weather.utils.getWeatherIcon
import com.bumptech.glide.Glide
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import java.util.*

class DaysAdapter(items: MutableList<DailyWeather>, onItemClicked: (Int) -> Unit) :
    BaseQuickAdapter<DailyWeather, BaseViewHolder>(R.layout.item_day, items) {

    init {
        setOnItemClickListener { _, _, position ->
            val item = getItem(position)
            onItemClicked(position)
        }
    }

    override fun convert(helper: BaseViewHolder, item: DailyWeather) {
        with(helper) {
            getView<TextView>(R.id.tempTV).text = String.format(Locale.getDefault(), "%.0f°", item.temp?.day)
            getView<TextView>(R.id.minTV).text = String.format(Locale.getDefault(), "%.0f°", item.temp?.min)
            getView<TextView>(R.id.maxTV).text = String.format(Locale.getDefault(), "%.0f°", item.temp?.max)
            getView<ImageView>(R.id.iconIV).setImageDrawable(getWeatherIcon(item.weather?.get(0)?.description ?: "",context))
            getView<TextView>(R.id.dayTV).text = getDayFromTimestamp(item.date)
        }
    }


}