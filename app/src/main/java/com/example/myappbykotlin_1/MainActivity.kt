package com.example.myappbykotlin_1

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log

import com.example.myappbykotlin_1.databinding.ActivityMainBinding //안드로이드가 자동으로 변환함

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        /* 뷰 바인딩
        val binding= ActivityMainBinding.inflate(layoutInflater) //뷰 바인딩 사용 준비
        setContentView(binding.root) //화면 안의 버튼 사용 가능
        binding.btnSay.setOnClickListener{
            binding.textSay.text = "Button Clicked!"
        } //binding 변수로 뷰에서 만든 버튼에 접근 가능
         */

        // 화면을 그려주는 함수 setContentView
        setContentView(R.layout.activity_main) // res/layout 디렉토리에 있는 activity_main.xml 파일을 사용한다
        Log.d("BasicSyntax", "로그 출력 method = Log.d")

    }
}