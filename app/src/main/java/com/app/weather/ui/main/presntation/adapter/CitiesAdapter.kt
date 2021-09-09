package com.app.weather.ui.main.presntation.adapter

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.app.weather.R
import com.app.weather.model.weather.WeatherModel
import com.app.weather.utils.getWeatherIcon
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder

class CitiesAdapter(val items: MutableList<WeatherModel>,
                    val onItemClicked: (WeatherModel) -> Unit,
                    val onItemDeleteClick: (WeatherModel) -> Unit
) :
    BaseQuickAdapter<WeatherModel, BaseViewHolder>(R.layout.item_city, items) {

    override fun convert(helper: BaseViewHolder, item: WeatherModel) {
        with(helper) {
            getView<TextView>(R.id.cityNameTV).text = item.placeName
            getView<ImageView>(R.id.deleteIV).setOnClickListener {
                onItemDeleteClick(item)
                items.remove(item)
                notifyDataSetChanged()
            }

            itemView.setOnClickListener {
                onItemClicked(item)
            }
        }
    }
}