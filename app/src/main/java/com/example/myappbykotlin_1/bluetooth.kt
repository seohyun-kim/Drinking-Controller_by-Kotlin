package com.example.myappbykotlin_1

import android.app.Activity
import android.bluetooth.*
import android.companion.AssociationRequest
import android.companion.BluetoothDeviceFilter
import android.companion.CompanionDeviceManager
import android.content.*
import android.content.ContentValues.TAG
import android.os.*
import androidx.appcompat.app.AppCompatActivity
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.selection.SelectionTracker
import androidx.recyclerview.selection.StableIdKeyProvider
import androidx.recyclerview.selection.StorageStrategy
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myappbykotlin_1.databinding.ActivityBluetoothBinding
import com.example.myappbykotlin_1.databinding.ActivityMainBinding
import kotlinx.android.synthetic.main.activity_alarm_record.*
import kotlinx.android.synthetic.main.activity_bluetooth.*
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.util.*
import java.util.regex.Pattern


const val TAG = "MY_APP_DEBUG_TAG"
const val MESSAGE_READ: Int = 0
const val MESSAGE_WRITE: Int = 1
const val MESSAGE_TOAST: Int = 2
private const val SELECT_DEVICE_REQUEST_CODE = 0

@RequiresApi(Build.VERSION_CODES.O)
class bluetooth : AppCompatActivity() {

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

//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_bluetooth)
//
//        val discoverableIntent: Intent = Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE).apply {
//            putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300)
//        }
//        //startActivity(discoverableIntent)
//
//        //receiver.onReceive(this, discoverableIntent)
//        //var test = bluetoothAdapter.startDiscovery()
//        //Log.d("scan test", test.toString())
//        //Log.d("result?",receiver.resultCode.toString())
//
//        //Log.d("result", receiver.list.toString())
//
////        if (deviceHardwareAddress.toString() == "00:20:12:08:91:90") {
////            ConnectThread(device)
////        }
//        //Log.d("device", BluetoothDevice.EXTRA_DEVICE)
//
//
//
//
//
//
//        //var tread = ConnectThread(device, bluetoothAdapter)
//
//
////        var tracker = SelectionTracker.Builder(
////            "my-selection-id",
////            recyclerView,
////            StableIdKeyProvider(recyclerView),
////            MyDetailsLookup(recyclerView),
////            StorageStrategy.createLongStorage())
////            .withOnItemActivatedListener(myItemActivatedListener)
////            .build()
//
//
//        //뷰 바인딩
//        val binding= ActivityBluetoothBinding.inflate(layoutInflater) //뷰 바인딩 사용 준비
//        setContentView(binding.root) //화면 안의 버튼 사용 가능
//
//        binding.btnScan.setOnClickListener {
//            //startActivity(discoverableIntent)
//            recyclerView.layoutManager = LinearLayoutManager(this)
//            receiver.onReceive(this, discoverableIntent)
//            bluetoothAdapter.startDiscovery()
//        }
//        val filter = IntentFilter(BluetoothDevice.ACTION_FOUND)
//        registerReceiver(receiver, filter)
//
//    }



        private val deviceManager: CompanionDeviceManager by lazy {
            getSystemService(Context.COMPANION_DEVICE_SERVICE) as CompanionDeviceManager
        }

        @RequiresApi(Build.VERSION_CODES.O)
        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            setContentView(R.layout.activity_main)

            // To skip filters based on names and supported feature flags (UUIDs),
            // omit calls to setNamePattern() and addServiceUuid()
            // respectively, as shown in the following  Bluetooth example.
            val deviceFilter: BluetoothDeviceFilter = BluetoothDeviceFilter.Builder()
                .setNamePattern(Pattern.compile("My device"))
                .addServiceUuid(ParcelUuid(UUID(0x123abcL, -1L)), null)
                .build()

            // The argument provided in setSingleDevice() determines whether a single
            // device name or a list of them appears.
            val pairingRequest: AssociationRequest = AssociationRequest.Builder()
                .addDeviceFilter(deviceFilter)
                .setSingleDevice(true)
                .build()

