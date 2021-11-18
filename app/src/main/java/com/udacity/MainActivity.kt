package com.udacity

import android.app.DownloadManager
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.widget.RadioButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*


class MainActivity : AppCompatActivity() {

    private var downloadID: Long = 0

    private lateinit var notificationManager: NotificationManager
    private lateinit var pendingIntent: PendingIntent
    private lateinit var action: NotificationCompat.Action

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        //For download manager
        registerReceiver(receiver, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))

        custom_button.setOnClickListener {
                download()
        }
    }

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val id = intent?.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)

            //Create Toast when download completes.
            if(id == downloadID){
                Toast.makeText(applicationContext,"Download completed",Toast.LENGTH_LONG).show()
                Log.i("MainActivity","Download completed")
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

        }else{
            //If no radioButton is selected, send a toast
            Toast.makeText(this,"Please select an option",Toast.LENGTH_SHORT).show()
        }

    }

    companion object {
        private const val CHANNEL_ID = "channelId"
    }
}
