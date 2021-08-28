package com.example.myappbykotlin_1

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.SystemClock
import android.util.Log
import android.widget.TimePicker
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.view.get
import com.example.myappbykotlin_1.databinding.ActivityAlarmModeBinding
import kotlinx.android.synthetic.main.activity_alarm_mode.*
import java.util.*


private const val M_SHARED_PREFERENCE_NAME = "time"
private const val M_ALARM_KEY = "alarm"
private const val M_ONOFF_KEY = "onOFF"
private val M_ALARM_REQUEST_CODE = 1000
class AlarmModeActivity : AppCompatActivity() {
    private var alarmMgr: AlarmManager? = null
    private lateinit var pendingIntent: PendingIntent
    val calendar: Calendar = Calendar.getInstance()


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


        var goHomeOption: Boolean = false;
        switchGoHome.setOnCheckedChangeListener { CompoundButton, onSwitch ->
            goHomeOption = onSwitch;
        }




        binding.btnInput.setOnClickListener {
            val value: String = inputValue.text.toString();
            if (value.isEmpty()) { // 입력창이 비어있는지 확인
                println("Write to edit text");
                var t1 = Toast.makeText(this, "목표값을 입력해 주세요.", Toast.LENGTH_SHORT)
                t1.show()
            } else {
                val goalValue: Int? = value.toIntOrNull();
                if (goalValue == null) { // 입력값이 Int형이 맞는지 확인
                    println("Please write Double!");
                    var t2 = Toast.makeText(this, "자연수로 입력해 주세요.", Toast.LENGTH_SHORT)
                    t2.show()
                } else { // 맞으면 AlarmRecord로 입력값을 보냄
                    val intent = Intent(this, AlarmRecord::class.java)
                    intent.putExtra("goalValue", value);
                    intent.putExtra("push", pushOption);
                    intent.putExtra("goHome", goHomeOption);


                    if (goHomeOption == true) {
                        val mTimePicker: TimePicker = findViewById((R.id.timePicker))
                        val hour: Int
                        val min:  Int
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            hour = mTimePicker.hour
                            min  = mTimePicker.minute
                            Log.d("hour", hour.toString())
                            Log.d("minute", min.toString())
                        }
                        else {
                            hour = mTimePicker.currentHour
                            min = mTimePicker.currentMinute
                        }

                        intent.putExtra("hour", hour);
                        intent.putExtra("minute", min);

                    }
                    else {
                        Log.d("cancel Alarm", "cancel Alarm")
                        cancelAlarm()
                    }

                    startActivity(intent);
//                    val alarmReceiverBroadcast: AlarmReceiver =
//                        AlarmReceiver()
//                    alarmReceiverBroadcast.onReceive(this, intent)
                }
            }
        }

    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    private fun cancelAlarm() {
        val pendingIntent = PendingIntent.getBroadcast(
            this,
            M_ALARM_REQUEST_CODE,
            Intent(this, AlarmRecord::class.java),
            PendingIntent.FLAG_NO_CREATE
        )
        pendingIntent?.cancel()
    }

    class AlarmReceiver : BroadcastReceiver() {

        companion object {
            const val TAG = "AlarmReceiver"
            const val NOTIFICATION_ID = 1001
            const val PRIMARY_CHANNEL_ID = "channel name"
        }

        lateinit var notificationManager: NotificationManager

        override fun onReceive(context: Context, intent: Intent) {
            Log.d(TAG, "Received intent : $intent")
            notificationManager = context.getSystemService(
                Context.NOTIFICATION_SERVICE
            ) as NotificationManager

            deliverNotification(context)
        }

        private fun deliverNotification(context: Context) {
            val contentIntent = Intent(context, AlarmModeActivity::class.java)
            val contentPendingIntent = PendingIntent.getActivity(
                context,
                NOTIFICATION_ID,
                contentIntent,
                PendingIntent.FLAG_UPDATE_CURRENT
            )
            val builder =
                NotificationCompat.Builder(context, PRIMARY_CHANNEL_ID)
                    .setContentTitle("Alert")
                    .setContentText("This is repeating alarm")
                    .setContentIntent(contentPendingIntent)
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setAutoCancel(true)
                    .setDefaults(NotificationCompat.DEFAULT_ALL)
            Log.d("alarm", "notify")
//            notificationManager.notify(NOTIFICATION_ID, builder.build())
        }
    }
}