package com.example.myappbykotlin_1

import android.app.Activity
import android.view.LayoutInflater
import android.view.ViewGroup

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.content.ContentValues
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.util.Log
import android.view.*
import android.widget.TextView
import android.widget.Toast

import com.example.myappbykotlin_1.databinding.ActivityMessureModeBinding
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.LinearLayoutManager

import androidx.recyclerview.widget.RecyclerView
import com.example.myappbykotlin_1.R
import kotlinx.android.synthetic.main.activity_alarm_record.*
import java.time.LocalDateTime
import kotlinx.android.synthetic.main.activity_messure_mode.*
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.collections.ArrayList


private const val SELECT_DEVICE_REQUEST_CODE = 0
private var mmBuffer: ByteArray = ByteArray(1024)
private var cumDataReceived: Double = 0.0; //블루투스 수신한 누적량 데이터 변수에 저장 (초기 0)
private var cupData: Double = 0.0

private var recordList = ArrayList<String>();// 기록 값이 들어갈 동적 배열
private var currentData = 0.0 // 현재 마신 양 (누적X)
private var cnt = 0; //회차 확인용
private var data: MutableList<LListData> = mutableListOf()
private var adapter = MessureModeActivity.CCustomAdapter()
private var listId: Int = 1

data class LListData( var iid: Int,  var ttime: String, var ttitle: String) {}

class MessureModeActivity : AppCompatActivity() {

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


        MessureRecyclerView.layoutManager = LinearLayoutManager(this)



        //뷰 바인딩

        binding.resultBtn.setOnClickListener{

            if(bt_service !=null) //연결안됐는데 결과보기 누르는경우
            {
                var CumDataReceived_=cumDataReceived.toString()
                var CupData_=cupData.toString()


                (bt_service as MessureModeActivity.MyBluetoothService.ConnectedThread).write("n".toByteArray())
                (bt_service as MyBluetoothService.ConnectedThread).cancel()
                //값 초기화
                cumDataReceived= 0.0;
                cupData =0.0
                currentData = 0.0 // 현재 마신 양 (누적X)
                recordList.clear()
                data.clear()
                adapter.dataSet = data
                MessureRecyclerView.adapter = adapter
                listId = 1


                val intent = Intent(this, MessureRecord::class.java)
                intent.putExtra("MessureValue",CumDataReceived_);
                intent.putExtra("MessureCupValue",CupData_);
                startActivity(intent);
                Log.d("Btn", "result Btn is clicked! method = Log.d")
            }
            else{
                Toast.makeText(this, "기기와 연결해 주세요.", Toast.LENGTH_SHORT).show()
            }

        }
    }



    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    class CCustomAdapter() :
        RecyclerView.Adapter<CCustomAdapter.ViewHolder>() {
        var dataSet = mutableListOf<LListData>()

        /**
         * Provide a reference to the type of views that you are using
         * (custom ViewHolder).
         */
        class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val textView: TextView

            init {
                // Define click listener for the ViewHolder's View.
                textView = view.findViewById(R.id.ttextView)
            }

            fun setText(listData: LListData) {
                Log.d("blblblsetText", listData.toString())
                textView.text = "[${listData.iid}회차] ${listData.ttime} : ${listData.ttitle}ml"
            }

        }

        override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
            // Create a new view, which defines the UI of the list item
            val view = LayoutInflater.from(viewGroup.context)
                .inflate(R.layout.item_recycler, viewGroup, false)

            return ViewHolder(view)
            //return view
        }

        // Replace the contents of a view (invoked by the layout manager)
        override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {

            // Get element from your dataset at this position and replace the
            // contents of the view with that element
            var data = dataSet[position]
           // viewHolder.setText(data)
            viewHolder.setText(data)
        }

        // Return the size of your dataset (invoked by the layout manager)
        override fun getItemCount() = dataSet.size
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
                    Log.e(ContentValues.TAG, "Error occurred when sending data", e)

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
                    MESSAGE_WRITE, -1, -1, mmBuffer
                )
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
            (bt_service as MyBluetoothService.ConnectedThread).write("m".toByteArray())
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


                    cnt += 1
                    recordList.add(cnt.toString() + "회차 : " + currentData.toString() + " ml \n")
                    Log.d("recordList", recordList.toString())
                    cumDataReceived += currentData
                    cupData = cumDataReceived / 50

                    runOnUiThread {
                        ccumData.text = cumDataReceived.toString() + " ml "
                        ccupText.text = " = " + cupData.toString() + " 잔"
                    }


                    data.add(LListData(listId, nowTime, currentData.toString()))
                    Log.d("blblbl--385:data",data.toString())
                    adapter.dataSet = data

                        MessureRecyclerView.scrollToPosition(data.size - 1)
                        MessureRecyclerView.adapter = adapter


                    listId += 1


                }
            }
        }
    }


}


