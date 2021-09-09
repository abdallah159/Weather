package com.app.weather.ui.main.presntation.adapter

import android.widget.ImageView
import android.widget.TextView
import com.app.weather.R
import com.app.weather.model.google_places.SearchPlaceModel
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder

class SearchAdapter(
    val items: MutableList<SearchPlaceModel>,
    val onItemClicked: (Int) -> Unit
) :
    BaseQuickAdapter<SearchPlaceModel, BaseViewHolder>(R.layout.item_search_city, items) {

    init {
        setOnItemClickListener { _, _, position ->
            val item = getItem(position)
            onItemClicked(position)
        }
    }

    override fun convert(helper: BaseViewHolder, item: SearchPlaceModel) {
        with(helper) {
            getView<TextView>(R.id.placeNameTV).text = item.description
            itemView.setOnClickListener {
                onItemClicked(adapterPosition)
            }
        }
    }
}