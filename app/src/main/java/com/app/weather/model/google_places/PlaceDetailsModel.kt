package com.app.weather.model.google_places

data class PlaceDetailsModel(
    val html_attributions: MutableList<Any>,
    val result: Result,
    val status: String
) {
    data class Result(
        val address_components: MutableList<AddressComponent>,
        val adr_address: String,
        val formatted_address: String,
        val geometry: Geometry,
        val icon: String,
        val id: String,
        val name: String,
        val photos: MutableList<Photo>,
        val place_id: String,
        val reference: String,
        val scope: String,
        val types: MutableList<String>,
        val url: String,
        val utc_offset: Int,
        val vicinity: String
    ) {
        data class AddressComponent(
            val long_name: String,
            val short_name: String,
            val types: MutableList<String>
        )

        data class Geometry(
            val location: Location,
            val viewport: Viewport
        ) {
            data class Location(
                val lat: Double,
                val lng: Double
            )

            data class Viewport(
                val northeast: Northeast,
                val southwest: Southwest
            ) {
                data class Northeast(
                    val lat: Double,
                    val lng: Double
                )

                data class Southwest(
                    val lat: Double,
                    val lng: Double
                )
            }
        }

        data class Photo(
            val height: Int,
            val html_attributions: MutableList<String>,
            val photo_reference: String,
            val width: Int
        )
    }
}