package com.example.fitnessguru

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.inputmethod.EditorInfo
import android.widget.*
import java.text.SimpleDateFormat
import java.util.*

class UserGoalActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_goal)
        val button = findViewById<Button>(R.id.userDataConfirm)

        //before going to the step counter
        //it will ask for digit input
        //if the user input nothing it will display toast messgae
        //if valid digit is added it will then proceed to main activity with user step goal limit shared to next activity
        button.setOnClickListener {
            val validateStepsInput = findViewById<EditText>(R.id.userInput)
            val stepsMessage = validateStepsInput.text.toString()


            if (!stepsMessage.equals("")) {
                //validation using regex
                if (stepsMessage.matches("^[0-9]*\$".toRegex())
                ) {
                    val intent = Intent(this, MainActivity::class.java).also {
                        it.putExtra("UserSetGoal", stepsMessage)
                        startActivity(it)
                        println("Test")

                    }
                } else if (!stepsMessage.matches("^[0-9]*\$".toRegex())
                ) {
                    Toast.makeText(applicationContext, "Input digit only", Toast.LENGTH_SHORT)
                        .show()
                }
            } else if (stepsMessage.equals("")) {
                Toast.makeText(
                    applicationContext,
                    "Please input your daily step goal",
                    Toast.LENGTH_SHORT
                )
                    .show()
            }


        }
    }
}
