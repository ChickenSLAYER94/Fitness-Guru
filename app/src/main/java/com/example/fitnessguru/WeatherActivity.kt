package com.example.fitnessguru

import androidx.appcompat.app.AppCompatActivity
import android.os.AsyncTask
import android.os.Bundle
import android.view.View
import android.widget.*
import org.json.JSONObject
import java.net.URL
import java.text.SimpleDateFormat
import java.util.*

class WeatherActivity : AppCompatActivity() {
    //weather api from openweathermap.org
    val API: String = "14933351abd3008db5d2821f5287684b"
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_weather)
        weatherInfoRetrival().execute()
    }

    inner class weatherInfoRetrival() : AsyncTask<String, Void, String>() {
        override fun onPreExecute() {
            super.onPreExecute()
            /* showing loading screen and hiding allLayout (main layout) or error message screen */
            findViewById<ProgressBar>(R.id.loadingScreen).visibility = View.VISIBLE
            findViewById<RelativeLayout>(R.id.allLayout).visibility = View.GONE
            findViewById<TextView>(R.id.errorOrInternet).visibility = View.GONE
        }

        override fun doInBackground(vararg params: String?): String? {
            var response:String?
            try{
                //api url
                //here latitude and longitude is manually added for this tutorial
                response = URL("https://api.openweathermap.org/data/2.5/weather?lat=53.33&lon=-6.27&units=metric&appid=$API").readText(
                    Charsets.UTF_8
                )
            }catch (e: Exception){
                response = null
            }
            return response
        }

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)
            try {
                /* Extracting information from the API whcih is in JSON */
                val jsonObj = JSONObject(result)
                val main = jsonObj.getJSONObject("main")
                val sys = jsonObj.getJSONObject("sys")
                val wind = jsonObj.getJSONObject("wind")
                val weather = jsonObj.getJSONArray("weather").getJSONObject(0)
                val temperature = main.getString("temp")
                val tempMinimum = main.getString("temp_min")
                val tempMaximum = main.getString("temp_max")
                val pres = main.getString("pressure")
                val humid = main.getString("humidity")
                val sunrise:Long = sys.getLong("sunrise")
                val sunset:Long = sys.getLong("sunset")
                val windSpeed = wind.getString("speed")
                val weatherDescription = weather.getString("description")
                val address = jsonObj.getString("name")+", "+sys.getString("country")

                /* Adding the extracted information from json file to respective Text-View*/
                findViewById<TextView>(R.id.locationAddress).text =  address
                val weaDesc = weatherDescription.capitalize()
                findViewById<TextView>(R.id.weatherStatus).text = weaDesc
                //rounding digit in temperature
                findViewById<TextView>(R.id.temp).text = Math.round(temperature.toDouble()).toString()+"°C"
                findViewById<TextView>(R.id.minTemp).text = "Min Temp: " + Math.round(tempMinimum.toDouble()).toString()+"°C"
                findViewById<TextView>(R.id.maxTemp).text = "Max Temp: " + Math.round(tempMaximum.toDouble()).toString()+"°C"
                val sunriseTime = SimpleDateFormat("hh:mm a", Locale.ENGLISH).format(Date(sunrise*1000))
                findViewById<TextView>(R.id.sunrise).text = sunriseTime
                val sunsetTime = SimpleDateFormat("hh:mm a", Locale.ENGLISH).format(Date(sunset*1000))
                findViewById<TextView>(R.id.sunset).text = sunsetTime
                findViewById<TextView>(R.id.wind).text = windSpeed
                findViewById<TextView>(R.id.pressure).text = pres
                findViewById<TextView>(R.id.humidity).text = humid


                /* hide loading screen and start allLayout*/
                findViewById<ProgressBar>(R.id.loadingScreen).visibility = View.GONE

                findViewById<RelativeLayout>(R.id.allLayout).visibility = View.VISIBLE

            } catch (e: Exception) {
//                if error occurs or no internet present
                findViewById<ProgressBar>(R.id.loadingScreen).visibility = View.GONE
                findViewById<TextView>(R.id.errorOrInternet).visibility = View.VISIBLE
            }

        }
    }
}