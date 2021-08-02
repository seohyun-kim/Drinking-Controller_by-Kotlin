package com.example.myappbykotlin_1

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toolbar
import com.example.myappbykotlin_1.databinding.ActivityMessureModeBinding
class MessureModeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_messure_mode)

        //actionbar
        val actionbar = supportActionBar
        //set actionbar title
        actionbar!!.title = "Messure Mode"
        //set back button
        actionbar.setDisplayHomeAsUpEnabled(true)
        actionbar.setDisplayHomeAsUpEnabled(true)

        //뷰 바인딩
        val binding=ActivityMessureModeBinding.inflate(layoutInflater) //뷰 바인딩 사용 준비
        setContentView(binding.root) //화면 안의 버튼 사용 가능
        binding.resultBtn.setOnClickListener{
            val nextIntent = Intent(this,MessureRecord::class.java)
            startActivity(nextIntent)
            Log.d("Btn", "result Btn is clicked! method = Log.d")
        } //binding 변수로 뷰에서 만든 버튼에 접근 가능
    }
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}
//package com.example.myapplication
//
///*import android.app.NotificationChannel
//import android.app.NotificationManager
//import android.content.Context
//import android.graphics.Color
//import android.os.Build
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import androidx.core.app.NotificationCompat
//import androidx.core.app.NotificationManagerCompat
//import androidx.recyclerview.widget.LinearLayoutManager
//import androidx.recyclerview.widget.RecyclerView */
//import android.content.Intent
//import android.os.Build
//import androidx.appcompat.app.AppCompatActivity
//import android.os.Bundle
//import android.util.Log
//import androidx.annotation.RequiresApi
//import com.example.myappbykotlin_1.R
//import java.util.Random  //임시로 랜덤함수 쓰기위해 import
//import java.time.LocalDateTime
//import kotlinx.android.synthetic.main.activity_messure_mode.*
//import java.time.format.DateTimeFormatter
//import com.example.myappbykotlin_1.databinding.ActivityMessureModeBinding
//
//class MessureModeActivity : AppCompatActivity() {
//
//    var cumDataReceived :Float = 0F; //블루투스 수신한 누적량 데이터 변수에 저장 (초기 0)
//    var cupData :Float = cumDataReceived/50
//    var recordList = ArrayList<String>();// 기록 값이 들어갈 동적 배열
//    val rand=Random()
//    var currentData =0 // 현재 마신 양 (누적X)
//    var cnt=0; //회차 확인용
//
//
//    private fun <float> reset(num:float){
//        currentData=0
//    }
//
//
//    @RequiresApi(Build.VERSION_CODES.O)
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_messure_mode)
//
//        //actionbar
//        val actionbar = supportActionBar
//        //set actionbar title
//        actionbar!!.title = "Messure Mode"
//        //set back button
//        actionbar.setDisplayHomeAsUpEnabled(true)
//        actionbar.setDisplayHomeAsUpEnabled(true)
//
//        //뷰 바인딩
//        val binding = ActivityMessureModeBinding.inflate(layoutInflater) //뷰 바인딩 사용 준비
//        setContentView(binding.root) //화면 안의 버튼 사용 가능
//
//        //1. 누적량 출력
//        binding.resetBtn.setOnClickListener { //알람모드 코드 참고
//
//            //DateTime
//            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"
//            val date_=LocalDate.now().format($formatter)
//
//            //test (버튼 클릭시마다 랜덤으로 추가되도록)
//            currentData = rand.nextInt(30) + 20 //랜덤으로 현재 마신 양 넣음
//            cnt += 1
//            recordList.add(cnt.toString() + "회차 : " + currentData.toString() + " ml \n")
//            Log.d("recordList", recordList.toString())
//            cumDataReceived += currentData
//            cupData = cumDataReceived / 50
//
//            binding.cumData.text = cumDataReceived.toString() + " ml "
//            binding.cupText.text = " = " + cupData.toString() + " 잔"
//
//        }
//
//
//        //2.다시하기(초기화) 버튼
//        resetBtn.setOnClickListener{
//            reset(0)
//        }
//
//
//        //3.결과 확인 버튼 (다른 페이지와 연동) 방법 1, 2, 3 -> ...실행 되는 거... 있을까요...
//
//        binding.resultBtn.setOnClickListener {
//            val go_intent = Intent(this, BActivity::class.java)
//            startActivity(go_intent)
//            Log.d("Btn", "Alarm Record Btn is clicked! method = Log.d")
//        } //binding 변수로 뷰에서 만든 버튼에 접근 가능,
//
//        val go_intent = findViewById(R.id.resultBtn) as Button
//        go_intent.setOnClickListener {
//            val intent = Intent(this@MainActivity, BActivity::class.java)
//            startActivity(intent)
//        }
//
//        override fun onCreate(savedInstanceState: Bundle?) {
//            super.onCreate(savedInstanceState)
//            setContentView(R.layout.activity_main)
//
//            val secondIntent = Intent(this, BActivity::class.java) // 인텐트를 생성
//
//            resultBtn.setOnClickListener { // 버튼 클릭시 할 행동
//                startActivity(secondIntent)  // 화면 전환하기
//            }
//        }
//
//
//    }
//
//}
//
///*3. 결과 확인 버튼(화면 전환) 구글링(위 방법3)
//class MainActivity : AppCompatActivity() {
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_main)
//
//        val secondIntent = Intent(this, SecondActivity::class.java) // 인텐트를 생성
//
//        btnMove.setOnClickListener { // 버튼 클릭시 할 행동
//            startActivity(secondIntent)  // 화면 전환하기
//        }
//    }
//} */
//
