package com.example.myappbykotlin_1

import android.bluetooth.*
import android.bluetooth.le.BluetoothLeScanner
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.content.BroadcastReceiver
import android.content.ContentValues.TAG
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.Handler
import android.util.Log
import androidx.annotation.RequiresApi

import com.example.myappbykotlin_1.databinding.ActivityMainBinding //안드로이드가 자동으로 변환함
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.util.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main) // res/layout 디렉토리에 있는 activity_main.xml 파일을 사용한다

        //뷰 바인딩
        val binding= ActivityMainBinding.inflate(layoutInflater) //뷰 바인딩 사용 준비
        setContentView(binding.root) //화면 안의 버튼 사용 가능

        //측정모드 버튼 클릭 시 화면 전환
        binding.MessureModeBtn.setOnClickListener{
            val nextIntent = Intent(this, MessureModeActivity::class.java)
            startActivity(nextIntent)
            Log.d("MainBtn", "Messure Mode Btn is clicked! method = Log.d")
        } //binding 변수로 뷰에서 만든 버튼에 접근 가능

        //측정모드 버튼 클릭 시 화면 전환
        binding.AlarmModeBtn.setOnClickListener{
            val nextIntent = Intent(this, AlarmModeActivity::class.java)
            startActivity(nextIntent)
            Log.d("MainBtn", "Alarm Mode Btn is clicked! method = Log.d")
        } //binding 변수로 뷰에서 만든 버튼에 접근 가능

        binding.btnBluetooth.setOnClickListener {
            val nextIntent = Intent(this, bluetooth::class.java)
            startActivity(nextIntent)
            Log.d("MainBtn", "Bluetooth Mode Btn is clicked! method = Log.d")
        }
    }

}




