package com.example.simpleloginandbroadcast

import android.app.AlertDialog
import android.content.DialogInterface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast

class BroadcastActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_broadcast)

        // display the username at the bottom
        displayUsername()

        // setup the emoji buttons
        setupEmoji()
    }

    private fun displayUsername() {
        val textUsernameDisplay = findViewById<TextView>(R.id.textUsernameDisplay)

        val extras = intent.extras
        if (extras != null) {
            val temp = "Logged in as " + extras.getString("username") + "."
            textUsernameDisplay.text = temp
        }
    }

    private fun setupEmoji() {
        // TODO: broadcast the corresponding emoji
        val emoji1 = findViewById<Button>(R.id.buttonEmoji1)
        val emoji2 = findViewById<Button>(R.id.buttonEmoji2)
        val emoji3 = findViewById<Button>(R.id.buttonEmoji3)

        emoji1.setOnClickListener {
            // TODO: send emoji 1
        }

        emoji2.setOnClickListener {
            // TODO: send emoji 2
        }

        emoji3.setOnClickListener {
            // TODO: send emoji 3
        }
    }

    override fun onBackPressed() {
        // create a dialog box to confirm logging out
        val builder = AlertDialog.Builder(this)
        builder.setMessage("Are you sure you want to log out?")
        builder.apply {
            setPositiveButton("Yes",
                DialogInterface.OnClickListener { _, _ ->
                    // TODO: logic for disconnecting from the web server and logging out

                    val message = "Logged out."
                    Toast.makeText(applicationContext, message, Toast.LENGTH_SHORT).show()
                    super.onBackPressed()
                })
            setNegativeButton("No",
                DialogInterface.OnClickListener { _, _ ->
                    // do nothing
                })
        }

        builder.show()
    }

    // makes the up navigation button match functionality with the back button
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
}