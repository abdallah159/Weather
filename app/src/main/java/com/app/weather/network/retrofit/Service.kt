package  com.app.weather.network.retrofit

import com.app.weather.BuildConfig
import com.app.weather.model.google_places.PlaceDetailsModel
import com.app.weather.model.google_places.SearchPlacesResponse
import com.app.weather.model.weather.WeatherModel
import io.reactivex.Observable
import retrofit2.http.*


interface Service {

    @GET("onecall")
    fun getWeather(@Query("lat") lat: Double,@Query("lon") lon: Double): Observable<WeatherModel>

    @GET("maps/api/place/autocomplete/json")
    fun getPlaces(
        @Query("key") key: String = BuildConfig.GOOGLE_MAPS_API_KEY,
        @Query("input") input: String
    ): Observable<SearchPlacesResponse>

    @GET("maps/api/place/details/json")
    fun getPlaceDetails(
        @Query("key") key: String = BuildConfig.GOOGLE_MAPS_API_KEY,
        @Query("placeid") placeid: String
    ): Observable<PlaceDetailsModel>

}