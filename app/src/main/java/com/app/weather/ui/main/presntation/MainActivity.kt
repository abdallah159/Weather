package com.app.weather.ui.main.presntation

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.*
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.app.weather.R
import com.app.weather.model.google_places.LocationModel
import com.app.weather.model.weather.WeatherModel
import com.app.weather.ui.base.BaseActivity
import com.app.weather.ui.main.presntation.adapter.CitiesAdapter
import com.app.weather.ui.main.presntation.adapter.DaysAdapter
import com.app.weather.ui.main.presntation.adapter.SearchAdapter
import com.app.weather.ui.main.viewmodel.MainViewModel
import com.app.weather.ui.map.SetLocationOnMapActivity
import com.app.weather.utils.Singleton
import com.app.weather.utils.getWeatherIcon
import com.app.weather.utils.observeOnce
import com.google.android.gms.location.*
import com.google.android.material.snackbar.Snackbar
import com.miguelcatalan.materialsearchview.MaterialSearchView
import kotlinx.android.synthetic.main.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import kotlinx.android.synthetic.main.content_main.view.*
import kotlinx.android.synthetic.main.toolbar.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.IO
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import java.util.*


class MainActivity : BaseActivity() {

    var userLocationChecker: Boolean = false
    var isRefresh: Boolean = false
    var localSavedList: MutableList<WeatherModel> = mutableListOf()

    val mainViewModel: MainViewModel by inject()
    private lateinit var mFusedLocationClient: FusedLocationProviderClient
    var locationManager: LocationManager? = null
    private val MAP_LOCATION_REQUEST = 44
    private val PERMISSION_ID = 22

    var lastLocation: LocationModel = LocationModel(0.0, 0.0, "")

    var citiesAdapter = CitiesAdapter(mutableListOf(),
        onItemClicked = {
            Singleton.instance!!.latitude = it.latitude
            Singleton.instance!!.latitude = it.longitude
            Singleton.instance!!.city = it.placeName
            mainViewModel.getWeatherByLocation(it.latitude, it.longitude)
        }, onItemDeleteClick = {
            var builder = AlertDialog.Builder(this, R.style.AlertDialogTheme)
            builder.setTitle(getString(R.string.confirm_delete))
            builder.setMessage(getString(R.string.delete_confirmation_message))
            builder.setPositiveButton(getString(R.string.yes)) { dialog, _ ->
                GlobalScope.launch(Dispatchers.IO) {
                    Singleton.instance?.appDatabase?.weatherDao()
                        ?.deletePlace(it.latitude, it.longitude)
                }
                dialog.cancel()
            }
            builder.setNegativeButton(getString(R.string.no)) { dialog, _ ->
                dialog.cancel()
            }
            var alert = builder.create()
            alert.show()

        })

    var daysAdapter = DaysAdapter(mutableListOf())

    var searchAdapter = SearchAdapter(mutableListOf(), onItemClicked = {
        mainViewModel.places.value?.get(it)?.place_id?.let { it1 ->
            mainViewModel.getPlaceDetails(
                it1
            )
        }
    })

    override fun getActivityView(): Int = R.layout.activity_main

