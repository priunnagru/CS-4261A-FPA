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
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener

class BroadcastActivity : AppCompatActivity() {
    // Same as root and user_info [See MainActivity] but for the emojis
    // TODO: OPTIONAL change name to reflect text instead of emoji; shift + F6 refactors
    lateinit var emojiRef: DatabaseReference
    lateinit var emojiDS: DataSnapshot

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_broadcast)

        emojiRef = MainActivity.rootRef.child("emoji_info")
        emojiRef.addValueEventListener(object: ValueEventListener {
            // Reminder: called when listener is attached and when data changes
            override fun onDataChange(snapshot: DataSnapshot) {
                emojiDS = snapshot
                // TODO: Add function call to broadcast notifications when the data is changed
                    // TODO: Make sure no messages sent when attached only changed
                // TODO: OPTIONAL Add update to UI to show messages on page if time allows
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })

        // display the username at the bottom
        displayUsername()

        // setup the emoji buttons
        setupEmoji()
    }

    private fun displayUsername() {
        val textUsernameDisplay = findViewById<TextView>(R.id.textUsernameDisplay)

        val temp = "Logged in as ${MainActivity.accountUser}."
        textUsernameDisplay.text = temp
    }

    // Currently just adds <e#, 1 or 2 or 3 and user> based on the button
    // So can tell who sent it and which emoji was sent
    // TODO: change to be text messages, and add user and message to DB on clicking "Send" button
    private fun setupEmoji() {
        val emoji1 = findViewById<Button>(R.id.buttonEmoji1)
        val emoji2 = findViewById<Button>(R.id.buttonEmoji2)
        val emoji3 = findViewById<Button>(R.id.buttonEmoji3)

        emoji1.setOnClickListener {
            var emojiCount = emojiDS.childrenCount
            emojiRef.child("e${++emojiCount}").setValue("1 ${MainActivity.accountUser}")
        }

        emoji2.setOnClickListener {
            var emojiCount = emojiDS.childrenCount
            emojiRef.child("e${++emojiCount}").setValue("2 ${MainActivity.accountUser}")
        }

        emoji3.setOnClickListener {
            var emojiCount = emojiDS.childrenCount
            emojiRef.child("e${++emojiCount}").setValue("3 ${MainActivity.accountUser}")
        }
    }

    override fun onBackPressed() {
        // create a dialog box to confirm logging out
        val builder = AlertDialog.Builder(this)
        builder.setMessage("Are you sure you want to log out?")
        builder.apply {
            setPositiveButton("Yes",
                DialogInterface.OnClickListener { _, _ ->
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