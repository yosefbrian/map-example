package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.common.api.Status
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.android.libraries.places.widget.AutocompleteSupportFragment
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener
import kotlinx.android.synthetic.main.activity_maps.*


class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    val TAG = "TAG"
    private lateinit var fusedLocationClient : FusedLocationProviderClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        supportActionBar?.hide()
        configureMap()
        configureSearch()
    }

    private fun configureMap(){
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    private fun configureSearch(){
        Places.initialize(applicationContext, getString(R.string.google_maps_key))
        val placesClient: PlacesClient = Places.createClient(this)

        val autocompleteFragment = supportFragmentManager.findFragmentById(R.id.autocomplete_fragment) as AutocompleteSupportFragment?
        autocompleteFragment!!.setPlaceFields(listOf(Place.Field.ID, Place.Field.LAT_LNG, Place.Field.ADDRESS, Place.Field.NAME))
        autocompleteFragment.setOnPlaceSelectedListener(object : PlaceSelectionListener {
            override fun onPlaceSelected(place: Place) {
                place.latLng?.let {
                    setLocation(it)
                    btnSelect.visibility = View.VISIBLE
                }

                tvAddress.text = place.address

                val intent = Intent()

                btnSelect.setOnClickListener {
                    setResult(555, intent.putExtra("Address", place.address))
                    finish()
                }
            }

            override fun onError(status: Status) {
                Log.i(TAG, "An error occurred: $status")
            }

        })
    }


    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        fusedLocationClient.lastLocation.addOnSuccessListener {
            setLocation(LatLng(it.latitude, it.longitude))
        }
    }

    private fun setLocation(latLng: LatLng){
        mMap.addMarker(MarkerOptions().position(latLng).title("Your Location"))
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng))
        val zoomLevel = 18.0.toFloat()
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoomLevel))
    }
}
