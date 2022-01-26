package com.example.simpleloginandbroadcast

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.EditText
import android.widget.Toast

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // setup the login form
        setupLogin()
    }

    private fun setupLogin() {
        val buttonLogin = findViewById<Button>(R.id.buttonLogin)
        val inputUsername = findViewById<EditText>(R.id.inputUsername)
        val inputPassword = findViewById<EditText>(R.id.inputPassword)

        val intentBroadcast = Intent(this, BroadcastActivity::class.java)
        buttonLogin.setOnClickListener {
            if (attemptLogin(inputUsername.text.toString(), inputPassword.text.toString())) {
                inputUsername.text.clear()
                inputPassword.text.clear()

                // TODO: connect to the web server
                startActivity(intentBroadcast)
                val message = "Logged in."
                Toast.makeText(applicationContext, message, Toast.LENGTH_SHORT).show()
            } else {
                val message = "Failed to login."
                Toast.makeText(applicationContext, message, Toast.LENGTH_SHORT).show()
            }
        }

        // this simply allows you to press done on the keyboard to login
        inputPassword.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                buttonLogin.performClick()
                true
            } else {
                false
            }
        }
    }

    private fun attemptLogin(username : String, password : String) : Boolean {
        // TODO: logic for authenticating
        if (username == "admin" && password == "admin") {
            return true
        }

        return false
    }
}