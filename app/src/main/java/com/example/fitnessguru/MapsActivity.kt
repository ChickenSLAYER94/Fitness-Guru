package com.example.fitnessguru

import android.content.ContentProviderClient
import android.content.pm.PackageManager
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.LocaleList
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.example.fitnessguru.databinding.ActivityMapsBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding
    private lateinit var currentLocation: Location

    //retrieve last known location
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private val permissionCode = 101


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        getCurrentLocationUser()

    }

    private fun getCurrentLocationUser() {
        //here we check permission is granted or not
        //if the permission is not granted, permission is requested
        if (ActivityCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) !=
            PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_COARSE_LOCATION
            ) !=
            PackageManager.PERMISSION_GRANTED
        ) {

            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                permissionCode
            )
            return
        }
        //addOnSuccessListener is used when task completes successfully
        val getLocation =
            fusedLocationProviderClient.lastLocation.addOnSuccessListener { location ->
                //check if location is null or not because there might be a situation where location is turned off in setting
                if (location != null) {
                    currentLocation = location
                    //toast message to display longitude and latitude
                    Toast.makeText(
                        applicationContext, currentLocation.latitude.toString() + "" +
                                currentLocation.longitude.toString(), Toast.LENGTH_LONG
                    ).show()

                    //this function notify when the map is ready to be used.
                    val mapFragment = supportFragmentManager
                        .findFragmentById(R.id.map) as SupportMapFragment
                    mapFragment.getMapAsync(this)

                }
            }
    }

    //this will request for location permission
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            //location permission granted
            permissionCode -> if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getCurrentLocationUser()
            }
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        //latLng combination of latitude and longitude coordinate and store as degrees
        val latLng = LatLng(
            currentLocation.latitude,
            currentLocation.longitude
        ) //this function is triggered when is ready to be used and provide a non null instance of google map
        /*Create a Marker to the map*/
        val marketOptions = MarkerOptions().position(latLng).title("Current Location")

        googleMap?.animateCamera(CameraUpdateFactory.newLatLng(latLng))
        googleMap?.animateCamera(
            CameraUpdateFactory.newLatLngZoom(
                latLng,
                15f
            )
        )
        //15f represent how much zoomed in map you want when opening this activity
        //put the red marker on current position on the map
        googleMap?.addMarker(marketOptions)
    }

}

