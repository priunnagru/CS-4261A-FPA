package com.example.simpleloginandbroadcast

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity() {

    // Static reference to root of DB, and user_info child of DB, as well as their data snapshots
    // Use DB Reference for getting children and setting values
    // Use DB snapshot for getting children and getting values
    // accountUser is for the "Logged in as ..." on the Broadcast page
    companion object {
        lateinit var rootRef: DatabaseReference
        lateinit var rootDS: DataSnapshot

        lateinit var userInfoRef: DatabaseReference
        lateinit var userInfoDS: DataSnapshot

        lateinit var accountUser: String
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Init both references and add a listener
        rootRef = Firebase.database.reference
        rootRef.addValueEventListener(object: ValueEventListener {
            // Runs when attached and when data (including that of children) changes
            override fun onDataChange(snapshot: DataSnapshot) {
                rootDS = snapshot
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })

        userInfoRef = rootRef.child("user_info")
        userInfoRef.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                userInfoDS = snapshot
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })

        // create notification channel
        createNotificationChannel()

        // setup the login form
        setupLogin()
    }

    lateinit var message : String
    private fun setupLogin() {
        val buttonLogin = findViewById<Button>(R.id.buttonLogin)
        val buttonToRegister = findViewById<Button>(R.id.buttonToRegister)
        val inputUsername = findViewById<EditText>(R.id.inputUsername)
        val inputPassword = findViewById<EditText>(R.id.inputPassword)

        var intentBroadcast : Intent

        buttonLogin.setOnClickListener {
            intentBroadcast = Intent(this, BroadcastActivity::class.java)
            if (attemptLogin(inputUsername.text.toString(), inputPassword.text.toString())) {
                startActivity(intentBroadcast)
            }
            Toast.makeText(applicationContext, message, Toast.LENGTH_SHORT).show()
            inputUsername.text.clear()
            inputPassword.text.clear()
        }
        buttonToRegister.setOnClickListener {
            intentBroadcast = Intent(this, RegisterActivity::class.java)
            startActivity(intentBroadcast)
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

    // Compares passed in user and pass to those in DB and if matches one, logs in
    private fun attemptLogin(username : String, password : String) : Boolean {
        for (child in userInfoDS.children) {
            var user = child.child("username").value.toString()
            var pass = child.child("password").value.toString()

            if (username == user && password == pass) {
                accountUser = user
                message = "Logged in."
                return true
            }
        }

        message = "Failed to login."
        return false
    }

    private fun createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = getString(R.string.channel_messages)
            val descriptionText = getString(R.string.channel_messages_description)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val id = getString(R.string.channel_messages_id)
            val channel = NotificationChannel(id, name, importance).apply {
                description = descriptionText
            }
            // Register the channel with the system
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
}