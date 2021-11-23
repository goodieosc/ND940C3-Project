package com.udacity

import android.app.DownloadManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.BitmapFactory
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.widget.RadioButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat.getSystemService
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*

// Notification ID.
private val NOTIFICATION_ID = 0

class MainActivity : AppCompatActivity() {

    private var downloadID: Long = 0



    private lateinit var notificationManager: NotificationManager
    private lateinit var pendingIntent: PendingIntent
    private lateinit var action: NotificationCompat.Action

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setSupportActionBar(toolbar)

        //Instantiate notification manager
        notificationManager = getSystemService(applicationContext, NotificationManager::class.java) as NotificationManager

        //For download manager. Using the DownloadReceiver class which extends BroadcastReceiver to alter when files are downloaded.
        registerReceiver(receiver, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))

        //Create the notidication channel for downloads.
        createChannel(CHANNEL_ID, CHANNEL_NAME,notificationManager)

        custom_button.setOnClickListener {
                download()
        }

    }

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent?) {
            val id = intent?.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)

            //Create Toast when download completes.
            if(id == downloadID){
                //Toast.makeText(applicationContext,"Download completed",Toast.LENGTH_LONG).show()
                Log.i("MainActivity","Download completed")

                // Call the sendNotification() extension function with the notification message
                notificationManager.sendNotification(CHANNEL_ID, getString(R.string.notification_description),applicationContext)
            }
        }
    }

    private fun download() {

        val selectedRadioButtonId = radioGroup.checkedRadioButtonId

        //If a radio button is selected, download the file the from the URI in the contentDescription of the selected radio box. .
        if (selectedRadioButtonId != -1) {

            //Set the value or URI to the content description of the selected radiobutton
            val selectedRadioButton: RadioButton = findViewById(selectedRadioButtonId)
            val url = selectedRadioButton.contentDescription.toString()
            Log.i("MainActivity",selectedRadioButton.contentDescription.toString())

            //Get the file name from the URI
            val urlWithNoSpace = "${selectedRadioButton.contentDescription}".filter { !it.isWhitespace() }
            val index = urlWithNoSpace.lastIndexOf("/")
            val fileName = urlWithNoSpace.substring(index+1)
            Log.i("MainActivity",fileName)

            val request =
                DownloadManager.Request(Uri.parse(url))
                    .setTitle(getString(R.string.app_name))
                    .setDescription(getString(R.string.app_description))
                    .setRequiresCharging(false)
                    .setAllowedOverMetered(true)
                    .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS,fileName) //Specify the downloads direction as the destination. If not specified, they get saved within the app itself.
                    .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE)
                    .setAllowedOverRoaming(true)

            val downloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager //as specifies the data type
            downloadID = downloadManager.enqueue(request)// enqueue puts the download request in the queue.

            //notificationManager.sendNotification(CHANNEL_ID,"texting cunt", applicationContext)


        }else{
            //If no radioButton is selected, send a toast
            Toast.makeText(this,"Please select an option",Toast.LENGTH_SHORT).show()
        }
    }

    companion object {
        private const val CHANNEL_ID = "Download Notification Channel"
        private const val CHANNEL_NAME = "Download Notifications"

    }
}

fun NotificationManager.sendNotification(channelId: String, messageBody: String, applicationContext: Context){

    //Intent to make the notification clickable.
    val contentIntent = Intent(applicationContext, MainActivity::class.java)
    val contentPendingIntent = PendingIntent.getActivity(
        applicationContext,
        NOTIFICATION_ID,
        contentIntent,
        PendingIntent.FLAG_UPDATE_CURRENT
    )

    val zipImage = BitmapFactory.decodeResource(
        applicationContext.resources,
        R.drawable.zip_file)


    val builder = NotificationCompat.Builder(applicationContext, channelId)
        .setSmallIcon(R.drawable.ic_assistant_black_24dp)
        .setContentTitle(applicationContext.getString(R.string.notification_title))
        .setContentText(messageBody)
        .setLargeIcon(zipImage)

        //Intent to make the notification clickable.
        .setContentIntent(contentPendingIntent)
        .setAutoCancel(true) //Cancels the notification when clicked.
        
        //Additional style options for large image
        .setStyle(NotificationCompat.BigPictureStyle()
            .bigPicture(zipImage)
            .bigLargeIcon(null))

        notify(0, builder.build())

}

private fun createChannel(channelId: String, channelName: String, notificationManager: NotificationManager) {

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) { //Check version, notifications channels only available from API 26.
        val notificationChannel = NotificationChannel(
            channelId, //Channel ID from function constructor
            channelName, //Channel name passed from function constructor
            NotificationManager.IMPORTANCE_HIGH //Importance level level
        )

        //Set aspects of the notification
        notificationChannel.enableLights(true) //Phone light flashing upon notification
        notificationChannel.lightColor = Color.RED //Flash red
        notificationChannel.enableVibration(true) //Vibrate
        notificationChannel.description = R.string.notification_description.toString()

        //Call createNotificationChannel on NotificationManager and pass notificationChannel
        notificationManager.createNotificationChannel(notificationChannel)

    }

}