    override fun afterInflation(savedInstance: Bundle?) {
        setSupportActionBar(toolbar)

        showLoading()
        setUpLists()
        observeViewModel()
        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        getLastLocation()

        setOnMapTV.setOnClickListener {
            startActivityForResult(
                SetLocationOnMapActivity().buildIntent(this, null),
                MAP_LOCATION_REQUEST
            )
        }

        refresh.setOnRefreshListener {
            isRefresh = true
            mainViewModel.getWeatherByLocation(
                Singleton.instance!!.latitude,
                Singleton.instance!!.longitude
            )
            mainViewModel.loading.value = false
        }
        search_view.setOnSearchViewListener(object : MaterialSearchView.SearchViewListener {
            override fun onSearchViewShown() {
                locationLY.visibility = View.VISIBLE
            }

            override fun onSearchViewClosed() {
                locationLY.visibility = View.GONE
            }

        })

        search_view.setOnQueryTextListener(object : MaterialSearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                mainViewModel.getPlaces(query ?: "")
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                mainViewModel.getPlaces(newText ?: "")
                return true
            }

        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == MAP_LOCATION_REQUEST) {
            if (data?.getParcelableExtra<LocationModel>("result") != null) {
                lastLocation = data.getParcelableExtra("result")!!
                Singleton.instance!!.latitude = lastLocation.lat
                Singleton.instance!!.longitude = lastLocation.lng
                Singleton.instance!!.city = lastLocation.name
                search_view.closeSearch()
                cityNameTV.text = Singleton.instance!!.city
                mainViewModel.getWeatherByLocation(
                    Singleton.instance!!.latitude,
                    Singleton.instance!!.longitude
                )
            }
        }
        if (requestCode == 200) {
            if (locationManager!!.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                getLastLocation()
                showLoading()
            } else {
                mainViewModel.getWeatherByLocation(
                    Singleton.instance!!.latitude,
                    Singleton.instance!!.longitude
                )
            }
        }
    }

    private fun observeViewModel() {
        mainViewModel.loading.observe(this, {
            when (it) {
                true -> showLoading()
                false -> hideLoading()
            }
        })
        mainViewModel.errorResponse.observe(this, {
            Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
        })

        mainViewModel.places.observe(this, {
            searchAdapter.data = it
            searchAdapter.notifyDataSetChanged()
        })

        mainViewModel.response.observe(this, {
            it.placeName = Singleton.instance!!.city
            refresh.isRefreshing = false
            tempTV.text = String.format(Locale.getDefault(), "%.0f°", it.current?.currentTemp)
            descTV.text = it.current?.currentWeather?.get(0)?.main
            humidityTV.text = "${it.current?.currentHumidity?.toInt()}%"
            windSpeedTV.text = "${it.current?.currentWindSpeed} km/hr"
            iconIV.setImageDrawable(
                getWeatherIcon(
                    it.current?.currentWeather?.get(0)?.description ?: "", this
                )
            )
            daysAdapter.data = it.daily?.toMutableList() ?: mutableListOf()
            daysAdapter.notifyDataSetChanged()

            if (isRefresh) {
                isRefresh = false
            } else {
                if (!(it.latitude == Singleton.instance!!.latitude && it.longitude == Singleton.instance!!.longitude)) {
                    lastLocation.lat = Singleton.instance!!.latitude
                    lastLocation.lng = Singleton.instance!!.longitude
                    lastLocation.name = Singleton.instance!!.city
                    cityNameTV.text = Singleton.instance!!.city
                    handleToSaveInDB(it)
                }
            }

        })

        mainViewModel.placeDetails.observe(this, {
            Singleton.instance!!.latitude = it.result.geometry.location.lat
            Singleton.instance!!.longitude = it.result.geometry.location.lng
            Singleton.instance!!.city = it.result.name
            cityNameTV.text = Singleton.instance!!.city
            search_view.closeSearch()
            mainViewModel.getWeatherByLocation(
                Singleton.instance!!.latitude,
                Singleton.instance!!.longitude
            )
        })

        Singleton.instance?.appDatabase?.weatherDao()?.getAll()?.observe(this, {
            citiesAdapter.data = it.toMutableList()
            citiesAdapter.notifyDataSetChanged()
        })
    }

    private fun handleToSaveInDB(weather: WeatherModel) {
        Singleton.instance?.appDatabase?.weatherDao()?.getAll()?.observeOnce(this, {
            localSavedList.clear()
            localSavedList.addAll(it.toMutableList())
            localSavedList.forEach { weatherModel ->
                if (weatherModel.latitude == weather.latitude && weather.longitude == weatherModel.longitude) {
                    weather.placeId = weatherModel.placeId
                }
            }
            saveToLocalDB(weather)
        })
    }

    fun saveToLocalDB(weather: WeatherModel) {
        GlobalScope.launch(Dispatchers.IO) {
            Singleton.instance?.appDatabase?.weatherDao()?.savePlace(weather)
        }
    }

    private fun setUpLists() {

        citiesRV.apply {
            adapter = citiesAdapter
            layoutManager =
                LinearLayoutManager(this@MainActivity, LinearLayoutManager.HORIZONTAL, false)
        }

        daysRV.apply {
            adapter = daysAdapter
            layoutManager =
                LinearLayoutManager(this@MainActivity, LinearLayoutManager.HORIZONTAL, false)
        }

        searchRV.apply {
            adapter = searchAdapter
            layoutManager =
                LinearLayoutManager(this@MainActivity, LinearLayoutManager.VERTICAL, false)
        }
    }

    override fun onBackPressed() {
        if (search_view.isSearchOpen) {
            search_view.closeSearch()
        } else {
            super.onBackPressed()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        val item: MenuItem = menu.findItem(R.id.action_search)
        search_view.setMenuItem(item)
        return true
    }


    override fun buildIntent(context: Context, data: Any?): Intent =
        Intent(context, MainActivity::class.java)


    @SuppressLint("MissingPermission")
    private fun getLastLocation() {
        if (checkPermissions()) {
            if (isLocationEnabled()) {
                requestNewLocationData()
            } else {
                showSnackMsg()
            }
        } else {
            requestPermissions()
        }
    }

    private fun isLocationEnabled(): Boolean {
        var locationManager: LocationManager =
            getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
    }

    @SuppressLint("MissingPermission")
    private fun requestNewLocationData() {
        var mLocationRequest = LocationRequest()
        mLocationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        mLocationRequest.interval = 1
        mLocationRequest.fastestInterval = 1
        mLocationRequest.numUpdates = 1

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        mFusedLocationClient.requestLocationUpdates(
            mLocationRequest, mLocationCallback,
            Looper.myLooper()
        )

        Handler().postDelayed({
            hideLoading()
            if (!userLocationChecker)
                mainViewModel.getWeatherByLocation(
                    Singleton.instance!!.latitude,
                    Singleton.instance!!.longitude,
                )
        }, 3000)

    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_ID) {
            if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                showMessage(" Permission not granted, you can change this from the settings.")
                mainViewModel.getWeatherByLocation(
                    Singleton.instance!!.latitude,
                    Singleton.instance!!.longitude
                )
            } else {
                if (isLocationEnabled()) {
                    requestNewLocationData()
                } else {
                    showSnackMsg()
                }
            }
        }
    }

    private val mLocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            var mLastLocation: Location = locationResult.lastLocation
            val geocoder = Geocoder(this@MainActivity, Locale.getDefault())
            val addresses: List<Address> =
                geocoder.getFromLocation(mLastLocation.latitude, mLastLocation.longitude, 1)
            var cityName: String = getString(R.string.cant_find_place_name)
            if (!addresses.isNullOrEmpty()) {
                cityName = addresses[0].getAddressLine(0)
            }
            Singleton.instance!!.city = cityName
            Singleton.instance!!.latitude = mLastLocation.latitude
            Singleton.instance!!.longitude = mLastLocation.longitude
            Singleton.instance!!.city = cityName

            search_view.closeSearch()
            cityNameTV.text = Singleton.instance!!.city
            mainViewModel.getWeatherByLocation(
                Singleton.instance!!.latitude,
                Singleton.instance!!.longitude
            )
            userLocationChecker = true
        }
    }

    fun showSnackMsg() {
        val snackbar = Snackbar.make(
            this.findViewById(android.R.id.content)!!,
            "Turn on location services",
            Snackbar.LENGTH_LONG
        )
        var snackCallBack = object : Snackbar.Callback() {
            override fun onDismissed(transientBottomBar: Snackbar?, event: Int) {
                super.onDismissed(transientBottomBar, event)
                mainViewModel.getWeatherByLocation(
                    Singleton.instance!!.latitude,
                    Singleton.instance!!.longitude
                )
            }
        }
        snackbar.addCallback(snackCallBack)

        snackbar.setAction("Turn on") {
            val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
            startActivityForResult(intent, 200)
            snackbar.removeCallback(snackCallBack)
        }
            .setBackgroundTint(ContextCompat.getColor(this, R.color.black))
            .setTextColor(ContextCompat.getColor(this, R.color.white))
            .setActionTextColor(ContextCompat.getColor(this, R.color.babyblue))
        snackbar.show()

    }


    private fun requestPermissions() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
            ),
            PERMISSION_ID
        )
    }

    private fun checkPermissions(): Boolean {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            return true
        }
        return false
    }


}
