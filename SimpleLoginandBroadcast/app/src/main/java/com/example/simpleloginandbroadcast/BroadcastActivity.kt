package com.example.simpleloginandbroadcast

import android.app.ActivityManager
import android.app.AlertDialog
import android.app.PendingIntent
import android.app.TaskStackBuilder
import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.method.ScrollingMovementMethod
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
    val NUM_MESSAGES_DB = 5

    // Same as root and user_info [See MainActivity] but for the emojis
    lateinit var messageRef: DatabaseReference
    lateinit var messageDS: DataSnapshot
    lateinit var messageListener: ValueEventListener

    var notificationId = 0;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_broadcast)

        val receivedMessages = findViewById<TextView>(R.id.textViewReceivedMessages)
        receivedMessages.movementMethod = ScrollingMovementMethod()

        messageRef = MainActivity.rootRef.child("message_info")
        messageListener = messageRef.addValueEventListener(object: ValueEventListener {
            // Reminder: called when listener is attached and when data changes
            override fun onDataChange(snapshot: DataSnapshot) {
                messageDS = snapshot

                // Basic testing to see if attached or changed
                if (attached) {
                    attached = false
                } else {
                    val temp = messageDS.child("m${messageDS.childrenCount}").value.toString()
                    sendNotification(temp)

                    if (messageDS.childrenCount > 100) {
                        messageRef.setValue(null)
                        attached = true     // Abuse of variable; when changed to null, onDataChange does nothing
                    }
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
            if (messageBox.text.isNotBlank()) {
                var messageCount = messageDS.childrenCount
                val htmlMessage = "" + MainActivity.accountUser + ": " + messageBox.text

                messageRef.child("m${++messageCount}").setValue(htmlMessage)

                messageBox.text.clear()
            }
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
                    messageRef.removeEventListener(messageListener)
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
        val receivedMessages = findViewById<TextView>(R.id.textViewReceivedMessages)

        receivedMessages.text = "${receivedMessages.text}\n$message"

        val curr = ActivityManager.RunningAppProcessInfo()
        ActivityManager.getMyMemoryState(curr)
        if (curr.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
            return
        }

        val id = getString(R.string.channel_messages_id)
        val builder = NotificationCompat.Builder(this, id)
            .setSmallIcon(R.drawable.common_full_open_on_phone)
            .setContentTitle(message.substringBefore(":") + " broadcasted a message!")
            .setContentText(message)
            .setStyle(NotificationCompat.BigTextStyle()
                .bigText(message))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .setContentIntent(PendingIntent.getActivity(this, 0,
                Intent(this, BroadcastActivity :: class.java), PendingIntent.FLAG_MUTABLE));
            //.setContentIntent(resultPendingIntent)

        with(NotificationManagerCompat.from(this)) {
            notify(notificationId++, builder.build())
        }
    }
}