package com.app.weather.ui.main.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.app.weather.model.google_places.PlaceDetailsModel
import com.app.weather.model.google_places.SearchPlaceModel
import com.app.weather.model.google_places.SearchPlacesResponse
import com.app.weather.model.weather.WeatherModel
import com.app.weather.network.AppRepository
import com.app.weather.network.retrofit.CallbackWrapper
import com.app.weather.ui.base.BaseViewModel
import com.app.weather.utils.Singleton
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class MainViewModel(private val repository: AppRepository) : BaseViewModel() {
    var response: MutableLiveData<WeatherModel> = MutableLiveData()
    var localWeatherList: MutableLiveData<ArrayList<WeatherModel>> = MutableLiveData()
    var errorResponse: MutableLiveData<String> = MutableLiveData()

    var places: MutableLiveData<MutableList<SearchPlaceModel>> = MutableLiveData()

    var placeDetails: MutableLiveData<PlaceDetailsModel> = MutableLiveData()

    fun getWeatherByLocation(lat: Double, lon: Double) {
        loading.value = true
        mCompositeDisposable.add(
            repository.getWeather(lat, lon)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object : CallbackWrapper<WeatherModel>() {
                    override fun onSuccess(t: WeatherModel) {
                        loading.value = false
                        response.postValue(t)
                    }

                    override fun onFail(t: String?) {
                        loading.value = false
                        errorResponse.postValue(t)
                    }
                })
        )
    }

    fun getPlaces(input: String) {
        mCompositeDisposable.add(
            repository.getPlaces(input = input)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object :
                    CallbackWrapper<SearchPlacesResponse>() {
                    override fun onSuccess(t: SearchPlacesResponse) {
                        places.postValue(t.predictions)
                    }

                    override fun onFail(t: String?) {
                        errorResponse.postValue(t)
                    }
                })
        )
    }

    fun getPlaceDetails(placeId: String) {
        mCompositeDisposable.add(
            repository.getPlaceDetails(placeid = placeId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object :
                    CallbackWrapper<PlaceDetailsModel>() {
                    override fun onSuccess(t: PlaceDetailsModel) {
                        placeDetails.postValue(t)
                    }

                    override fun onFail(t: String?) {}

                })
        )
    }



}
