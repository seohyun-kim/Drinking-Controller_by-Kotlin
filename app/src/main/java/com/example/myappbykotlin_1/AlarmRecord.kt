package com.example.myappbykotlin_1

import android.app.Activity
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.*
import androidx.appcompat.app.AppCompatActivity
import android.util.Log
import android.view.*
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myappbykotlin_1.databinding.ActivityAlarmRecordBinding
import kotlinx.android.synthetic.main.activity_alarm_mode.*
import java.time.LocalDateTime
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts


import kotlinx.android.synthetic.main.activity_alarm_record.*
import kotlinx.android.synthetic.main.activity_bluetooth.*
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.*
import kotlin.collections.ArrayList


private var getData: Double? = null

private const val SELECT_DEVICE_REQUEST_CODE = 0
private var mmBuffer: ByteArray = ByteArray(1024)
private var cumDataReceived: Double = 0.0; //블루투스 수신한 누적량 데이터 변수에 저장 (초기 0)
private var cupData: Double = 0.0

private var recordList = ArrayList<String>();// 기록 값이 들어갈 동적 배열


private var currentData = 0.0 // 현재 마신 양 (누적X)
private var cnt = 0; //회차 확인용
private var goalData: Double = 100.00 //default
private var priorTime = System.currentTimeMillis()
private var first: Boolean = true //처음인지

private var data: MutableList<ListData> = mutableListOf()
private var adapter = AlarmRecord.CustomAdapter()
private var listId: Int = 1

private var pushValue: Boolean = false //default
// notification 설정
private var NOTIFICATION_ID = 500;
private var channelID = "Warning";
private var channelName = "Warning_";
private var channelDiscription = "Warning__"

// notification 설정 (속도경고)
private var NOTIFICATION_ID2 = 600;
private var channelID2 = "speed warning";
private var channelName2 = "speed warning_";
private var channelDiscription2 = "speed warning__"
private var alarm_flag='1'
data class ListData(var id: Int, var time: String,  var title: String) {}



class AlarmRecord : AppCompatActivity() {

    // 메뉴 생성
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.bt_option, menu)
        return true
    }
    // 메뉴 리스너
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle item selection
        return when (item.itemId) {
            R.id.bluetooth -> {
                val btIntent = Intent(this, bluetooth::class.java)
                startActivityForResult(btIntent, 100);
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
    // 되돌아오기
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            when (requestCode) {
                100 -> {
                    //val address = data!!.getStringExtra("btMacAddress").toString()
                    if (data!!.hasExtra("btMacAddress")) {
                        //val address = data!!.getStringExtra("btMacAddress").toString()
                        val address =  data!!.getParcelableExtra<BluetoothDevice>("btMacAddress")
                        Log.d("bluetoothDeviceAddress", address.toString())
                        ConnectThread(address, bluetoothAdapter).run()
                    } else {
                        Log.d("bluetoothDeviceAddress", "not thing")
                    }
                }
            }
        }
    }

    private var bluetoothAdapter: BluetoothAdapter = BluetoothAdapter.getDefaultAdapter()




    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_alarm_record)

        //뷰 바인딩
        val binding = ActivityAlarmRecordBinding.inflate(layoutInflater) //뷰 바인딩 사용 준비
        setContentView(binding.root)


        Log.d("intent", intent.toString())
        if (intent.hasExtra("goalValue")) {
            goalData = intent.getStringExtra("goalValue")!!.toDouble()
            Log.d("goalData", "goalData $goalData")
            binding.goalText.text = intent.getStringExtra("goalValue") + "ml"

            // 여기다가
            // write()
           // (bt_service as MyBluetoothService.ConnectedThread).write(goalData.toString().toByteArray())

        } else {
            Toast.makeText(this, "전달된 이름이 없습니다", Toast.LENGTH_SHORT).show()
            Log.d("goalData", "goalData 안 들어왔음!!")
        }


        if (intent.hasExtra("push")) {
            pushValue = intent.getBooleanExtra("push", false)
            Log.d("push", pushValue.toString())
        } else {
            Log.d("push", "푸시옵션 안 들어왔음!!")
        }


        RecyclerView.layoutManager = LinearLayoutManager(this)



        //actionbar
        val actionbar = supportActionBar
        //set actionbar title
        actionbar!!.title = "Alarm Mode Record"
        //set back button
        actionbar.setDisplayHomeAsUpEnabled(true)
        actionbar.setDisplayHomeAsUpEnabled(true)

