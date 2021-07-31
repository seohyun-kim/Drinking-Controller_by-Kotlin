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
    var bluetoothHeadset: BluetoothHeadset? = null
    private val profileListener = object : BluetoothProfile.ServiceListener {

        override fun onServiceConnected(profile: Int, proxy: BluetoothProfile) {
            if (profile == BluetoothProfile.HEADSET) {
                bluetoothHeadset = proxy as BluetoothHeadset
            }
        }

        override fun onServiceDisconnected(profile: Int) {
            if (profile == BluetoothProfile.HEADSET) {
                bluetoothHeadset = null
            }
        }
    }

    private var bluetoothAdapter: BluetoothAdapter = BluetoothAdapter.getDefaultAdapter()

    override fun onResume() {
        super.onResume()
        // Get the default adapter
        //var bluetoothAdapter: BluetoothAdapter? = BluetoothAdapter.getDefaultAdapter()
        if (bluetoothAdapter == null) {
            Log.d("bluetoothAdapter", "not Support")
        }

        val REQUEST_ENABLE_BT = 100

        if (bluetoothAdapter?.isEnabled == false) {
            val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT)
        }

        val pairedDevices: Set<BluetoothDevice>? = bluetoothAdapter?.bondedDevices
        pairedDevices?.forEach { device ->
            val deviceName = device.name
            val deviceHardwareAddress = device.address // MAC address
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main) // res/layout 디렉토리에 있는 activity_main.xml 파일을 사용한다


// Register for broadcasts when a device is discovered.
        val filter = IntentFilter(BluetoothDevice.ACTION_FOUND)
        registerReceiver(receiver, filter)

        val discoverableIntent: Intent = Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE).apply {
            putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300)
        }
        startActivity(discoverableIntent)

        receiver.onReceive(this, discoverableIntent)
        var test = bluetoothAdapter.startDiscovery()
        Log.d("scan test", test.toString())
        Log.d("result?",receiver.resultCode.toString())

        Log.d("result", receiver.list.toString())

//        if (deviceHardwareAddress.toString() == "00:20:12:08:91:90") {
//            ConnectThread(device)
//        }
        Log.d("device", BluetoothDevice.EXTRA_DEVICE)





        //var tread = ConnectThread(device, bluetoothAdapter)


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
    }

    override fun onDestroy() {
        super.onDestroy()


        // Don't forget to unregister the ACTION_FOUND receiver.
        unregisterReceiver(receiver)
    }

    // Create a BroadcastReceiver for ACTION_FOUND.
    private val receiver = object : BroadcastReceiver() {

        val list: MutableList<ListData> = mutableListOf()
        override fun onReceive(context: Context, intent: Intent) {
            val action: String? = intent.action
            when(action) {
                BluetoothDevice.ACTION_FOUND -> {
                    // Discovery has found a device. Get the BluetoothDevice
                    // object and its info from the Intent.
                    val device: BluetoothDevice? =
                        intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)
                    val deviceName = device?.name
                    val deviceHardwareAddress = device?.address // MAC address
                    Log.d("1", "2")
                    list.add(ListData(deviceName, deviceHardwareAddress))
                    Log.d("address", deviceName.toString())
                    Log.d("address", deviceHardwareAddress.toString())
                }
            }
        }
    }

    private inner class ConnectThread(
        device: BluetoothDevice,
        bluetoothAdapter: BluetoothAdapter?
    ) : Thread() {
        var bluetoothAdapter: BluetoothAdapter? = bluetoothAdapter

        private val mmSocket: BluetoothSocket? by lazy(LazyThreadSafetyMode.NONE) {
            var MY_UUID = UUID.randomUUID()
            device.createRfcommSocketToServiceRecord(MY_UUID)
        }

        public override fun run() {
            // Cancel discovery because it otherwise slows down the connection.
            bluetoothAdapter?.cancelDiscovery()

            mmSocket?.use { socket ->
                // Connect to the remote device through the socket. This call blocks
                // until it succeeds or throws an exception.
                socket.connect()

                // The connection attempt succeeded. Perform work associated with
                // the connection in a separate thread.
                //manageMyConnectedSocket(socket)
            }
        }

        // Closes the client socket and causes the thread to finish.
        fun cancel() {
            try {
                mmSocket?.close()
            } catch (e: IOException) {
                Log.e(TAG, "Could not close the client socket", e)
            }
        }
    }

    private const val TAG = "MY_APP_DEBUG_TAG"

    // Defines several constants used when transmitting messages between the
// service and the UI.
    const val MESSAGE_READ: Int = 0
    const val MESSAGE_WRITE: Int = 1
    const val MESSAGE_TOAST: Int = 2
// ... (Add other message types here as needed.)

    class MyBluetoothService(
        // handler that gets info from Bluetooth service
        private val handler: Handler
    ) {

        private inner class ConnectedThread(private val mmSocket: BluetoothSocket) : Thread() {

            private val mmInStream: InputStream = mmSocket.inputStream
            private val mmOutStream: OutputStream = mmSocket.outputStream
            private val mmBuffer: ByteArray = ByteArray(1024) // mmBuffer store for the stream

            override fun run() {
                var numBytes: Int // bytes returned from read()

                // Keep listening to the InputStream until an exception occurs.
                while (true) {
                    // Read from the InputStream.
                    numBytes = try {
                        mmInStream.read(mmBuffer)
                    } catch (e: IOException) {
                        Log.d(TAG, "Input stream was disconnected", e)
                        break
                    }

                    // Send the obtained bytes to the UI activity.
                    val readMsg = handler.obtainMessage(
                        MESSAGE_READ, numBytes, -1,
                        mmBuffer)
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
                    Log.e(TAG, "Could not close the connect socket", e)
                }
            }
        }
    }

    data class ListData(var name: String?, var address: String?) {}



// Establish connection to the proxy.
    //bluetoothAdapter?.getProfileProxy(context, profileListener, BluetoothProfile.HEADSET)

// ... call functions on bluetoothHeadset

// Close proxy connection after use.
    //bluetoothAdapter?.closeProfileProxy(BluetoothProfile.HEADSET, bluetoothHeadset)
}

