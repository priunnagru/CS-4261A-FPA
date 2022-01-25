package com.example.simpleloginandbroadcast

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val buttonLogin = findViewById<Button>(R.id.buttonLogin)
        val inputUsername = findViewById<EditText>(R.id.inputUsername)
        val inputPassword = findViewById<EditText>(R.id.inputPassword)

        val intentBroadcast = Intent(this, BroadcastActivity::class.java)
        buttonLogin.setOnClickListener {
            val username = inputUsername.text.toString()
            val password = inputPassword.text.toString()
            if (username == "admin" && password == "admin") {
                intentBroadcast.putExtra("username", username)
                inputUsername.setText("")
                inputPassword.setText("")

                startActivity(intentBroadcast)
            }
        }
    }
}