//        binding.sendBtn.setOnClickListener {
//            //아두이노로 goal전송
//            if(bt_service != null){
//                (bt_service as MyBluetoothService.ConnectedThread).write(goalData.toString().toByteArray())
//                Toast.makeText(this, "기기로 목표값이 전송되었습니다.", Toast.LENGTH_SHORT).show()
//            }
//            else{
//                Toast.makeText(this, "먼저 기기와 연결해주세요.", Toast.LENGTH_SHORT).show()
//            }
//
//        }

        //저장하기 버튼 클릭 시
        binding.saveBtn.setOnClickListener {
            //내부저장소 이용


            /////////// 날짜 test

//            val curTime = System.currentTimeMillis()// 현재시간을 가져오기 (시간차 계산)
//            val date = Date(curTime) // 현재 시간을 Date 타입으로 변환
//            val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale("ko", "KR")) // 날짜, 시간을 가져오고 싶은 형태 선언
//            val nowTime = dateFormat.format(date)   // 현재 시간을 dateFormat 에 선언한 형태의 String 으로 변환
            val curTime = System.currentTimeMillis()
            val now = Date(curTime)
            val sharedPreference = getSharedPreferences("test", 0);
            val editor = sharedPreference.edit();
            //데이터 넣음(key=> 날짜, value==>오늘 마신량)

            editor.putString("$now", cumDataReceived.toString());
            editor.apply();

            //내부저장소 전체 출력
            val allEntries: Map<String, *> = sharedPreference.getAll()
            for ((key, value) in allEntries) {
                Log.d("entire values", key + ": " + value.toString())
            }
            //데이터 삭제
            //   editor.clear()
            //   editor.apply()

            //토스트
            var t1 = Toast.makeText(this, "저장되었습니다.", Toast.LENGTH_SHORT)
            t1.show()
            (bt_service as MyBluetoothService.ConnectedThread).write("n".toByteArray())
            (bt_service as MyBluetoothService.ConnectedThread).cancel()
            //값 초기화
            cumDataReceived= 0.0;
            cupData =0.0
            currentData = 0.0 // 현재 마신 양 (누적X)
            recordList.clear()
            data.clear()
            adapter.dataSet = data
            RecyclerView.adapter = adapter
            listId = 1



            //홈화면으로 이동
            val homeIntent = Intent(this, MainActivity::class.java)
            startActivity(homeIntent)

        }

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
                textView.text = "[${listData.id}회차] ${listData.time} : ${listData.title}ml"
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

