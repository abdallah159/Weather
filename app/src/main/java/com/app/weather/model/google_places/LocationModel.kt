package com.app.weather.model.google_places

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class LocationModel(
    var lat: Double,
    var lng: Double,
    var name: String
):Parcelable
