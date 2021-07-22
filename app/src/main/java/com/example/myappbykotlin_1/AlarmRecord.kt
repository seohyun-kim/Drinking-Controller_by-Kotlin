package com.example.myappbykotlin_1

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class AlarmRecord : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_alarm_record)

        //actionbar
        val actionbar = supportActionBar
        //set actionbar title
        actionbar!!.title = "Alarm Mode Record"
        //set back button
        actionbar.setDisplayHomeAsUpEnabled(true)
        actionbar.setDisplayHomeAsUpEnabled(true)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true

    }
}