package com.example.myappbykotlin_1
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.example.myappbykotlin_1.databinding.ActivityCalendarBinding
import java.time.LocalDate
import java.util.Locale
import java.text.SimpleDateFormat
import java.util.Date
import java.util.*
import android.annotation.SuppressLint
import android.graphics.Color
import android.view.View
import java.io.FileInputStream
import java.io.FileOutputStream
import kotlinx.android.synthetic.main.activity_calendar.*
import android.widget.Toast
import com.example.myappbykotlin_1.databinding.ActivityMessureRecordBinding


class calendar: AppCompatActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    var fname: String = ""
    var str: String = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_calendar)
        val actionbar = supportActionBar
        //set actionbar title
        actionbar!!.title = "calendar"
        //set back button
        actionbar.setDisplayHomeAsUpEnabled(true)
        actionbar.setDisplayHomeAsUpEnabled(true)
        val binding = ActivityCalendarBinding.inflate(layoutInflater) //뷰 바인딩 사용 준비
        setContentView(binding.root) //화면 안의 버튼 사용 가능

        calendarView.setOnDateChangeListener { view, year, month, dayOfMonth ->
// 달력 날짜가 선택되면
          //  home_Btn.visibility=View.INVISIBLE
            diaryTextView.visibility = View.VISIBLE // 해당 날짜가 뜨는 textView가 Visible
            // save_Btn.visibility = View.VISIBLE // 저장 버튼이 Visible
            contextEditText.visibility = View.VISIBLE // EditText가 Visible
            textView2.visibility = View.INVISIBLE // 저장된 일기 textView가 Invisible
            //  cha_Btn.visibility = View.INVISIBLE // 수정 Button이 Invisible
            //  del_Btn.visibility = View.INVISIBLE // 삭제 Button이 Invisible

            diaryTextView.text = String.format("%d / %02d / %02d", year, month + 1, dayOfMonth)
// 날짜를 보여주는 텍스트에 해당 날짜를 넣는다.
            contextEditText.setText("") // EditText에 공백값 넣기


            checkedDay(year, month, dayOfMonth) // checkedDay 메소드 호출
            
        }


        binding.homeBtn.setOnClickListener{
            val nextIntent = Intent(this,MainActivity::class.java)
            startActivity(nextIntent)
            Log.d("Btn", "Messure Record Btn is clicked! method = Log.d")
        } //binding 변수로 뷰에서 만든 버튼에 접근 가능


    }
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
    fun checkedDay(cYear: Int, cMonth: Int, cDay: Int) {
        val sharedPreference = getSharedPreferences("test", 0);
        val editor = sharedPreference.edit();
        val cMonth = String.format("%02d", cMonth + 1);
        val cDay = String.format("%02d", cDay);

        //전체데이터 평균계산
        val allEntries: Map<String, *> = sharedPreference.getAll()
        var sum:Double =0.0;
        var cnt:Int =0;
        for ((key, value) in allEntries) {
            //val value_:Double =value.toString().toDouble()
            sum = sum +value.toString().toDouble()
            Log.d("blblbl entire values", key + ": " + value.toString())
            cnt++
        }
        val average = sum/cnt //전체데이터평균
        Log.d(" blblblaverage", average.toString());


        fname = "" + cYear + "-" + (cMonth) + "-" + cDay
        Log.d(" data", "value:" + fname);

// 저장할 파일 이름 설정. Ex) 2019-01-20.txt
//        var fis: FileInputStream? = null // FileStream fis 변수 설정
//
//        try {
//            fis = openFileInput(fname) // fname 파일 오픈!!
//
//            val fileData = ByteArray(fis.available()) // fileData에 파이트 형식
////으로 저장
//            fis.read(fileData) // fileData를 읽음
//            fis.close()
//
//            str = String(fileData) // str 변수에 fileData를 저장

        contextEditText.visibility = View.INVISIBLE
       // home_Btn.visibility=View.INVISIBLE
        textView2.visibility = View.VISIBLE
        val value = sharedPreference.getString(fname, "데이터 없음");
        if(value == "데이터 없음")
        {
            textView2.text = "${value}"
            textView2.setTextColor(Color.parseColor("#000000"))
        }
        else {
            textView2.text = "${value + " ml"}" // textView에 str 출력

            if(value.toString().toDouble() > average) //평균보다 크면 빨간색
            {
               textView2.setTextColor(Color.parseColor("#FF0000"))
            }
            else{
                textView2.setTextColor(Color.parseColor("#000000"))
            }
            Log.d("shPrtext",value.toString())
        }
    }
}



