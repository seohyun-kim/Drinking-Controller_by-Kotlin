package com.example.myappbykotlin_1

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toolbar
import com.example.myappbykotlin_1.databinding.ActivityAlarmModeBinding
import com.example.myappbykotlin_1.databinding.ActivityMessureRecordBinding
class MessureModeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_messure_record)

        //actionbar
        val actionbar = supportActionBar
        //set actionbar title
        actionbar!!.title = "Messure Mode"
        //set back button
        actionbar.setDisplayHomeAsUpEnabled(true)
        actionbar.setDisplayHomeAsUpEnabled(true)

        //뷰 바인딩
        val binding=ActivityMessureRecordBinding.inflate(layoutInflater) //뷰 바인딩 사용 준비
        setContentView(binding.root) //화면 안의 버튼 사용 가능
        binding.homebackbtn.setOnClickListener{
            val nextIntent = Intent(this,MainActivity::class.java)
            startActivity(nextIntent)
            Log.d("Btn", "Alarm Record Btn is clicked! method = Log.d")
        } //binding 변수로 뷰에서 만든 버튼에 접근 가능
    }
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}