            // When the app tries to pair with a Bluetooth device, show the
            // corresponding dialog box to the user.
            deviceManager.associate(pairingRequest,
                object : CompanionDeviceManager.Callback() {

                    override fun onDeviceFound(chooserLauncher: IntentSender) {
                        startIntentSenderForResult(chooserLauncher,
                            SELECT_DEVICE_REQUEST_CODE, null, 0, 0, 0)
                    }

                    override fun onFailure(error: CharSequence?) {
                        // Handle the failure.
                    }
                }, null)
        }

        override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
            when (requestCode) {
                SELECT_DEVICE_REQUEST_CODE -> when(resultCode) {
                    Activity.RESULT_OK -> {
                        // The user chose to pair the app with a Bluetooth device.
                        val deviceToPair: BluetoothDevice? =
                            data?.getParcelableExtra(CompanionDeviceManager.EXTRA_DEVICE)
                        deviceToPair?.let { device ->
                            device.createBond()
                            // Maintain continuous interaction with a paired device.
                        }
                    }
                }
                else -> super.onActivityResult(requestCode, resultCode, data)
            }
        }



    override fun onDestroy() {
        super.onDestroy()


        // Don't forget to unregister the ACTION_FOUND receiver.
        unregisterReceiver(receiver)
    }




    // Create a BroadcastReceiver for ACTION_FOUND.
    private val receiver = object : BroadcastReceiver() {
        var id: Int = 1
        var deviceList: MutableList<BluetoothDevice?> = mutableListOf()
        var list: MutableList<ListData> = mutableListOf()

        var adapter = CustomAdapter() { list -> // 리스너 클릭 함수
            Log.d(list.name, list.address.toString())
            Log.d("device", deviceList.toString())
            ConnectThread(deviceList[list.id - 1], bluetoothAdapter).run()
        }

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
                    //if (deviceName != null and deviceHardwareAddress != null)
                    list.add(ListData(id, deviceName, deviceHardwareAddress))
                    deviceList.add(device)
                    Log.d("address", deviceName.toString())
                    Log.d("address", deviceHardwareAddress.toString())


                    adapter.dataSet = list
                    //RecyclerView.scrollToPosition(list.size - 1)
                    recyclerView.adapter = adapter
                    id += 1
                }
            }
        }
    }

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
            // Cancel discovery because it otherwise slows down the connection.
            bluetoothAdapter?.cancelDiscovery()

            mmSocket?.use { socket ->
                // Connect to the remote device through the socket. This call blocks
                // until it succeeds or throws an exception.
                socket.connect()

                // The connection attempt succeeded. Perform work associated with
                // the connection in a separate thread.
                manageMyConnectedSocket(socket)

                //MyBluetoothService(Handler())
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

            var test = MyBluetoothService(handler).ConnectedThread(socket)
            test.run()
        }
    }

    val handler = object:  Handler(Looper.getMainLooper()) {

        override fun handleMessage(msg: Message) {
            Log.d("do","do")
            when (msg.what) {
                MESSAGE_WRITE -> {
                    val readBuff = msg.obj as ByteArray
                    val tempMsg = String(readBuff, 0, msg.arg1, charset("UTF-8"))
                    Log.d("arduino message", tempMsg)
                }
            }

        }
    }

    // Defines several constants used when transmitting messages between the
// service and the UI.

// ... (Add other message types here as needed.)

    class MyBluetoothService(
        // handler that gets info from Bluetooth service
        private val handler: Handler
    ) {
        inner class ConnectedThread(private val mmSocket: BluetoothSocket) : Thread() {

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
                    Log.d("readMsg", readMsg.toString())
                    Log.d("readMsg", String(readMsg.obj as ByteArray, charset("UTF-8")))
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

    data class ListData(var id: Int, var name: String?, var address: String?) {}



// Establish connection to the proxy.
    //bluetoothAdapter?.getProfileProxy(context, profileListener, BluetoothProfile.HEADSET)

// ... call functions on bluetoothHeadset

// Close proxy connection after use.
    //bluetoothAdapter?.closeProfileProxy(BluetoothProfile.HEADSET, bluetoothHeadset)


    class CustomAdapter(var itemClick: (ListData) -> Unit) :
        RecyclerView.Adapter<CustomAdapter.ViewHolder>() {
        var dataSet = mutableListOf<ListData>()

        /**
         * Provide a reference to the type of views that you are using
         * (custom ViewHolder).
         */
        inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val textName: TextView
            val textAddress: TextView
            val view: View = view

            init {
                // Define click listener for the ViewHolder's View.
                textName = view.findViewById(R.id.deviceName)
                textAddress = view.findViewById(R.id.deviceAddress)


            }

            fun setText(listData: ListData) {
                textName.text = "${listData.name}"
                textAddress.text = "${listData.address}"
                    //"[${listData.id}회차] ${listData.time} : ${listData.title}ml"
                view.setOnClickListener { itemClick(listData) }
            }

        }

        // Create new views (invoked by the layout manager)
        override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
            // Create a new view, which defines the UI of the list item
            val view = LayoutInflater.from(viewGroup.context)
                .inflate(R.layout.bluetooth_device, viewGroup, false)

            return ViewHolder(view)
        }

        // Replace the contents of a view (invoked by the layout manager)
        override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {

            // Get element from your dataset at this position and replace the
            // contents of the view with that element
            var data = dataSet.get(position)
            viewHolder.setText(data)
            Log.d("data", data.toString())
        }

        // Return the size of your dataset (invoked by the layout manager)
        override fun getItemCount() = dataSet.size

    }
}