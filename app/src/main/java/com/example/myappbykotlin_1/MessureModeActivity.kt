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
