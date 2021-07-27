package com.example.myappbykotlin_1

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.myappbykotlin_1.databinding.ActivityAlarmModeBinding
import kotlinx.android.synthetic.main.activity_alarm_mode.*

class AlarmModeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_alarm_mode)

        //actionbar
        val actionbar = supportActionBar
        //set actionbar title
        actionbar!!.title = "Alarm Mode"
        //set back button
        actionbar.setDisplayHomeAsUpEnabled(true)
        actionbar.setDisplayHomeAsUpEnabled(true)

        //뷰 바인딩
        val binding = ActivityAlarmModeBinding.inflate(layoutInflater) //뷰 바인딩 사용 준비
        setContentView(binding.root) //화면 안의 버튼 사용 가능

        // notification 설정
        var NOTIFICATION_ID = 1001;
        var channelID = "myChannel";
        var channelName = "channel name";
        var channelDiscription = "test Channel"

        var pushOption: Boolean = false;
        switchPush.setOnCheckedChangeListener { CompoundButton, onSwitch ->
            pushOption = onSwitch;
        }
        createNotificationChannel(channelID, channelName, channelDiscription)



        binding.btnInput.setOnClickListener {
            val value: String = inputValue.text.toString();
            if (value.isEmpty()) { // 입력창이 비어있는지 확인
                println("Write to edit text");
            } else {
                val goalValue: Double? = value.toDoubleOrNull();
                if (goalValue == null) { // 입력값이 Double형이 맞는지 확인
                    println("Please write Double!");
                } else { // 맞으면 AlarmRecord로 입력값을 보냄
                    val intent = Intent(this, AlarmRecord::class.java)
                    intent.putExtra("goalValue", value);

                    // notify
                    var builder = NotificationCompat.Builder( this, channelID)
                        .setSmallIcon(R.drawable.mainpage_beer)
                        .setContentTitle("Input Value")
                        .setContentText(value)
                        .setPriority(NotificationCompat.PRIORITY_HIGH);

                    if (pushOption == true) {
                        with(NotificationManagerCompat.from(this)) {
                            notify(NOTIFICATION_ID, builder.build());
                        }
                    }
                    startActivity(intent);
                }
            }
        }

    }
    private fun createNotificationChannel(
        channelID: String,
        channelName: String,
        channelDiscription: String
    ) {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = channelName
            val descriptionText = channelDiscription
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(channelID, name, importance).apply {
                description = descriptionText
            }
            // Register the channel with the system
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}