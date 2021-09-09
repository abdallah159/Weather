package  com.app.weather.network


import com.app.weather.App
import com.app.weather.model.google_places.PlaceDetailsModel
import com.app.weather.model.google_places.SearchPlacesResponse
import com.app.weather.model.weather.WeatherModel
import com.app.weather.network.retrofit.Service
import io.reactivex.Observable

class AppRepository : Service {
    override fun getWeather(lat: Double,lon:Double): Observable<WeatherModel> {
        return App.getService.getWeather(lat,lon)
    }

    override fun getPlaces(key: String, input: String): Observable<SearchPlacesResponse> {
        return App.getGoogleService.getPlaces(input = input)
    }

    override fun getPlaceDetails(key: String, placeid: String): Observable<PlaceDetailsModel> {
        return App.getGoogleService.getPlaceDetails(placeid = placeid)
    }
}
