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

//        save_Btn.setOnClickListener { // 저장 Button이 클릭되면
//            saveDiary(fname) // saveDiary 메소드 호출
//            //toast(fname + "데이터를 저장했습니다.") // 토스트 메세지
//            str = contextEditText.getText().toString() // str 변수에 edittext내용을 toString
////형으로 저장
//            textView2.text = "${str}" // textView에 str 출력
//            save_Btn.visibility = View.INVISIBLE
//            cha_Btn.visibility = View.VISIBLE
//            del_Btn.visibility = View.VISIBLE
//            contextEditText.visibility = View.INVISIBLE
//            textView2.visibility = View.VISIBLE
//        }



            // save_Btn.visibility = View.INVISIBLE
            // cha_Btn.visibility = View.VISIBLE
            // del_Btn.visibility = View.VISIBLE

            // home_Btn.setOnClickListener { //홈 버튼 누를 시
            //  contextEditText.visibility = View.VISIBLE
            // textView2.visibility = View.INVISIBLE
            //  contextEditText.setText(str) // editText에 textView에 저장된
// 내용을 출력
            //save_Btn.visibility = View.VISIBLE
            // cha_Btn.visibility = View.INVISIBLE
            // del_Btn.visibility = View.INVISIBLE
            //  textView2.text = "${contextEditText.getText()}"
       // }


//            del_Btn.setOnClickListener {
//                textView2.visibility = View.INVISIBLE
//                contextEditText.setText("")
//                contextEditText.visibility = View.VISIBLE
//                save_Btn.visibility = View.VISIBLE
//                cha_Btn.visibility = View.INVISIBLE
//                del_Btn.visibility = View.INVISIBLE
//                removeDiary(fname)
//              //  toast(fname + "데이터를 삭제했습니다.")
//            }
//
//            if(textView2.getText() == ""){
//                textView2.visibility = View.INVISIBLE
//                diaryTextView.visibility = View.VISIBLE
//                save_Btn.visibility = View.VISIBLE
//                cha_Btn.visibility = View.INVISIBLE
//                del_Btn.visibility = View.INVISIBLE
//                contextEditText.visibility = View.VISIBLE
//            }
//
////        } catch (e: Exception) {
////            e.printStackTrace()
////        }
//    }

        //
//    @SuppressLint("WrongConstant")
//    fun saveDiary(readyDay: String) {
//        var fos: FileOutputStream? = null
//
//        try {
//            fos = openFileOutput(readyDay, MODE_NO_LOCALIZED_COLLATORS)
//            var content: String = contextEditText.getText().toString()
//            fos.write(content.toByteArray())
//            fos.close()
//
//        } catch (e: Exception) {
//            e.printStackTrace()
//        }
//
//    }
//
//    @SuppressLint("WrongConstant")
//    fun removeDiary(readyDay: String) {
//        var fos: FileOutputStream? = null
//
//        try {
//            fos = openFileOutput(readyDay, MODE_NO_LOCALIZED_COLLATORS)
//            var content: String = ""
//            fos.write(content.toByteArray())
//            fos.close()
//
//        } catch (e: Exception) {
//            e.printStackTrace()
//        }
//
//    }
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
        }
        else {
            textView2.text = "${value + " ml"}" // textView에 str 출력
            Log.d("shPrtext",value.toString())
        }
    }
}



