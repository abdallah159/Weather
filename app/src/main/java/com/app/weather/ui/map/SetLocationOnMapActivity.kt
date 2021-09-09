package com.app.weather.ui.map

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.LocationManager
import android.os.Bundle
import android.provider.Settings
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import com.app.weather.R
import com.app.weather.model.google_places.LocationModel
import com.app.weather.ui.base.BaseActivity
import com.app.weather.utils.views.WorkaroundMapFragment
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import kotlinx.android.synthetic.main.activity_set_location_on_map.*
import java.util.*

class SetLocationOnMapActivity : BaseActivity(), WorkaroundMapFragment.OnTouchListener,
    OnMapReadyCallback {

    private var mMap: GoogleMap? = null
    private var mFusedLocationClient: FusedLocationProviderClient? = null
    private val MY_PERMISSIONS_REQUEST_LOCATION = 101

    lateinit var location: LocationModel

    override fun buildIntent(context: Context, data: Any?): Intent {
        return Intent(context, SetLocationOnMapActivity::class.java)
    }

    override fun getActivityView(): Int = R.layout.activity_set_location_on_map

    override fun afterInflation(savedInstance: Bundle?) {
        setupMap()

        closeIV.setOnClickListener { onBackPressed() }
        doneIV.setOnClickListener {
            val returnIntent = Intent()
            returnIntent.putExtra("result", location)
            setResult(Activity.RESULT_OK, returnIntent)
            finish()
        }

    }

    private fun setupMap() {
        val mapFragment =
            this.supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(this)
        (mapFragment as WorkaroundMapFragment?)?.run {
            setListener(object : WorkaroundMapFragment.OnTouchListener {
                override fun onTouch() {
                }
            })
            setListener(this@SetLocationOnMapActivity)
        }
    }

    @SuppressLint("MissingPermission")
    private fun updateLocationUI() {

        if (isLocationEnabled(this)) {
            mFusedLocationClient?.lastLocation?.addOnSuccessListener {
                it?.let {
                    val userPosition = LatLng(it.latitude, it.longitude)
                    mMap?.animateCamera(CameraUpdateFactory.newLatLngZoom(userPosition, 16.0f))
                    setCurrentLocation(userPosition)
                }
            }

            mMap?.isMyLocationEnabled = true
            mMap?.uiSettings?.isMyLocationButtonEnabled = true
        } else {
            showLocationIsDisabledAlert()
        }
    }

    private fun showLocationIsDisabledAlert() {
        val alertDialog: AlertDialog = AlertDialog.Builder(this, R.style.AlertDialogStyle).create()
        alertDialog.setTitle(getString(R.string.enable_location))
        alertDialog.setMessage(getString(R.string.we_cant_show_your_location))
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, getString(R.string.enable)) { dialog, _ ->
            dialog.dismiss()
            startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
        }

        alertDialog.setOnShowListener(DialogInterface.OnShowListener {
            alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE)
                .setTextColor(ContextCompat.getColor(this, R.color.colorPrimary))
            alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE)
                .setBackgroundColor(ContextCompat.getColor(this, R.color.white))
            alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).textSize = 16f
            alertDialog.getButton(AlertDialog.BUTTON_POSITIVE)
                .setBackgroundColor(ContextCompat.getColor(this, R.color.white))
            alertDialog.getButton(AlertDialog.BUTTON_POSITIVE)
                .setTextColor(ContextCompat.getColor(this, R.color.colorAccent))
            alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).textSize = 16f
        })

        alertDialog.show()
    }

    private fun isLocationEnabled(mContext: Context): Boolean {
        val lm = mContext.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return lm.isProviderEnabled(LocationManager.GPS_PROVIDER) || lm.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
    }


    override fun onMapReady(googleMap: GoogleMap?) {

        mMap = googleMap
        if (mMap != null) {
            mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
            if (this.hasPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                && this.hasPermission(Manifest.permission.ACCESS_COARSE_LOCATION)
            )
                updateLocationUI()
            else
                this.requestPermissionsSafely(
                    arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    ), MY_PERMISSIONS_REQUEST_LOCATION
                )

            mMap?.setOnCameraIdleListener {
                val midLatLng: LatLng = mMap?.cameraPosition?.target!!
                setCurrentLocation(midLatLng)
            }
        }
    }


    private fun setCurrentLocation(latLng: LatLng) {
        try {
            var geocoder = Geocoder(this, Locale.getDefault())

            var addresses: List<Address> =
                geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1)
            var cityName = addresses.get(index = 0).getAddressLine(0)

            location = LocationModel(latLng.latitude, latLng.longitude, cityName)

        } catch (e: Exception) {
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            MY_PERMISSIONS_REQUEST_LOCATION -> {
                if (grantResults.size > 1 && grantResults[0] + grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    updateLocationUI()
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        updateLocationUI()
    }

    override fun onTouch() {

    }

}