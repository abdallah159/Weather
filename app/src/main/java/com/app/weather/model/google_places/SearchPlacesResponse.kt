package com.app.weather.model.google_places

data class SearchPlacesResponse(
    val predictions: MutableList<SearchPlaceModel>,
    val status: String
)