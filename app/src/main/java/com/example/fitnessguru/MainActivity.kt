package com.example.fitnessguru

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast


class MainActivity : AppCompatActivity(), SensorEventListener {
    private var sensorManager: SensorManager? = null
    private var running = false
    private var totalSteps = 0f
    private var previousTotalSteps = 0f

    //this is for progress bar
    private var probar = 0
    private var userStepGoal = 0
    private var userSetDistance = 0.0
    private var calBurned = 0.0
    private var distanceTravelled = 0.0
    private var remainingSteplimit = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        //getting user set goal from UserGoalActivity.kt activity
        val setGoalInfo = intent.getStringExtra("UserSetGoal")

        if (setGoalInfo != null) {
            userStepGoal = setGoalInfo.toInt()
        }


        distanceCalulate()
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager

        var helper = MyDB(applicationContext)
        var db = helper.readableDatabase

        //start maps activity when maps button is pressed in the main activity
        //also save data to the database
        findViewById<Button>(R.id.button_maps).setOnClickListener {
            var cv = ContentValues()
            cv.put("UserSetGoal", userStepGoal.toString())
            cv.put("CalBurned", calBurned.toString())
            cv.put("DistanceTravelled", distanceTravelled.toString())
            cv.put("Progression", probar.toString())
            cv.put("RemainingSteplimit", remainingSteplimit.toString())
            db.insert("STEPCOUNTER", null, cv)

            val intent = Intent(this@MainActivity, MapsActivity::class.java)
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

    private fun distanceCalulate() {
        //here we 0.0008 because in average 10,000 steps is 8 kilometers
        userSetDistance = userStepGoal * 0.0008
        findViewById<TextView>(R.id.userDistance).text = ("Dist: $userSetDistance" + " km")
    }

    //it will record and update each steps
    override fun onSensorChanged(event: SensorEvent?) {
        if (running) {
            //the initial value will be 0
            totalSteps = event!!.values[0]
            val currentSteps = (totalSteps.toInt() - previousTotalSteps.toInt())
            if (currentSteps < userStepGoal) {
                remainingSteplimit = userStepGoal - currentSteps
                //this will print remaining steps from daily limit.
                if(remainingSteplimit>0) {
                    findViewById<TextView>(R.id.stepsRemaining).text = ("$remainingSteplimit")
                } else if (remainingSteplimit<1){
                    findViewById<TextView>(R.id.stepsRemaining).text = ("Goal Reached")
                }
            }

            //here I am taking average data which is 10000 steps is 8 kilometers
            distanceTravelled = currentSteps * 0.0008 //round up
            findViewById<TextView>(R.id.distance).text = ("$distanceTravelled" + " km")

            //here I am taking average data which is 1000 steps = 49 calories burned
            calBurned = Math.round(currentSteps * 0.049).toDouble()
            findViewById<TextView>(R.id.calories).text = ("$calBurned" + " kcal") //round up

            //display results in progress bar
            if (currentSteps > 0) {
                //changing total steps to percentage to display in pedometer UI
                val stepsToPercentage = ((currentSteps * 100) / userStepGoal)
                probar = stepsToPercentage.toInt()
                findViewById<ProgressBar>(R.id.progress_bar).progress = probar
            }
        }
    }

    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {
    }
}