package com.app.weather.ui.main.presntation.adapter

import android.widget.ImageView
import android.widget.TextView
import com.app.weather.R
import com.app.weather.model.weather.DailyWeather
import com.app.weather.utils.getDayFromTimestamp
import com.app.weather.utils.getWeatherIcon
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import java.util.*

class DaysAdapter(private val items: MutableList<DailyWeather>) :
    BaseQuickAdapter<DailyWeather, BaseViewHolder>(R.layout.item_day, items) {

    override fun convert(helper: BaseViewHolder, item: DailyWeather) {
        with(helper) {
            getView<TextView>(R.id.tempTV).text = String.format(Locale.getDefault(), "%.0f°", item.temp?.day)
            getView<TextView>(R.id.minTV).text = String.format(Locale.getDefault(), "%.0f°", item.temp?.min)
            getView<TextView>(R.id.maxTV).text = String.format(Locale.getDefault(), "%.0f°", item.temp?.max)
            getView<ImageView>(R.id.iconIV).setImageDrawable(getWeatherIcon(item.weather?.get(0)?.description ?: "", context))
            getView<TextView>(R.id.dayTV).text = getDayFromTimestamp(item.date)
        }
    }


}