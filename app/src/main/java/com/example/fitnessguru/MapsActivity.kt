package com.example.fitnessguru

import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Geocoder
import android.location.Location
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.LocaleList
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.example.fitnessguru.databinding.ActivityMapsBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.PolylineOptions
import com.google.gson.Gson
import okhttp3.OkHttpClient
import okhttp3.Request
import java.util.*
import kotlin.collections.ArrayList

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding
    private lateinit var currentLocation: Location

    //here for milestone 2 I am adding destination's latitude and longitude manually
    private var destinationLatitude: Double = 0.0
    private var destinationLongitude: Double = 0.0

    //retrieve last known location
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private val permissionCode = 101


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        getCurrentLocationUser()

        //start weather activity when weather button is pressed in the maps Activity
        findViewById<Button>(R.id.weatherInfo).setOnClickListener {
            val currentlatitude = currentLocation.latitude.toString()
            val currentlongitude = currentLocation.longitude.toString()

            if (!currentlatitude.equals("")) {
                val intent = Intent(this, WeatherActivity::class.java).also {
                    it.putExtra("latitudeInfo", currentlatitude)
                    it.putExtra("longitudeInfo", currentlongitude)
                    startActivity(it)
                }
            }
            else if (currentlatitude.equals("")){
                Toast.makeText(applicationContext,"no Input",Toast.LENGTH_SHORT).show()
            }

        }
    }

    // function to return the latitude and longitude of the address
    private fun addressToLatLng(address: String): String? {
        var city: String = address
        return try{
            var geoCode= Geocoder(this, Locale.getDefault())
            var addresses = geoCode.getFromLocationName("$city",1)
            var lat = addresses[0].latitude
            var lng = addresses[0].longitude
            "$lat,$lng" //return latitude and longitude separated by ","
        }catch(e: Exception){
            return null
        }
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
                    val gd = findViewById<Button>(R.id.directions)
                    gd.setOnClickListener {
                        mapFragment.getMapAsync {

                            //this enable to input the desired location
                            //calling function addressToLatLng which converts human readable address to longitude and latitude
                            val editText = findViewById<EditText>(R.id.userLocationInput)
                            val locationToLatNLag = editText.text.toString()
                            val addressFind  = addressToLatLng(locationToLatNLag)
                            //Since addressToLatLng returns $latitude,$longitude delimiter is used sto separate it
                            val delimiter=","
                            val list1 = addressFind?.split(delimiter)
                            if (list1 != null) {
                                destinationLatitude = list1.get(0).toDouble()
                                destinationLongitude = list1.get(1).toDouble()

                                //test output in logcat **** remove it *****
                                Log.e(list1.get(0),"this is latitude" )
                                Log.e(list1.get(1),"this is longitude" )
                            } else{
                                //if location not found it will print this
                                Toast.makeText(applicationContext,"Location Invalid",Toast.LENGTH_SHORT).show()
                            }

                            mMap = it
                            val originLocation =
                                LatLng(currentLocation.latitude, currentLocation.longitude)
                            mMap.addMarker(MarkerOptions().position(originLocation))
                            val destinationLocation =
                                LatLng(destinationLatitude, destinationLongitude)
                            mMap.addMarker(MarkerOptions().position(destinationLocation))

                            //here I am retrieving information via Google Direction API to get the ideal way for walking or running
                            val urlToExtractDistanceInfo = findDirectionFromURL(
                                originLocation,
                                destinationLocation,
                                "AIzaSyBWWHcXQ-1vr1MmjKKrYFh3ZwSFvSY9V30"
                            )
                            findDirection(urlToExtractDistanceInfo).execute()
                            //camera zoom after distance is known and displayed
                            mMap.animateCamera(
                                CameraUpdateFactory.newLatLngZoom(
                                    originLocation,
                                    15F
                                )
                            )
                        }
                    }
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
        val markerOptions = MarkerOptions().position(latLng).title("Current Location")
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))

        googleMap?.animateCamera(CameraUpdateFactory.newLatLng(latLng))
        googleMap?.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 17f))
        //15f represent how much zoomed in map you want when opening this activity
        //put the red marker on current position on the map
        googleMap?.addMarker(markerOptions)

    }

//this method return url to extract the information regarding distance.
    //here we are using mode walking to get the best route for fitness activity
     fun findDirectionFromURL(origin: LatLng, dest: LatLng, secret: String): String {
        return "https://maps.googleapis.com/maps/api/directions/json?origin=${origin.latitude},${origin.longitude}" +
                "&destination=${dest.latitude},${dest.longitude}" +
                "&sensor=false" +
                "&mode=walking" +
                "&key=$secret"
    }

    //retrive google maps direction from API
    //this will call decodeJsonMarking function for decoding Json file
     inner class findDirection(val url: String) :
        AsyncTask<Void, Void, List<List<LatLng>>>() {
        override fun doInBackground(vararg params: Void?): List<List<LatLng>> {
            val req = Request.Builder().url(url).build()
            val reply = OkHttpClient().newCall(req).execute()
            val info = reply.body!!.string()
            val output = ArrayList<List<LatLng>>()
            try {
                //reply from object
                val repObj = Gson().fromJson(info, MapData::class.java)
                val way = ArrayList<LatLng>()
                for (i in 0 until repObj.routes[0].legs[0].steps.size) {
                    way.addAll(decodeJsonMarking(repObj.routes[0].legs[0].steps[i].polyline.points))
                }
                output.add(way)
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return output
        }

        //this will mark the information current location to destination with the green line.
        override fun onPostExecute(result: List<List<LatLng>>) {
            val lineoption = PolylineOptions()
            for (i in result.indices) {
                lineoption.addAll(result[i])
                lineoption.width(10f)
                lineoption.color(Color.GREEN)
                lineoption.geodesic(true)
            }
            mMap.addPolyline(lineoption)
        }
    }

    /*since the information from google maps direction url in getDirectionURL() function results in json
    we have to decode it*/
    fun decodeJsonMarking(encoded: String): List<LatLng> {
        val lineMarking = ArrayList<LatLng>()
        //let i is Index
        var i = 0
        val length = encoded.length
        var latitude = 0
        var longitude = 0
        while (i < length) {
            var b: Int
            var shift = 0
            var output = 0
            do {
                b = encoded[i++].code - 63
                output = output or (b and 0x1f shl shift)
                shift += 5
            } while (b >= 0x20)
            val dlat = if (output and 1 != 0) (output shr 1).inv() else output shr 1
            latitude += dlat
            shift = 0
            output = 0
            do {
                b = encoded[i++].code - 63
                output = output or (b and 0x1f shl shift)
                shift += 5
            } while (b >= 0x20)
            val dlng = if (output and 1 != 0) (output shr 1).inv() else output shr 1
            longitude += dlng
            val latitudePlusLongitude = LatLng((latitude.toDouble() / 1E5), (longitude.toDouble() / 1E5))
            lineMarking.add(latitudePlusLongitude)
        }
        return lineMarking
    }




}

