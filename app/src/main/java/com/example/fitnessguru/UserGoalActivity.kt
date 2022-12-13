package com.example.fitnessguru

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.KeyEvent
import android.view.inputmethod.EditorInfo
import android.widget.*

class UserGoalActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_goal)

        val userGender = listOf("Male", "Female", "Other", "Prefer not to say")
        val adapter = ArrayAdapter(this, R.layout.gender_list, userGender)
        findViewById<AutoCompleteTextView>(R.id.auto_complete_selection).setAdapter(adapter)


        val button = findViewById<Button>(R.id.userDataConfirm)


        //before going to the step counter
        //it will ask for digit input
        //if the user input nothing it will display toast method
        //if valid digit is added it will then proceed to main activity with user step goal limit shared to next activity
        button.setOnClickListener {
            val validateStepsInput = findViewById<EditText>(R.id.userInput)
            val validateHeight = findViewById<EditText>(R.id.userHeight)
            val validateWeight = findViewById<EditText>(R.id.userWeight)

            val stepsMessage = validateStepsInput.text.toString()
            val heightMessage = validateHeight.text.toString()
            val weightMessage = validateWeight.text.toString()

            if (!stepsMessage.equals("") && !heightMessage.equals("") && !weightMessage.equals("")) {
                //validation using regex
                if (stepsMessage.matches("^[0-9]*\$".toRegex()) && heightMessage.matches("^[0-9]*\$".toRegex()) && weightMessage.matches(
                        "^[0-9]*\$".toRegex()
                    )
                ) {
                    val intent = Intent(this, MainActivity::class.java).also {
                        it.putExtra("UserSetGoal", stepsMessage)
                        startActivity(it)
                        println("Test")

                    }
                } else if (!stepsMessage.matches("^[0-9]*\$".toRegex()) || !heightMessage.matches("^[0-9]*\$".toRegex()) || !weightMessage.matches(
                        "^[0-9]*\$".toRegex()
                    )
                ) {
                    Toast.makeText(applicationContext, "Input number only", Toast.LENGTH_SHORT)
                        .show()
                }
            } else if (stepsMessage.equals("")) {
                Toast.makeText(
                    applicationContext,
                    "Please input your daily limit",
                    Toast.LENGTH_SHORT
                )
                    .show()
            } else if (heightMessage.equals("")) {
                Toast.makeText(
                    applicationContext,
                    "Please input your daily limit",
                    Toast.LENGTH_SHORT
                )
                    .show()
            } else if (weightMessage.equals("")) {
                Toast.makeText(
                    applicationContext,
                    "Please input your daily limit",
                    Toast.LENGTH_SHORT
                )
                    .show()
            }


        }
    }
}
