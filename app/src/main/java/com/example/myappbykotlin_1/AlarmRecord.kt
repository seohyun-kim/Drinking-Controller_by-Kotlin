package com.example.myappbykotlin_1

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.myappbykotlin_1.databinding.ActivityAlarmRecordBinding
import kotlinx.android.synthetic.main.activity_alarm_record.*

class AlarmRecord : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_alarm_record)

        //뷰 바인딩
        val binding= ActivityAlarmRecordBinding.inflate(layoutInflater) //뷰 바인딩 사용 준비
        setContentView(binding.root)

        var goalData:Double = 100.00 //default
        if (intent.hasExtra("goalValue")) {
            goalData =intent.getStringExtra("goalValue")!!.toDouble()
            Log.d("goalData", "goalData $goalData")
            binding.goalText.text =intent.getStringExtra("goalValue") + "ml"

        } else {
            //Toast.makeText(this, "전달된 이름이 없습니다", Toast.LENGTH_SHORT).show()
            // 토스트 안됨 ㅠ
            Log.d("goalData", "goalData 안 들어왔음!!")
        }

        var cumDataReceived :Float = 120F; //블루투스 수신한 누적량 데이터 변수에 저장 (임시로 100)
        var cupData :Float = cumDataReceived/50

        //임시 버튼 (나중엔 블루투스 값 들어올때마다 자동으로 새로고침 되도록)
        //버튼 클릭 시 데이터 새로 입력
        binding.updateBtn.setOnClickListener{
            binding.cumData.text = cumDataReceived.toString() + " ml "
            binding.cupText.text= " = " + cupData.toString()+" 잔"

            if (cumDataReceived > goalData){
                binding.msgText.text = "목표량 초과! 멈춰!!!"
                binding.msgText.setTextColor(Color.parseColor("#FF0000"))
            }
            else if (cumDataReceived > goalData -50)
            {
                binding.msgText.text = "어? 어?! 그만 그만!!"
                binding.msgText.setTextColor(Color.parseColor("#FF1111"))
            }
            else if (cumDataReceived > goalData -130)
            {
                binding.msgText.text = "목표량에 다다르고 있어요!"
                binding.msgText.setTextColor(Color.parseColor("#FF7F00"))
            }
            else if (cumDataReceived > goalData -200)
            {
                binding.msgText.text = "아직까지는 괜찮아요."
                binding.msgText.setTextColor(Color.parseColor("#0067A3"))
            }
            else{
                binding.msgText.text = "술이 달다~"
                binding.msgText.setTextColor(Color.parseColor("#008000"))
            }

            //test (버튼 클릭시마다 1잔씩 추가되도록)
            cumDataReceived+=50
            cupData = cumDataReceived/50
        }  //소주 1잔 50ml


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