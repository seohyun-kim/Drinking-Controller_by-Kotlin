package com.example.myappbykotlin_1

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.EditText;
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.myappbykotlin_1.databinding.ActivityAlarmModeBinding
import com.example.myappbykotlin_1.databinding.ActivityAlarmRecordBinding
import com.example.myappbykotlin_1.databinding.ActivityMainBinding
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
        val binding=ActivityAlarmModeBinding.inflate(layoutInflater) //뷰 바인딩 사용 준비
        setContentView(binding.root) //화면 안의 버튼 사용 가능

        switchPush.setOnCheckedChangeListener { CompoundButton, onSwitch ->
            if (onSwitch) {
                var NOTIFICATION_ID = 1001;

                var builder = NotificationCompat.Builder( this, "myChannel")
                    .setSmallIcon(R.drawable.back)
                    .setContentTitle("Test Alarm")
                    .setContentText("testing")
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT);


                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    val name = "channel name";
                    val descriptionText = "test Channel";
                    val importance = NotificationManager.IMPORTANCE_DEFAULT;
                    val channel = NotificationChannel("myChannel", name, importance).apply {
                        description = descriptionText;
                    }
                    // Register the channel with the system
                    val notificationManager: NotificationManager =
                        getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager;
                    notificationManager.createNotificationChannel(channel);
                }

                //NotificationManagerCompat.from(this).notify(NOTIFICATION_ID, builder.build());

                with(NotificationManagerCompat.from(this)) {
                    // notificationId is a unique int for each notification that you must define
                    notify(NOTIFICATION_ID, builder.build());
                }
            }
        }

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

                    startActivity(intent);
                }
            }
        }

    }
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}