package com.example.fitnessguru

import android.content.Context
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast


class MainActivity : AppCompatActivity(), SensorEventListener {

    private var sensorManager: SensorManager? = null
    private var running = false
    private var totalSteps = 0f
    private var previousTotalSteps = 0f

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager

        //start maps activity when maps button is pressed in the main activity
        findViewById<Button>(R.id.button_maps).setOnClickListener {
            val intent = Intent(this@MainActivity, MapsActivity::class.java)
            startActivity(intent)
        }

        //start maps activity when weather button is pressed in the main Activity
        findViewById<Button>(R.id.button_weather).setOnClickListener {
            val intent = Intent(this@MainActivity, WeatherActivity::class.java)
            startActivity(intent)
        }

    }

    override fun onResume() {
        super.onResume()
        running = true
        val stepSensor: Sensor? = sensorManager?.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)

        //this message will show if Step counter is not found (usually in the case of old phones)
        if (stepSensor == null) {
            Toast.makeText(this, "Sensor not detected", Toast.LENGTH_SHORT).show()

        } else {
            sensorManager?.registerListener(this, stepSensor, SensorManager.SENSOR_DELAY_UI)
        }
    }

    //it will record and update each steps
    override fun onSensorChanged(event: SensorEvent?) {
        if (running) {
            //the initial value will be 0
            totalSteps = event!!.values[0]

            val currentSteps = totalSteps.toInt() - previousTotalSteps.toInt()
            findViewById<TextView>(R.id.textView).text = ("$currentSteps")
        }
    }

    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {
    }
}