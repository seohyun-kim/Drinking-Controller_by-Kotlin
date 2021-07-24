package com.example.myappbykotlin_1

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.EditText;
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

        // 버튼 클릭 시 화면 전환
        //binding.btnBackPage.setOnClickListener{
        //val nextIntent = Intent(this, AlarmRecord::class.java)
        //startActivity(nextIntent)
        //Log.d("Btn", "Alarm Record Btn is clicked! method = Log.d")
    //} //binding 변수로 뷰에서 만든 버튼에 접근 가능
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}