//    data class ListData(var id: Int, var time: String, var title: String) {}



    //Notify
    private fun createNotificationChannel(
        channelID: String,
        channelName: String,
        channelDiscription: String
    ) {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = channelName
            val descriptionText = channelDiscription
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(channelID, name, importance).apply {
                description = descriptionText
            }
            // Register the channel with the system
            val notificationManager: NotificationManager =
                getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }




    class MyBluetoothService(
        // handler that gets info from Bluetooth service
        private val handler: Handler
    ) {
        inner class ConnectedThread(private val mmSocket: BluetoothSocket) : Thread() {

            private val mmInStream: InputStream = mmSocket.inputStream
            private val mmOutStream: OutputStream = mmSocket.outputStream
            //    private var mmBuffer: ByteArray = ByteArray(1024) // mmBuffer store for the stream

            override fun run() {
                var numBytes: Int // bytes returned from read()

                // Keep listening to the InputStream until an exception occurs.
                while (true) {
                    Log.d("bluetoothThread", "do")
                    sleep(1000)
                    // Read from the InputStream.
                    numBytes = try {
                        mmInStream.read(mmBuffer)
                    } catch (e: IOException) {
                        Log.d(ContentValues.TAG, "Input stream was disconnected", e)
                        break
                    }

                    // Send the obtained bytes to the UI activity.
                    val readMsg = handler.obtainMessage(
                        MESSAGE_READ, numBytes, -1,
                        mmBuffer
                    )
                    readMsg.sendToTarget()
                }
            }

            // Call this from the main activity to send data to the remote device.
            fun write(bytes: ByteArray) {
                try {
                    mmOutStream.write(bytes)
                } catch (e: IOException) {
                    Log.e(TAG, "Error occurred when sending data", e)

                    // Send a failure message back to the activity.
                    val writeErrorMsg = handler.obtainMessage(MESSAGE_TOAST)
                    val bundle = Bundle().apply {
                        putString("toast", "Couldn't send data to the other device")
                    }
                    writeErrorMsg.data = bundle
                    handler.sendMessage(writeErrorMsg)
                    return
                }

                // Share the sent message with the UI activity.
                val writtenMsg = handler.obtainMessage(
                    MESSAGE_WRITE, -1, -1, mmBuffer)
                writtenMsg.sendToTarget()
            }

            // Call this method from the main activity to shut down the connection.
            fun cancel() {
                try {
                    mmSocket.close()
                } catch (e: IOException) {
                    Log.e(ContentValues.TAG, "Could not close the connect socket", e)
                }
            }
        }
    }

    var bt_service: Thread? = null
    private inner class ConnectThread(
        device: BluetoothDevice?,
        bluetoothAdapter: BluetoothAdapter?
    ) : Thread() {
        var bluetoothAdapter: BluetoothAdapter? = BluetoothAdapter.getDefaultAdapter()

        private val mmSocket: BluetoothSocket? by lazy(LazyThreadSafetyMode.NONE) {
            var MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
            device!!.createRfcommSocketToServiceRecord(MY_UUID)
        }

        public override fun run() {
            bluetoothAdapter?.cancelDiscovery()

            mmSocket?.let { socket ->
                socket.connect()

                manageMyConnectedSocket(socket)

            }
        }

        // Closes the client socket and causes the thread to finish.
        fun cancel() {
            try {
                mmSocket?.close()
            } catch (e: IOException) {
                Log.e(ContentValues.TAG, "Could not close the client socket", e)
            }
        }

        private fun manageMyConnectedSocket(socket: BluetoothSocket) {

            bt_service = MyBluetoothService(handler).ConnectedThread(socket)
            (bt_service as MyBluetoothService.ConnectedThread).start()
            (bt_service as MyBluetoothService.ConnectedThread).write("a".toByteArray())
            (bt_service as MyBluetoothService.ConnectedThread).write(goalData.toString().toByteArray())
            Log.d("data", "write")
        }
    }

    val handler = object : Handler(Looper.getMainLooper()) {

        override fun handleMessage(msg: Message) {
            Log.d("do", "do")
            Log.d("msg", msg.what.toString())
            when (msg.what) {
                MESSAGE_READ -> {
                    Log.d("readMsg", msg.toString())
                    Log.d("readMsg", String(msg.obj as ByteArray, charset("UTF-8")))
                    val test = String(msg.obj as ByteArray, charset("UTF-8"))
                    var list_one = java.util.ArrayList<String>()
                    var a:String=""
                    val mutableIterator = test.iterator()
                    for (item in mutableIterator)
                    {
                        if(item>='0' && item <='9' || item == '.')
                        {
                            a += item
                        }
                    }

                    for(i in 0..1023)
                    {
                        mmBuffer.set(i,0)
                    }
                    currentData = a.toDouble();
                    Log.d("blblbl", currentData.toString())


                    val curTime = System.currentTimeMillis()// 현재시간을 가져오기 (시간차 계산)
                    val date = Date(curTime) // 현재 시간을 Date 타입으로 변환
                    val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale("ko", "KR")) // 날짜, 시간을 가져오고 싶은 형태 선언
                    val nowTime = dateFormat.format(date)   // 현재 시간을 dateFormat 에 선언한 형태의 String 으로 변환


                    var diffTime = (curTime - priorTime) / 1000 //이전 시간과 초차이
                    Log.d("diffTime", diffTime.toString())

                    cumDataReceived += currentData
                    cupData = cumDataReceived / 50


                    //양으로
                        if (cumDataReceived > goalData) {
                            runOnUiThread {
                                msgText.text = "목표량 초과! 멈춰!!!"
                                msgText.setTextColor(Color.parseColor("#FF0000"))
                                imageView5.setColorFilter(Color.parseColor("#FF0000"))
                                cumData.setTextColor(Color.parseColor("#FFFFFF"))
                                cupText.setTextColor(Color.parseColor("#FFFFFF"))
                            }


                      }
                        //                        else if ((goalData-cumDataReceived)/ goalData > 0.9) {
//                            runOnUiThread {
//                                msgText.text = "어? 어?! 그만 그만!!"
//                                msgText.setTextColor(Color.parseColor("#FF1111"))
//                                imageView5.setImageResource(R.drawable.circle_r)
//
//                            }

//                        }
                else if ((goalData-cumDataReceived)/ goalData > 0.6) {
                            runOnUiThread {
                                msgText.text = "목표량에 다다르고 있어요!"
                                msgText.setTextColor(Color.parseColor("#FF7F00"))
                                imageView5.setImageResource(R.drawable.circle)
                            }
                        } else if ((goalData-cumDataReceived)/ goalData > 0.3) {
                            runOnUiThread {
                                msgText.text = "아직까지는 괜찮아요."
                                msgText.setTextColor(Color.parseColor("#0067A3"))
                                imageView5.setImageResource(R.drawable.circle_g)
                            }
                        } else {
                            runOnUiThread {
                                msgText.text = "즐거운 술자리에요~"
                                msgText.setTextColor(Color.parseColor("#008000"))
                            }
                        }


                    cnt += 1
                    recordList.add(cnt.toString() + "회차 : " + currentData.toString() + " ml \n")
                    Log.d("recordList", recordList.toString())

                    runOnUiThread {
                       cumData.text = cumDataReceived.toString() + " ml "
                        cupText.text = " = " + cupData.toString() + " 잔"
                    }


                    var builder = NotificationCompat.Builder(this@AlarmRecord, channelID)
                        .setSmallIcon(R.drawable.mainpage_beer)
                        .setContentTitle("Stop!!! 그만 마시세요!")
                        .setContentText(cumDataReceived.toString())
                        .setPriority(NotificationCompat.PRIORITY_HIGH);


                    var builder2 = NotificationCompat.Builder(this@AlarmRecord, channelID)
                        .setSmallIcon(R.drawable.mainpage_beer)
                        .setContentTitle("속도가 빨라요! 천천히 마시세요!")
                        .setPriority(NotificationCompat.PRIORITY_HIGH);

                    createNotificationChannel(channelID, channelName, channelDiscription)
                    createNotificationChannel(channelID2, channelName2, channelDiscription2)

                    var overNotify: Boolean = false
                    if (pushValue == true && cumDataReceived > goalData - 50) {
                        with(NotificationManagerCompat.from(this@AlarmRecord)) {
                            notify(NOTIFICATION_ID, builder.build());
                        }
                        overNotify = true;
                    }

                    if (pushValue == true && first == false && diffTime < 10 && overNotify == false) { //3분 이내에 다시마시면 (test10초)
                        with(NotificationManagerCompat.from(this@AlarmRecord)) {
                            notify(NOTIFICATION_ID2, builder2.build());
                        }
                    }

                    data.add(ListData(listId, nowTime, currentData.toString()))

                    adapter.dataSet = data
                    RecyclerView.scrollToPosition(data.size - 1)
                    RecyclerView.adapter = adapter
                    listId += 1

                    priorTime = curTime;
                    first = false
                }
            }
        }
    }

}