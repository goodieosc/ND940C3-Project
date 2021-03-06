package com.udacity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_detail.*
import kotlinx.android.synthetic.main.content_detail.*

class DetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)
        setSupportActionBar(toolbar)

        val fileName = intent.getStringExtra("fileName")
        val status = intent.getStringExtra("status")

        fileNameValueTextView.setText("$fileName")
        statusValueTextView.setText("$status")


        okButton.setOnClickListener {
            motionLayout.transitionToEnd{startActivity(Intent(applicationContext,MainActivity::class.java))}

        }
    }
}
