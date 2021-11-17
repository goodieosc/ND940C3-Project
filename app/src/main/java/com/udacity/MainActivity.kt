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

        registerReceiver(receiver, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))



        custom_button.setOnClickListener {
//            if (radioGroup.isSelected == true){
                download()
//            } else {
//                Toast.makeText(this,"Please select an option",Toast.LENGTH_SHORT).show()
//            }



        }
    }

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val id = intent?.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
        }
    }

    private fun download() {

        val selectedRadioButtonId = radioGroup.checkedRadioButtonId


        if (selectedRadioButtonId != -1) {

            //Set the value or url to the content description of the selected radiobutton
            val selectedRadioButton: RadioButton = findViewById(selectedRadioButtonId)
            val url = selectedRadioButton.contentDescription.toString()
            Log.i("MainActivity",selectedRadioButton.contentDescription.toString())



            val request =
                DownloadManager.Request(Uri.parse(url))
                    .setTitle(getString(R.string.app_name))
                    .setDescription(getString(R.string.app_description))
                    .setRequiresCharging(false)
                    .setAllowedOverMetered(true)
                    .setAllowedOverRoaming(true)

            val downloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
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
