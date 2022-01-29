package com.example.simpleloginandbroadcast

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.EditText
import android.widget.Toast

class RegisterActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        // setup the login form
        setupRegister()
    }

    lateinit var message : String
    private fun setupRegister() {
        val buttonRegister = findViewById<Button>(R.id.buttonRegister)
        val inputUsername = findViewById<EditText>(R.id.inputUsername)
        val inputPassword = findViewById<EditText>(R.id.inputPassword)
        val inputRetypePassword = findViewById<EditText>(R.id.inputRetypePassword)

        val intentBroadcast = Intent(this, MainActivity::class.java)
        buttonRegister.setOnClickListener {
            if (attemptRegister(inputUsername.text.toString(),
                    inputPassword.text.toString(),
                    inputRetypePassword.text.toString())) {
                startActivity(intentBroadcast)
            }
            Toast.makeText(applicationContext, message, Toast.LENGTH_SHORT).show()
            inputUsername.text.clear()
            inputPassword.text.clear()
            inputRetypePassword.text.clear()
        }

        // this simply allows you to press done on the keyboard to login
        inputRetypePassword.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                buttonRegister.performClick()
                true
            } else {
                false
            }
        }
    }

    // Compares user to those in DB, if not empty and not match AND pass is retyped correctly,
    // adds login to DB
    private fun attemptRegister(username : String, password : String, retypePassword : String) : Boolean {
        for (child in MainActivity.userInfoDS.children) {
            var user = child.child("username").value.toString()

            if (username == "") {
                message = "Username cannot be empty.\nPlease try again."
                return false
            } else if (username.lowercase() == user.lowercase()) {
                message = "Username already exist.\nPlease try again."
                return false
            } else if (password != retypePassword) {
                message = "The password do not match.\nPlease try again"
                return false
            }
        }

        var numUsers = MainActivity.userInfoDS.childrenCount
        val newUserRef = MainActivity.userInfoRef.child("user_${++numUsers}")
        newUserRef.child("username").setValue(username)
        newUserRef.child("password").setValue(password)

        message = "Registered account.\nPlease log in."
        return true
    }
}