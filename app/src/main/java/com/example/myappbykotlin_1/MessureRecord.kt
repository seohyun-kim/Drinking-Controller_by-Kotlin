package com.example.myappbykotlin_1
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.example.myappbykotlin_1.databinding.ActivityMessureRecordBinding
import java.time.LocalDate
import java.util.Locale
import java.text.SimpleDateFormat
import java.util.Date
import java.util.*
import android.widget.Toast
import java.time.LocalTime

class MessureRecord: AppCompatActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_messure_record)

        //actionbar
        val actionbar = supportActionBar
        //set actionbar title
        actionbar!!.title = "Messure Record"
        //set back button
        actionbar.setDisplayHomeAsUpEnabled(true)
        actionbar.setDisplayHomeAsUpEnabled(true)

       // val curTime = System.currentTimeMillis()


        // 현재시간을 가져오기
        val long_now = System.currentTimeMillis()
        // 현재 시간을 Date 타입으로 변환
        val t_date = Date(long_now)
        // 날짜, 시간을 가져오고 싶은 형태 선언
        val t_dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale("ko", "KR"))
        // 현재 시간을 dateFormat 에 선언한 형태의 String 으로 변환
        val str_date = t_dateFormat.format(t_date)
        val now = str_date
        //뷰 바인딩
        val binding=ActivityMessureRecordBinding.inflate(layoutInflater) //뷰 바인딩 사용 준비
        setContentView(binding.root) //화면 안의 버튼 사용 가능
        var drinkcapacity :Float= 150F;
        var ml_drinkcapacity :Float= drinkcapacity/50;

        if (intent.hasExtra("MessureValue") && intent.hasExtra("MessureCupValue")) {
            drinkcapacity = intent.getStringExtra("MessureValue")!!.toFloat()
            ml_drinkcapacity=intent.getStringExtra("MessureCupValue")!!.toFloat()
            Log.d("Messurevalue", "Mesuure Data $drinkcapacity")
            binding.drinkcapacitytext.text =drinkcapacity.toString()+ " ml "
            binding.cupdata.text="= " +ml_drinkcapacity.toString()+" 잔 "
        }
        else {
            Toast.makeText(this, "전달된 이름이 없습니다", Toast.LENGTH_SHORT).show()
            Log.d("goalData", "goalData 안 들어왔음!!")
        }
        //binding.drinkcapacitytext.text= drinkcapacity.toString()+" ml " + ml_drinkcapacity.toString()+" 잔 ";

        binding.homebackbtn.setOnClickListener{
            val nextIntent = Intent(this,MainActivity::class.java)
            startActivity(nextIntent)
            Log.d("Btn", "Messure Record Btn is clicked! method = Log.d")
        } //binding 변수로 뷰에서 만든 버튼에 접근 가능
        //임시로 값 저장
        binding.savebtn.setOnClickListener{
            //내부저장소 이용
            val sharedPreference = getSharedPreferences("test", 0);
            val editor = sharedPreference.edit();
            //데이터 넣음(key=> 날짜, value==>주량)
//            editor.putString("2021-08-15", 500F.toString());
//            editor.putString("2021-08-04", 200F.toString());
//            editor.putString("2021-08-17", 183F.toString());
//            editor.putString("2021-08-10", 240F.toString());
//            editor.putString("2021-07-29", 73F.toString());
            editor.putString("$now", drinkcapacity.toString());
            editor.apply();
            Log.d("blblbldrinkcapacity", drinkcapacity.toString())
            //없는 데이터 출력하면 "데이터 없음"이라고 뜸
            val value1 = sharedPreference.getString("2021-07-24", "데이터 없음");
            val value2= sharedPreference.getString("2021-08-02","데이터 없음");
            Log.d("no data","value"+value1);
            Log.d("current data","value"+value2);
            //내부저장소 전체 출력
            val allEntries: Map<String, *> = sharedPreference.getAll()
            for ((key, value) in allEntries) {
                Log.d("entire values", key + ": " + value.toString())
            }
            //데이터 삭제
//            editor.clear()
//            editor.apply()

            // 저장확인 알림
            var t1 = Toast.makeText(this, "저장되었습니다.", Toast.LENGTH_SHORT)
            t1.show()

        }
        binding.calendarbtn.setOnClickListener{
            val nextIntent = Intent(this,calendar::class.java)
            startActivity(nextIntent)
            Log.d("Btn", "calendarBtn is clicked! method = Log.d")
        }
    }
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}
