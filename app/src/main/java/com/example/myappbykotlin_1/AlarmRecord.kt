package com.example.myappbykotlin_1

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myappbykotlin_1.databinding.ActivityAlarmRecordBinding
import java.util.Random  //임시로 랜덤함수 쓰기위해 import

import kotlinx.android.synthetic.main.activity_alarm_record.*

class AlarmRecord : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_alarm_record)

        //뷰 바인딩
        val binding= ActivityAlarmRecordBinding.inflate(layoutInflater) //뷰 바인딩 사용 준비
        setContentView(binding.root)

        var goalData:Double = 100.00 //default
        if (intent.hasExtra("goalValue")) {
            goalData =intent.getStringExtra("goalValue")!!.toDouble()
            Log.d("goalData", "goalData $goalData")
            binding.goalText.text =intent.getStringExtra("goalValue") + "ml"

        } else {
            //Toast.makeText(this, "전달된 이름이 없습니다", Toast.LENGTH_SHORT).show()
            // 토스트 안됨 ㅠ
            Log.d("goalData", "goalData 안 들어왔음!!")
        }

        var cumDataReceived :Float = 120F; //블루투스 수신한 누적량 데이터 변수에 저장 (임시로 100)
        var cupData :Float = cumDataReceived/50

        var recordList = ArrayList<String>();// 기록 값이 들어갈 동적 배열

        val rand=Random()
        var currentData =0 // 현재 마신 양 (누적X)
        var cnt=0; //회차 확인용

        var btnClicked =false;//자세히 보기 버튼 클릭 여부

// 변수 설정
        var data:MutableList<ListData> = mutableListOf()
        var adapter = CustomAdapter()
        var listId: Int = 1
        RecyclerView.layoutManager = LinearLayoutManager(this)

        //임시 버튼 (나중엔 블루투스 값 들어올때마다 자동으로 새로고침 되도록)
        //버튼 클릭 시 데이터 새로 입력
        binding.updateBtn.setOnClickListener{
            binding.cumData.text = cumDataReceived.toString() + " ml "
            binding.cupText.text= " = " + cupData.toString()+" 잔"

            if (cumDataReceived > goalData){
                binding.msgText.text = "목표량 초과! 멈춰!!!"
                binding.msgText.setTextColor(Color.parseColor("#FF0000"))
            }
            else if (cumDataReceived > goalData -50)
            {
                binding.msgText.text = "어? 어?! 그만 그만!!"
                binding.msgText.setTextColor(Color.parseColor("#FF1111"))
            }
            else if (cumDataReceived > goalData -130)
            {
                binding.msgText.text = "목표량에 다다르고 있어요!"
                binding.msgText.setTextColor(Color.parseColor("#FF7F00"))
            }
            else if (cumDataReceived > goalData -200)
            {
                binding.msgText.text = "아직까지는 괜찮아요."
                binding.msgText.setTextColor(Color.parseColor("#0067A3"))
            }
            else{
                binding.msgText.text = "술이 달다~"
                binding.msgText.setTextColor(Color.parseColor("#008000"))
            }

            //test (버튼 클릭시마다 랜덤으로 추가되도록)
            currentData= rand.nextInt(30) +20 //랜덤으로 현재 마신 양 넣음
            cnt+=1
            recordList.add(cnt.toString() + "회차 : "+currentData.toString()+" ml \n")
            Log.d("recordList", recordList.toString())
            cumDataReceived+=currentData
            cupData = cumDataReceived/50

// 값지정
            data.add(ListData(listId, currentData.toString()))
            adapter.dataSet = data
            RecyclerView.scrollToPosition(data.size - 1)
            RecyclerView.adapter = adapter
            listId += 1
// Seohyun
//            if (btnClicked === true)
//            {
//                binding.recordView.text = recordList.toString()
//            }

        }  //소주 1잔 50ml


        binding.recordShowBtn.setOnClickListener{
// Seohyun
//            if (btnClicked === false)
//            {
//                btnClicked=true;
//                binding.recordView.text = recordList.toString()
//            }
//            else{
//                btnClicked=false;
//                binding.recordView.text = ""
//            }

        }
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



    class CustomAdapter() :
        RecyclerView.Adapter<CustomAdapter.ViewHolder>() {
        var dataSet = mutableListOf<ListData>()

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
            fun setText(listData: ListData) {
                textView.text = "${listData.id}회차: ${listData.title}ml"
            }

        }

        // Create new views (invoked by the layout manager)
        override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
            // Create a new view, which defines the UI of the list item
            val view = LayoutInflater.from(viewGroup.context)
                .inflate(R.layout.text_row_item, viewGroup, false)

            return ViewHolder(view)
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

    data class ListData(var id: Int, var title: String) {}
}