package com.example.fitnessguru

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.KeyEvent
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast

class UserGoalActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_goal)

        val  button = findViewById<Button>(R.id.userDataConfirm)


        //before going to the step counter
        //it will ask for digit input
        //if the user input nothing it will display toast method
        //if valid digit is added it will then proceed to main activity with user step goal limit shared to next activity
        button.setOnClickListener{
            val editText = findViewById<EditText>(R.id.userInput)
            val message = editText.text.toString()

            if (!message.equals("")) {
                val intent = Intent(this, MainActivity::class.java).also {
                    it.putExtra("UserSetGoal", message)
                    startActivity(it)
                }
            }
            else if (message.equals("")){
                Toast.makeText(applicationContext,"Please input your daily limit",Toast.LENGTH_SHORT).show()
            }

        }
    }
}
