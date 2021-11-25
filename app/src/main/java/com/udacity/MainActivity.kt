package com.udacity

import android.annotation.SuppressLint
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
import android.os.Handler
import android.util.Log
import android.widget.RadioButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getSystemService
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*

// Notification ID.
private val NOTIFICATION_ID = 0
private val REQUEST_CODE = 0


class MainActivity : AppCompatActivity() {

    private var downloadID: Long = 0
    private var fileName: String = ""
    private var selectedRadioButtonText = ""
    var downloadStatus: Int = 0
    private var totalBytes: Int = 0

    private val handler: Handler = Handler()

    private lateinit var notificationManager: NotificationManager
    private lateinit var pendingIntent: PendingIntent
    private lateinit var action: NotificationCompat.Action
    private lateinit var downloadResult: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setSupportActionBar(toolbar)

        //Instantiate notification manager
        notificationManager = getSystemService(applicationContext, NotificationManager::class.java) as NotificationManager

        //For download manager. Using the DownloadReceiver class which extends BroadcastReceiver to alter when files are downloaded.
        registerReceiver(downloadReceiver, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))

        //Create the notidication channel for downloads.
        createChannel(CHANNEL_ID, CHANNEL_NAME,notificationManager)

        custom_button.setOnClickListener {
                download()
        }

    }

    private val downloadReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent?) {
            val id = intent?.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)

            //Create Toast when download completes.
            if(id == downloadID){
                //Toast.makeText(applicationContext,"Download completed",Toast.LENGTH_LONG).show()
                //Log.i("MainActivity","Download completed")

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

            selectedRadioButtonText = selectedRadioButton.text.toString()
            Log.i("MainActivity", selectedRadioButtonText)

            val url = selectedRadioButton.contentDescription.toString()
            Log.i("MainActivity",selectedRadioButton.contentDescription.toString())

            //Get the file name from the URI
            val urlWithNoSpace = "${selectedRadioButton.contentDescription}".filter { !it.isWhitespace() }
            val index = urlWithNoSpace.lastIndexOf("/")
            fileName = urlWithNoSpace.substring(index+1)
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

            //var downloadProgress: DownloadProgressUpdater.DownloadProgressListener
            //downloadProgress = DownloadProgressUpdater(downloadManager, downloadID,downloadProgress)
            getDownloadStatus(downloadManager,downloadID)


            //notificationManager.sendNotification(CHANNEL_ID,"test", applicationContext)

        }else{
            //If no radioButton is selected, send a toast
            Toast.makeText(this,"Please select an option",Toast.LENGTH_SHORT).show()
        }
    }

    companion object {
        private const val CHANNEL_ID = "Download Notification Channel"
        private const val CHANNEL_NAME = "Download Notifications"

    }

    fun NotificationManager.sendNotification(channelId: String, messageBody: String, applicationContext: Context){

        //Intent to make the notification clickable.
        val contentIntent = Intent(applicationContext, MainActivity::class.java) //MainActivity specified as the destination for the intent.
        val contentPendingIntent = PendingIntent.getActivity(
            applicationContext,
            NOTIFICATION_ID,
            contentIntent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )

        //Intent for the button to do to the DetailActivity.
        val detailIntent = Intent(applicationContext, DetailActivity::class.java)  //DetailActivity specified as the destination for the intent.
        detailIntent.putExtra("fileName", selectedRadioButtonText) //Pass value with intent
        detailIntent.putExtra("status", downloadResult) //Pass value with intent

        val pendingIntent: PendingIntent = PendingIntent.getActivity(
            applicationContext,
            REQUEST_CODE,
            detailIntent,
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

            //Add button and the Intent for the button
            .addAction(
                R.drawable.ic_assistant_black_24dp,
                "File details",
                pendingIntent
            )

            //Additional style options for large image
            .setStyle(NotificationCompat.BigPictureStyle()
                .bigPicture(zipImage)
                .bigLargeIcon(null))

        notify(0, builder.build())
    }

    fun getDownloadStatus(manager: DownloadManager, downloadId: Long) {
        val query: DownloadManager.Query = DownloadManager.Query()
        query.setFilterById(downloadId)

        while (downloadStatus <= 2) {
            Thread.sleep(500)

            manager.query(query).use {
                if (it.moveToFirst()) {

                    totalBytes = it.getInt(it.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES))
                    Log.i("Main Activity","Total bytes for this file is: $totalBytes Kbps")

                    downloadStatus = it.getInt(it.getColumnIndex(DownloadManager.COLUMN_STATUS))
                    //Log.i("Main Activity","Download status is $downloadStatus")
                    val bytesDownloadedSoFar = it.getInt(it.getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR))
                    Log.i("Main Activity","Bytes downloaded so far: $bytesDownloadedSoFar Kbps")

                    when (downloadStatus){
                        DownloadManager.STATUS_SUCCESSFUL -> {
                            Log.i("Main Activity","Download successful")
                            downloadResult = "Download successful"
                        }
                        DownloadManager.STATUS_FAILED -> {
                            Log.i("Main Activity","Download failed")
                            downloadResult = "Download failed"
                        }
                        DownloadManager.STATUS_RUNNING -> {
                            Log.i("Main Activity", "Still downloading")
                            //update progress
                            val percentProgress = ((bytesDownloadedSoFar * 100L) / totalBytes)
                            android.util.Log.i("Main Activity","Download progress: $percentProgress")
                        }
                    }
                }
            }
        }

        //Reset the download status once complete
        downloadStatus = 0
    }
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

class DownloadCompletedQuery(private val manager: DownloadManager, private val downloadId: Long) {
    private val query: DownloadManager.Query = DownloadManager.Query()
    var downloadStatus: Int = 0
     private var totalBytes: Int = 0

     init {
        query.setFilterById(this.downloadId)
        run()
    }

    @SuppressLint("Range") //Suppresses errors from code below.
    fun run() {
        while (downloadStatus <= 2) {
            Thread.sleep(500)

            manager.query(query).use {
                if (it.moveToFirst()) {

                    totalBytes = it.getInt(it.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES))
                    Log.i("Main Activity","Total bytes for this file is: $totalBytes Kbps")

                    downloadStatus = it.getInt(it.getColumnIndex(DownloadManager.COLUMN_STATUS))
                    //Log.i("Main Activity","Download status is $downloadStatus")
                    val bytesDownloadedSoFar = it.getInt(it.getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR))
                    Log.i("Main Activity","Bytes downloaded so far: $bytesDownloadedSoFar Kbps")

                    when (downloadStatus){
                        DownloadManager.STATUS_SUCCESSFUL -> {
                            Log.i("Main Activity","Download successful")
                        }
                        DownloadManager.STATUS_FAILED -> {
                            Log.i("Main Activity","Download failed")
                        }
                        DownloadManager.STATUS_RUNNING -> {
                            Log.i("Main Activity", "Still downloading")
                            //update progress
                            val percentProgress = ((bytesDownloadedSoFar * 100L) / totalBytes)
                            android.util.Log.i("Main Activity","Download progress: $percentProgress")
                        }
                    }
                }
            }
        }
    }
}

