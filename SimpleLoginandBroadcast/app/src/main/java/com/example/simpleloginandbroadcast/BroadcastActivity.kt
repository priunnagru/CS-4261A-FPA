package com.example.simpleloginandbroadcast

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView

class BroadcastActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_broadcast)

        val textUsernameDisplay = findViewById<TextView>(R.id.textUsernameDisplay)

        val extras = intent.extras
        if (extras != null) {
            val temp = "Logged in as " + extras.getString("username") + "."
            textUsernameDisplay.setText(temp)
        }
    }
}