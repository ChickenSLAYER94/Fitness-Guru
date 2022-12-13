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
import android.util.Log
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity(), SensorEventListener {
    private var sensorManager: SensorManager? = null
    private var running = false
    private var totalSteps = 0f

    private var resetCodeStepCounter = "0"
    private var resetBy = 0F
    //this is for progress bar
    private var probar = 0
    private var userStepGoal = 0
    private var userSetDistance = 0.0
    private var calBurned = 0.0
    private var distanceTravelled = 0.0
    private var remainingStepToDB = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //getting user set goal from UserGoalActivity.kt activity
        val setGoalInfo = intent.getStringExtra("UserSetGoal")

        if (setGoalInfo != null) {
            userStepGoal = setGoalInfo.toInt()
        }


        setDistanceGoalCalulate()
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager

        var helper = MyDB(applicationContext)
        var db = helper.readableDatabase

       /* Log.e(helper.getDetailsAccordingToDate().get(0).DistanceTravelled.toString(),"This is db testing")
        Log.e(helper.getDetailsAccordingToDate().get(0).UserSetGoal.toString(),"This is db testing")
        Log.e(helper.getDetailsAccordingToDate().get(0).Date.toString(),"This is db testing")*/

        //current date to add in datebase
        val formatter = SimpleDateFormat("dd-MM-yyyy")
        val date = Date()
        val current = formatter.format(date)
        Log.e(current,"***check***this")
        //start maps activity when maps button is pressed in the main activity
        //also save data to the database

        findViewById<Button>(R.id.button_maps).setOnClickListener {
            var cv = ContentValues()
            cv.put("UserSetGoal", userStepGoal.toString())
            cv.put("CalBurned", calBurned.toString())
            cv.put("DistanceTravelled", distanceTravelled.toString())
            cv.put("Progression", probar.toString())
            cv.put("RemainingSteplimit", remainingStepToDB.toString())
            cv.put("Date", current)
            db.insert("STEPCOUNTER", null, cv)
            val intent = Intent(this@MainActivity, MapsActivity::class.java)
            startActivity(intent)
        }
        findViewById<TextView>(R.id.dateCurrent).text = current

    }

    override fun onPause() {
        super.onPause()
        val formatter = SimpleDateFormat("dd-MM-yyyy")
        val date = Date()
        val current = formatter.format(date)
        Log.e(current,"***check***this")
        var helper = MyDB(applicationContext)
        var db = helper.readableDatabase
        var cv = ContentValues()

        cv.put("UserSetGoal", userStepGoal.toString())
        cv.put("CalBurned", calBurned.toString())
        cv.put("DistanceTravelled", distanceTravelled.toString())
        cv.put("Progression", probar.toString())
        cv.put("RemainingSteplimit", remainingStepToDB.toString())
        cv.put("Date", current)
        db.insert("STEPCOUNTER", null, cv)

        Toast.makeText(this, "Pause!!!", Toast.LENGTH_SHORT).show()
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

    private fun setDistanceGoalCalulate() {
        //here we 0.0008 because in average 10,000 steps is 8 kilometers
        userSetDistance = Math.round(userStepGoal * 0.0008 * 100)/100.0
        findViewById<TextView>(R.id.userDistance).text = ("Dist: $userSetDistance" + " km")
        findViewById<TextView>(R.id.userGoal).text = ("Dist: $userStepGoal" + " Goal")
    }

    //it will record and update each steps
    override fun onSensorChanged(event: SensorEvent?) {
        if (running) {
            //the initial value will be 0
            totalSteps = event!!.values[0]

            resetStep()
            if(resetStep().equals("1")){
                resetCodeStepCounter = "0"
                resetBy = totalSteps
                Log.e(totalSteps.toString(),"***total steps****")
                Log.e(resetCodeStepCounter,"***total counter****")
            }

            totalSteps = totalSteps - resetBy
            Log.e(totalSteps.toString(),"***out side111 total steps****")
            Log.e(resetBy.toString(),"***out side  steps****")


            var currentSteps = (totalSteps.toInt())
            Log.e(currentSteps.toString(),"***out c cize side  steps****")

            if (currentSteps < userStepGoal) {
                var remainingSteplimit = userStepGoal - currentSteps
                remainingStepToDB = remainingSteplimit
                Log.e(remainingSteplimit.toString(),"***remaining  steps****")
                //this will print remaining steps from daily limit.
                if(remainingSteplimit>0) {
                    findViewById<TextView>(R.id.stepsRemaining).text = ("$remainingSteplimit")
                } else if (remainingSteplimit<1){
                    findViewById<TextView>(R.id.stepsRemaining).text = ("Goal Reached")
                }
            }

            //here I am taking average data which is 10000 steps is 8 kilometers

            distanceTravelled = Math.round(currentSteps * 0.0008 * 100)/100.0 //round up
            findViewById<TextView>(R.id.distance).text = ("$distanceTravelled" + " km")

            //here I am taking average data which is 1000 steps = 49 calories burned

            calBurned = Math.round(currentSteps * 0.049 * 10)/10.0
            findViewById<TextView>(R.id.calories).text = ("$calBurned" + " kcal") //round up

            //display results in progress bar
            /*if (currentSteps > 0) {
                //changing total steps to percentage to display in pedometer UI
                val stepsToPercentage = ((currentSteps * 100) / userStepGoal)
                probar = stepsToPercentage.toInt()
                findViewById<ProgressBar>(R.id.progress_bar).progress = probar
            }*/
        }
    }

    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {
    }

    private fun resetStep(): String {
        findViewById<TextView>(R.id.stepsRemaining).setOnClickListener  {
            Toast.makeText(this, " Press long tap to reset steps", Toast.LENGTH_SHORT).show()
        }

        findViewById<Button>(R.id.input_data).setOnClickListener  {
            Toast.makeText(this, " Press long tap to reset steps", Toast.LENGTH_SHORT).show()
        }

        findViewById<TextView>(R.id.stepsRemaining).setOnLongClickListener {
            resetCodeStepCounter = "1"
            totalSteps = 0f
            findViewById<TextView>(R.id.stepsRemaining).text = userStepGoal.toString()

            true
        }

        findViewById<Button>(R.id.input_data).setOnLongClickListener {
            resetCodeStepCounter = "1"
            totalSteps = 0f
            findViewById<TextView>(R.id.stepsRemaining).text = userStepGoal.toString()

            true
        }
        return resetCodeStepCounter
    }
}