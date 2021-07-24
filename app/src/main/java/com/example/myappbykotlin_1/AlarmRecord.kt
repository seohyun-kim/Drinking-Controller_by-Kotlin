package com.example.myappbykotlin_1

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.myappbykotlin_1.databinding.ActivityAlarmRecordBinding

class AlarmRecord : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_alarm_record)

        //뷰 바인딩
        val binding= ActivityAlarmRecordBinding.inflate(layoutInflater) //뷰 바인딩 사용 준비
        setContentView(binding.root)


        //data
        var goalData:Float = 300F //임시로 목표값 지정
        var cumDataReceived :Float = 120F; //블루투스 수신한 누적량 데이터 변수에 저장 (임시로 100)
        var cupData :Float = cumDataReceived/50
        binding.goalText.text = goalData.toString() + " ml = " + (goalData/50).toString()+"잔"

        //임시 버튼 (나중엔 블루투스 값 들어올때마다 자동으로 새로고침 되도록)
        //버튼 클릭 시 데이터 새로 입력
        binding.updateBtn.setOnClickListener{
            binding.cumData.text = cumDataReceived.toString() + " ml "
            binding.cupText.text= " = " + cupData.toString()+" 잔"

            if (cumDataReceived > goalData){
                binding.msgText.text = " 목표량을 초과하였습니다."
                binding.msgText.setTextColor(Color.parseColor("#FF0000"))
            }
            else if (cumDataReceived > goalData -100)
            {
                binding.msgText.text = " 목표량에 다다르고 있어요! 멈춰볼까요?"
                binding.msgText.setTextColor(Color.parseColor("#FF7F00"))
            }
            else{
                binding.msgText.text = " 아직까지는 괜찮아요. 적당히 마실거죠?"
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