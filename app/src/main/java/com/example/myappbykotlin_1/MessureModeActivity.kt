package com.example.myappbykotlin_1

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toolbar
import com.example.myappbykotlin_1.databinding.ActivityMessureModeBinding
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myappbykotlin_1.R
import kotlinx.android.synthetic.main.activity_alarm_record.*
import java.time.LocalDateTime
import kotlinx.android.synthetic.main.activity_messure_mode.*
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.collections.ArrayList

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

        //뷰바인딩 사용
        val binding=ActivityMessureModeBinding.inflate(layoutInflater)
        setContentView(binding.root) //화면 안의 버튼 사용 가능

        var cumDataReceived :Float = 0F; //블루투스 수신한 누적량 데이터 변수에 저장 (초기 0)
        var cupData :Float = cumDataReceived/50
        var recordList = ArrayList<String>();// 기록 값이 들어갈 동적 배열
        val rand=Random()
        var currentData =0 // 현재 마신 양 (누적X)
        var cnt=0; //회차 확인용

        var data:MutableList<MessureModeActivity.ListData> = mutableListOf()
        var adapter = MessureModeActivity.CustomAdapter()
        var listId: Int = 1
        MessureRecyclerView.layoutManager = LinearLayoutManager(this)


        binding.resetBtn.setOnClickListener { //알람모드 코드 참고

            //DateTime
            val long_now = System.currentTimeMillis()
            // 현재 시간을 Date 타입으로 변환
            val t_date = Date(long_now)
            // 날짜, 시간을 가져오고 싶은 형태 선언
            val t_dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale("ko", "KR"))
            // 현재 시간을 dateFormat 에 선언한 형태의 String 으로 변환
            val str_date = t_dateFormat.format(t_date)


            //test (버튼 클릭시마다 랜덤으로 추가되도록)
            val currentData = rand.nextInt(30) + 20 //랜덤으로 현재 마신 양 넣음
            cnt += 1
            recordList.add(cnt.toString() + "회차 : " + currentData.toString() + " ml \n")
            Log.d("recordList", recordList.toString())
            cumDataReceived += currentData
            cupData = cumDataReceived / 50

            binding.cumData.text = cumDataReceived.toString() + " ml "
            binding.cupText.text = " = " + cupData.toString() + " 잔"

            data.add(MessureModeActivity.ListData(listId, str_date.toString(), currentData.toString()))
            adapter.dataSet = data
            MessureRecyclerView.scrollToPosition(data.size - 1)
            MessureRecyclerView.adapter = adapter
            listId += 1

        }
        //뷰 바인딩

        binding.resultBtn.setOnClickListener{
            var nextval=cumDataReceived.toString()
            val intent = Intent(this, MessureRecord::class.java)
            intent.putExtra("goalValue",nextval);
            startActivity(intent);
            Log.d("Btn", "result Btn is clicked! method = Log.d")
        } //binding 변수로 뷰에서 만든 버튼에 접근 가능
    }



    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    class CustomAdapter() :
        RecyclerView.Adapter<MessureModeActivity.CustomAdapter.ViewHolder>() {
        var dataSet = mutableListOf<MessureModeActivity.ListData>()

        /**
         * Provide a reference to the type of views that you are using
         * (custom ViewHolder).
         */
        class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val textView: TextView

            init {
                // Define click listener for the ViewHolder's View.
                textView = view.findViewById(R.id.textView)
            }

            fun setText(listData: MessureModeActivity.ListData) {
                textView.text = "[${listData.id}회차] ${listData.time} : ${listData.title}ml"
            }

        }

        override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): MessureModeActivity.CustomAdapter.ViewHolder {
            // Create a new view, which defines the UI of the list item
            val view = LayoutInflater.from(viewGroup.context)
                .inflate(R.layout.text_row_item, viewGroup, false)

            return MessureModeActivity.CustomAdapter.ViewHolder(view)
            //return view
        }

        // Replace the contents of a view (invoked by the layout manager)
        override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {

            // Get element from your dataset at this position and replace the
            // contents of the view with that element
            var data = dataSet.get(position)
            viewHolder.setText(data)
        }

        // Return the size of your dataset (invoked by the layout manager)
        override fun getItemCount() = dataSet.size
    }

    data class ListData(var id: Int, var time:String, var title: String) {}
}




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
