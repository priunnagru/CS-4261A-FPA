package com.example.simpleloginandbroadcast

import android.app.AlertDialog
import android.content.DialogInterface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener

class BroadcastActivity : AppCompatActivity() {
    var attached = true

    // Same as root and user_info [See MainActivity] but for the emojis
    // TODO: OPTIONAL change name to reflect text instead of emoji; shift + F6 refactors
    lateinit var messageRef: DatabaseReference
    lateinit var messageDS: DataSnapshot

    var notificationId = 0;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_broadcast)

        messageRef = MainActivity.rootRef.child("emoji_info")
        messageRef.addValueEventListener(object: ValueEventListener {
            // Reminder: called when listener is attached and when data changes
            override fun onDataChange(snapshot: DataSnapshot) {
                messageDS = snapshot



                // TODO: OPTIONAL Add update to UI to show messages on page if time allows

                // Basic testing to see if attached or changed
                if (attached) {
                    //println("\n\n\n\nattached not changed\n\n\n\n")
                    attached = false
                } else {
                    val temp = messageDS.child("e${messageDS.childrenCount}").value.toString()
                    sendNotification(temp)
                }
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })

        // display the username at the bottom
        displayUsername()

        // setup the emoji buttons
        setupMessages()
    }

    private fun displayUsername() {
        val textUsernameDisplay = findViewById<TextView>(R.id.textUsernameDisplay)

        val temp = "Logged in as ${MainActivity.accountUser}."
        textUsernameDisplay.text = temp
    }

    // Currently just adds <user>:<message>
    private fun setupMessages() {
        val sendMessage = findViewById<Button>(R.id.buttonSendMessage)
        val messageBox = findViewById<EditText>(R.id.editTextMessage)

        sendMessage.setOnClickListener {
            var messageCount = messageDS.childrenCount
            val htmlMessage = "" + MainActivity.accountUser + ": " + messageBox.text

            messageRef.child("e${++messageCount}").setValue(htmlMessage)

            messageBox.text.clear()
        }

        // this simply allows you to press done on the keyboard to send the message
        messageBox.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                sendMessage.performClick()
                true
            } else {
                false
            }
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

    fun sendNotification(message : String) {
        val id = getString(R.string.channel_messages_id)
        var builder = NotificationCompat.Builder(this, id)
            .setSmallIcon(R.drawable.common_full_open_on_phone)
            .setContentTitle(message.substringBefore(":") + " broadcasted a message!")
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        with(NotificationManagerCompat.from(this)) {
            notify(notificationId++, builder.build())
        }
    }
}