package com.example.phychosiolz.service

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCallback
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothGattDescriptor
import android.bluetooth.BluetoothManager
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.app.ServiceCompat
import androidx.lifecycle.MutableLiveData
import com.example.phychosiolz.MainActivity
import com.example.phychosiolz.MyApplication
import com.example.phychosiolz.R
import com.example.phychosiolz.data.enums.EmotionType
import com.example.phychosiolz.data.enums.TestDataType
import com.example.phychosiolz.data.room.dao.EmotionDao
import com.example.phychosiolz.data.room.dao.TestDataDao
import com.example.phychosiolz.data.room.model.Emotion
import com.example.phychosiolz.data.room.model.TestData
import com.example.phychosiolz.model.BLEDeviceInfo
import com.example.phychosiolz.model.GraphDataPack
import com.example.phychosiolz.model.ManagerTransInfo
import com.example.phychosiolz.network.UserHTTPServer
import com.example.phychosiolz.network.UserNetWorkController
import com.example.phychosiolz.network.port
import com.example.phychosiolz.utils.ChaquopyUtil
import com.example.phychosiolz.utils.IOUtil
import com.example.phychosiolz.utils.UDPUtil
import com.example.phychosiolz.view_model.DeviceStatus
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.Duration
import java.time.Instant
import java.time.Instant.now
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.Calendar

/***
 * 服务，用于蓝牙连接，数据接收，数据处理，数据上传
 * 为什么要把蓝牙和网络一起写？
 * 答：除了本身网络服务大部分功能依赖蓝牙的数据，还有一个是服务会有弹窗，我不喜欢弹窗，所以写一起就只有一个弹窗。
 * --zyd edited on 2024/1/25
 */
class UserService : Service() {
    /**********************以下为服务的数据*********************************/
    private val userInfo by lazy {
        MyApplication.instance.userRepository.loginedUser.value
    }
    private lateinit var dao: TestDataDao
    private lateinit var emodao: EmotionDao

    //scan到的设备
    val scannedDevices: MutableLiveData<MutableList<BLEDeviceInfo>> by lazy {
        MutableLiveData<MutableList<BLEDeviceInfo>>()
    }

    //使用的设备
    val connectedDevice: MutableLiveData<BLEDeviceInfo?> =
        MutableLiveData<BLEDeviceInfo?>(null)

    val isScanning: MutableLiveData<Boolean> by lazy {
        MutableLiveData<Boolean>(false)
    }

    val emoResLiveData: MutableLiveData<EmotionType> by lazy {
        MutableLiveData<EmotionType>(EmotionType.NEUTRAL)
    }

    companion object {
        const val BLE_ServiceUUID = "6e400001-b5a3-f393-e0a9-e50e24dcca9e"
        const val BLE_CharacterUUID = "6e400003-b5a3-f393-e0a9-e50e24dcca9e"
    }

    fun setDao(dao: TestDataDao) {
        this.dao = dao
    }

    private val binder = UserBinder()

    /**********************以下为服务的普通控制逻辑*********************************/
    override fun onBind(intent: Intent): IBinder {
        return binder
    }

    inner class UserBinder : Binder() {
        fun getService(): UserService = this@UserService
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startForeground()
        setup()
        return START_STICKY//保持服务不被杀死
    }

    override fun onDestroy() {
        Log.i("UserService", "onDestroy")
        mHttpServer?.stop()
        super.onDestroy()
    }

    private fun startForeground() {
        try {
            val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val channel = NotificationChannel(
                    "CHANNEL_ID", "设备通知", NotificationManager.IMPORTANCE_HIGH
                )
                notificationManager.createNotificationChannel(channel)
            }
            val notification =
                NotificationCompat.Builder(this, "CHANNEL_ID").setSmallIcon(R.mipmap.ic_launcher)
                    .setContentTitle("正在开启蓝牙服务").setContentText("点击查看详情")
                    .setContentIntent(
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                            PendingIntent.getActivity(
                                this,
                                0,
                                Intent(this, MainActivity::class.java),
                                PendingIntent.FLAG_IMMUTABLE
                            )
                        } else {
                            PendingIntent.getActivity(
                                this,
                                0,
                                Intent(this, MainActivity::class.java),
                                PendingIntent.FLAG_MUTABLE
                            )
                        }
                    ).build()
            if (Build.VERSION.SDK_INT >= 34) {
                ServiceCompat.startForeground(/* service = */ this,/* id = */ 100, // Cannot be 0
                    /* notification = */ notification,/* foregroundServiceType = */
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                        ServiceInfo.FOREGROUND_SERVICE_TYPE_CONNECTED_DEVICE
                    } else {
                        0
                    }
                )
            } else {
                Log.i("UserService", "startForeground")
                startForeground(1, notification)
            }
        } catch (e: Exception) {
            Log.i("BLEServiceE", e.toString())
        }
    }

    /**********************以下为蓝牙模块*********************************/

    private val scanCallback = object : ScanCallback() {
        override fun onScanResult(callbackType: Int, result: ScanResult?) {
            super.onScanResult(callbackType, result)
            if (result == null) return
            getDeviceScanned(result.device)
        }
    }

    private val gattCallback = object : BluetoothGattCallback() {
        //状态改变
        @SuppressLint("MissingPermission")
        override fun onConnectionStateChange(gatt: BluetoothGatt?, status: Int, newState: Int) {
            when (newState) {
                BluetoothGatt.STATE_CONNECTED -> {
                    //提高MTU
                    gatt!!.requestMtu(224)
                    connectedDevice.postValue(
                        BLEDeviceInfo(
                            gatt.device.name,
                            DeviceStatus.CONNECTED,
                            gatt.device.address,
                            gatt.device.type.toString(),
                            gatt.device,
                            null,
                            null,
                            null,
                            null
                        )
                    )
                }

                BluetoothGatt.STATE_DISCONNECTED -> {
                    disconnectDevice()
                    Log.i("BLEStatus", "STATE_DISCONNECTED")
                }

                BluetoothGatt.STATE_CONNECTING -> {
                    Log.d("BLEStatus", "正在连接");
                }

                BluetoothGatt.STATE_DISCONNECTING -> {
                    Log.d("BLEStatus", "正在断开");
                }

                else -> {
                    Log.d("BLEStatus", "未知状态");
                }
            }
        }


        //接收数据
        @RequiresApi(Build.VERSION_CODES.O)
        override fun onCharacteristicChanged(
            gatt: BluetoothGatt, characteristic: BluetoothGattCharacteristic
        ) {
            handleData(characteristic.value)
        }


        @SuppressLint("MissingPermission")
        override fun onMtuChanged(gatt: BluetoothGatt, mtu: Int, status: Int) {
            super.onMtuChanged(gatt, mtu, status)
            ///设置mtu值，即bluetoothGatt.requestMtu()时触发，提示该操作是否成功
            if (status == BluetoothGatt.GATT_SUCCESS) {
                gatt.discoverServices()
                Log.w("ble", "设置MTU成功，新的MTU值：" + (mtu - 3) + ",status" + status);
            } else if (status == BluetoothGatt.GATT_FAILURE) {
                Log.e("ble", "设置MTU值失败：" + (mtu - 3) + ",status" + status);
            }
        }

        @SuppressLint("MissingPermission")
        override fun onServicesDiscovered(gatt: BluetoothGatt, status: Int) {
            Log.i("UserService", "onServicesDiscovered")
            //gatt 通信协议
            super.onServicesDiscovered(gatt, status)
            for (blueToothService in gatt.services) {
                if (blueToothService.uuid.toString() == BLE_ServiceUUID) {
                    for (bluetoothGattCharacteristic in blueToothService.characteristics) {
                        if (bluetoothGattCharacteristic.uuid.toString() == BLE_CharacterUUID) {
                            //打开通知，否则接收不到信号
                            val isEnableNotification = gatt.setCharacteristicNotification(
                                bluetoothGattCharacteristic, true
                            )
                            if (isEnableNotification) {
                                val descriptorList = bluetoothGattCharacteristic.descriptors
                                if (descriptorList != null && descriptorList.size > 0) {
                                    for (descriptor in descriptorList) {
                                        descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE)
                                        gatt.writeDescriptor(descriptor)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private var minCounterForHeart: Float = 0f//值的累加
    private var minValidCountForHeart: Int = 0//计算加了多少个

    private var minCounterForTemp: Float = 0f
    private var minValidCountForTemp: Int = 0

    private var minCounterForO2: Float = 0f
    private var minValidCountForO2: Int = 0

    data class DeviceData(
        val heartRateValidValue: Float,
        val temperatureValidValue: Float,
        val bloodOxygenValidValue: Float,
        val pointHeartbeat: Float,
        val pointSpO2: Float,
        val pointTemp: Float,
        val pointSkin: Float,
        val isHeartValid: Boolean,
        val isTempValid: Boolean,
        val isO2Valid: Boolean,
        val maxTemp: Float,
        val elc: Int
    )

//    val deviceData: MutableLiveData<DeviceData> by lazy {
//        MutableLiveData<DeviceData>(
//            DeviceData(
//                0f,
//                0f,
//                0f,
//                0f,
//                0f,
//                0f,
//                0f,
//                false,
//                false,
//                false,
//                0f,
//                0
//            )
//        )
//    }
//    fun refreshDeviceData(hr: Float, temp: Float, o2: Float, ph: Float, po2: Float, pt: Float, ps: Float, ih: Boolean, it: Boolean, io: Boolean, mt: Float, elc: Int) {
//        deviceData.postValue(DeviceData(hr, temp, o2, ph, po2, pt, ps, ih, it, io, mt, elc))
//    }

    //主界面的值
    val heartRateValidValue: MutableLiveData<Float> by lazy {
        MutableLiveData<Float>(0f)
    }
    val temperatureValidValue: MutableLiveData<Float> by lazy {
        MutableLiveData<Float>(0f)
    }
    val bloodOxygenValidValue: MutableLiveData<Float> by lazy {
        MutableLiveData<Float>(0f)
    }
    val dataTimeValue: MutableLiveData<String> by lazy {
        MutableLiveData<String>("")
    }

    val pointHeartbeat: MutableLiveData<Float> by lazy {
        MutableLiveData<Float>(0f)
    }
    val pointSpO2: MutableLiveData<Float> by lazy {
        MutableLiveData<Float>(0f)
    }
    val pointTemp: MutableLiveData<Float> by lazy {
        MutableLiveData<Float>(0f)
    }
    val pointSkin: MutableLiveData<Float> by lazy {
        MutableLiveData<Float>(0f)
    }

    val isHeartValid: MutableLiveData<Boolean> by lazy {
        MutableLiveData<Boolean>(false)
    }
    val isTempValid: MutableLiveData<Boolean> by lazy {
        MutableLiveData<Boolean>(false)
    }
    val isO2Valid: MutableLiveData<Boolean> by lazy {
        MutableLiveData<Boolean>(false)
    }
    val maxTemp: MutableLiveData<Float> by lazy {
        MutableLiveData<Float>(0f)
    }

    val elc: MutableLiveData<Int> by lazy {
        MutableLiveData<Int>(0)
    }

    //计时器，用于计算一分钟内的平均值
    private val LIMIT = 6000

    //1s20次，1分钟1200，5分钟6000
    private var timeCounter = 0//计数器，达到6000时，启动一次情绪识别

    @RequiresApi(Build.VERSION_CODES.O)
    private fun handleData(value: ByteArray) {
        Log.i("UserService", "handleData")
        //避免进行识别时，数据文件被写入，引发错误，当进行识别时，不接收数据
        timeCounter += 1//计数器加一
        //计算时间

        if (timeCounter >= LIMIT) {//如果计数器>=6000,数据不会使用，直接丢弃
            if (timeCounter == LIMIT) {//如果是6000，就启动识别
                runRecognition()
                timeCounter = 0//计数器清零
            }
            return
        }

        //处理数据
        //1s 20个数据包
        bagCounterForFineTuneDataCollect += 1//收集了一个数据包，计数器加一

        IOUtil.handleData(
            value,
            userInfo!!.uid!!.toString(),
            timeCounter >= LIMIT - 20 && timeCounter <= LIMIT,//是否是最后20个数据包,用于情绪识别
            { pointValue, heartRate, isValid ->
                /***
                 * 心率,
                 */
                Log.i("UserServiceHeartRate", "H:$heartRate")
                pointHeartbeat.postValue(pointValue)//传回界面画图
                isHeartValid.postValue(isValid)//传回界面
                heartRateValidValue.postValue(heartRate)//传回界面
                if (isValid) {
                    minCounterForHeart += heartRate //如果合法，则加上
                    minValidCountForHeart += 1//计数器加一，因为有些包不合法，所以要计数
                    // 1200个数据包就刷新一次数据库
                    if (timeCounter % 1200 == 0) {
                        minValidCountForHeart = 0//计数器清零
                        minCounterForHeart = 0f//清零
                        //取出数据库同一时间段（30分钟），计算均值
                        val currentDateTime: LocalDateTime =
                            LocalDateTime.ofInstant(now(), ZoneId.systemDefault())//传回包的时间
                        val data = dao.getTestData(
                            userInfo!!.uid!!,
                            currentDateTime.year,
                            currentDateTime.monthValue,
                            currentDateTime.dayOfMonth,
                            currentDateTime.hour * 2 + if (currentDateTime.minute > 30) 1 else 0,
                            TestDataType.HEART_RATE.code
                        )
                        val currentMinRes = minCounterForHeart / minValidCountForHeart//计算均值
                        if (data == null) {//如果没有数据，说明当前时间段没有数据，就插入
                            dao.insert(
                                TestData(
                                    userInfo!!.uid!!,
                                    currentDateTime.year,
                                    currentDateTime.monthValue,
                                    currentDateTime.dayOfMonth,
                                    currentDateTime.hour * 2 + if (currentDateTime.minute > 30) 1 else 0,
                                    1,
                                    currentMinRes,
                                    TestDataType.HEART_RATE.code
                                )
                            )
                        } else {//如果有数据，就更新
                            data.avg = (data.avg * data.num + currentMinRes) / (data.num + 1)
                            data.num += 1//当前分钟的数据量+1
                            dao.update(data)
                        }
                    }
                }
            },
            { avg, max, isValid ->
                /***
                 * 体温
                 */
                Log.i("UserService", "T:$avg")
                pointTemp.postValue(avg)
                temperatureValidValue.postValue(avg)
                isTempValid.postValue(isValid)
                if (maxTemp.value!! < max) {
                    maxTemp.postValue(max)
                }
                if (isValid) {
                    minCounterForTemp += avg
                    minValidCountForTemp += 1
                    if (timeCounter % 1200 == 0) {
                        val currentDateTime: LocalDateTime =
                            LocalDateTime.ofInstant(now(), ZoneId.systemDefault())//传回包的时间
                        minCounterForTemp = 0f
                        minValidCountForTemp = 0
                        val data = dao.getTestData(
                            userInfo!!.uid!!,
                            currentDateTime.year,
                            currentDateTime.monthValue,
                            currentDateTime.dayOfMonth,
                            currentDateTime.hour * 2 + if (currentDateTime.minute > 30) 1 else 0,
                            TestDataType.TEMPERATURE.code
                        )
                        val currentMinRes = minCounterForTemp / minValidCountForTemp
                        if (data == null) {
                            dao.insert(
                                TestData(
                                    userInfo!!.uid!!,
                                    currentDateTime.year,
                                    currentDateTime.monthValue,
                                    currentDateTime.dayOfMonth,
                                    currentDateTime.hour * 2 + if (currentDateTime.minute > 30) 1 else 0,
                                    1,
                                    currentMinRes,
                                    TestDataType.TEMPERATURE.code
                                )
                            )
                        } else {
                            data.avg = (data.avg * data.num + currentMinRes) / (data.num + 1)
                            data.num += 1
                            dao.update(data)
                        }
                    }
                }
            },
            { pointValue, avg, isValid ->
                /***
                 * 血氧
                 */
                Log.i("UserService", "O:$avg")
                pointSpO2.postValue(pointValue)
                bloodOxygenValidValue.postValue(avg)
                isO2Valid.postValue(isValid)
                if (isValid) {
                    minCounterForO2 += avg
                    minValidCountForO2 += 1
                    if (timeCounter % 1200 == 0) {
                        val currentDateTime: LocalDateTime =
                            LocalDateTime.ofInstant(now(), ZoneId.systemDefault())//传回包的时间
                        minValidCountForO2 = 0
                        minCounterForO2 = 0f
                        val data = dao.getTestData(
                            userInfo!!.uid!!,
                            currentDateTime.year,
                            currentDateTime.monthValue,
                            currentDateTime.dayOfMonth,
                            currentDateTime.hour * 2 + if (currentDateTime.minute > 30) 1 else 0,
                            TestDataType.SP02.code
                        )
                        val currentMinRes = minCounterForO2 / minValidCountForO2
                        if (data == null) {
                            dao.insert(
                                TestData(
                                    userInfo!!.uid!!,
                                    currentDateTime.year,
                                    currentDateTime.monthValue,
                                    currentDateTime.dayOfMonth,
                                    currentDateTime.hour * 2 + if (currentDateTime.minute > 30) 1 else 0,
                                    1,
                                    currentMinRes,
                                    TestDataType.SP02.code
                                )
                            )
                        } else {
                            data.avg = (data.avg * data.num + currentMinRes) / (data.num + 1)
                            data.num += 1
                            dao.update(data)
                        }
                    }
                }
            },
            { pointValue ->
                pointSkin.postValue(pointValue)
            },
            {
                elc.postValue(it)
            },
            bagCounterForFineTuneDataCollect <= 500,
            isDepressionAttack
        )

        //每4个数据包，就上传一次数据
        if (timeCounter % 4 == 0) {
            GlobalScope.launch {
                withContext(Dispatchers.IO) {
                    //传给需要的医生
                    val dataPack = GraphDataPack(
                        heartRateValidValue.value!!,
                        pointHeartbeat.value!!,
                        temperatureValidValue.value!!,
                        maxTemp.value!!,
                        bloodOxygenValidValue.value!!,
                        pointSpO2.value!!,
                        pointSkin.value!!
                    )
                    UserNetWorkController.sendGraphPack(dataPack)
                }
            }
        }
    }



    private val bluetoothAdapter: BluetoothAdapter by lazy {
        (getSystemService(BLUETOOTH_SERVICE) as BluetoothManager).adapter
    }
    private var bluetoothGatt: BluetoothGatt? = null
    fun switchScanState() {
        isScanning.value = isScanning.value?.not()
        if (isScanning.value == true) {
            startScan()
        } else {
            stopScan()
        }
    }

    @SuppressLint("MissingPermission")
    @Synchronized
    private fun getDeviceScanned(device: BluetoothDevice?) {
        if (device == null || device.name == null) return
        //避免重复添加
        var list = scannedDevices.value
        if (list != null) {
            for (item in list) {
                if (item.mDeviceName == device.name) {
                    return
                }
            }
        } else {
            list = mutableListOf()
        }
        list.add(
            BLEDeviceInfo(
                device.name,
                DeviceStatus.DISCONNECTED,
                device.address,
                device.type.toString(),
                device,
                null,
                null,
                null,
                null
            )
        )
        Log.i("UserService", "getDeviceScanned$list")
        scannedDevices.postValue(list)
    }

    @SuppressLint("MissingPermission")
    fun startScan() {
        bluetoothAdapter.bluetoothLeScanner?.startScan(scanCallback)
    }

    @SuppressLint("MissingPermission")
    fun stopScan() {
        bluetoothAdapter.bluetoothLeScanner?.stopScan(scanCallback)
    }

    @SuppressLint("MissingPermission")
    fun connectDevice(context: Context, address: String) {
        try {
            Log.i("UserService", "connectDevice")
            //如果有旧的连接，先断开
            connectedDevice.value?.mAddress.let {
                if (it != null) {
                    disconnectDevice()
                }
            }
            val device = bluetoothAdapter.getRemoteDevice(address)
            bluetoothGatt = device.connectGatt(context, false, gattCallback)
            isScanning.postValue(false)
        } catch (exception: IllegalArgumentException) {
        }
    }

    @SuppressLint("MissingPermission")
    fun disconnectDevice() {
        bluetoothGatt?.disconnect()
        Log.i("UserService", "disconnectDevice")
        bluetoothGatt?.close()
        connectedDevice.postValue(connectedDevice.value.let {
            it?.mDeviceState = DeviceStatus.DISCONNECTED
            it
        })
    }

    /**********************以下为 网络控制 与 情绪、抑郁症识别*********************************/
    private var mHttpServer: UserHTTPServer? = null
    var responseDoctor = MutableLiveData<ManagerTransInfo?>()
    val isDepressionAttackRecognitionsOn = MutableLiveData(false)
    val dsRepo = MyApplication.instance.dataStoreRepository

    //允许自己被发现
    @OptIn(DelicateCoroutinesApi::class)
    private fun setup() {
        GlobalScope.launch {
            if (mHttpServer == null) {
                mHttpServer = UserHTTPServer(UDPUtil.getLocalIpAddress(this@UserService), port)
                mHttpServer!!.start(10000, false)
                (mHttpServer as UserHTTPServer).listener =
                    object : UserHTTPServer.OnDoctorResponseListener {
                        override fun onDoctorResponse(response: ManagerTransInfo) {
                            Log.i("UserService", "onDoctorResponse")
                            responseDoctor.postValue(response)
                            UserNetWorkController.isAskingForHelp = false
                        }
                    }
            }
            launch {
                withContext(Dispatchers.IO) {
                    UserNetWorkController.runListen(
                        this@UserService, MyApplication.instance.userRepository.loginedUser.value!!
                    )
                }
            }
            //init chaquo python
            ChaquopyUtil.setup(this@UserService)
        }
    }

    //识别出抑郁症，弹窗询问是否需要帮助
    fun depressionAttack() {
        dCallback?.onDepression()
    }

    //确认需要帮助，向管理员发送请求
    fun askForHelp() {
        Log.i("UserService", "askForHelp")
        UserNetWorkController.sendBroadcastForUser(this, userInfo!!)
    }

    fun attackDone() {
        mHttpServer?.clear()
        responseDoctor.postValue(null)
        UserNetWorkController.isAskingForHelp = false
        //
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun runRecognition() {
        val res =
            ChaquopyUtil.runEmotionRecognition(IOUtil.getUserDir(userInfo!!.uid!!.toString()))
        emoResLiveData.postValue(res)//刷新值
        Log.i("UserService", "EmoRes:$res")
        //存储思路，存储前先查当天是否有这种情绪，如果有，就数量加1，如果没有，就插入
        val currentDateTime: LocalDateTime =
            LocalDateTime.ofInstant(now(), ZoneId.systemDefault())//传回包的时间
        val data = emodao.getASpecificEmotionDataByUid(
            userInfo!!.uid!!,
            currentDateTime.year,
            currentDateTime.monthValue,
            currentDateTime.dayOfMonth,
            res.code
        )
        if (data == null) {//如果没有数据，就插入
            emodao.insertEmotionData(
                Emotion(
                    null,
                    userInfo!!.uid!!.toString(),
                    res.code,
                    currentDateTime.year,
                    currentDateTime.monthValue,
                    currentDateTime.dayOfMonth,
                    1
                )
            )
        } else {//如果有数据，就更新
            emodao.updateEmotionData(data.eid!!)
        }
        if (isDepressionAttackRecognitionsOn.value == true) {
            ChaquopyUtil.runDepressionAttackRecognition(
                ::depressionAttack,
                IOUtil.getUserDir(userInfo!!.uid!!.toString()),
                IOUtil.getDepressionAttackFineTuneModelPath(userInfo!!.uid!!.toString())
            )
        }
    }

    //迁移抑郁症识别模型
// 首先，用IOUtil完成数据的拼接，两种样本的数据各取15s
    fun doDepressionRecognitionMigration(): Boolean {
        if (!IOUtil.catFineTuneData(userInfo!!.uid!!.toString())) return false
        GlobalScope.launch {
            withContext(Dispatchers.IO) {
                ChaquopyUtil.runDepressionRecognitionMigration(
                    IOUtil.getCatFineTuneDataPath(userInfo!!.uid!!.toString()),
                    IOUtil.getDepressionAttackFineTuneModelPath(userInfo!!.uid!!.toString())
                )
                withContext(Dispatchers.Main) {
                    isDepressionAttackRecognitionsOn.postValue(true)
                }
            }
        }
        return true
    }

    fun turnOnDepressionAttackRecognitions(onFineTuneNeeded: () -> Unit) {
        if (isDepressionAttackRecognitionsOn.value == true)
            return
        IOUtil.checkDepressionAttackModelExist(
            userInfo!!.uid!!.toString()
        ).let {
            if (it) {
                isDepressionAttackRecognitionsOn.postValue(true)
            } else {
                isDepressionAttackRecognitionsOn.postValue(false)
                onFineTuneNeeded()//需要微调

            }
        }
    }

    fun ifAttackDataExist(): Boolean {
        return IOUtil.checkDepressionAttackModelExist(
            userInfo!!.uid!!.toString()
        )
    }

    private var isDepressionAttack = false

    //微调数据收集25s
    var bagCounterForFineTuneDataCollect = 0//达到25s时结束，即25*20=500

    /***
     * 开始收集微调数据
     * 如果包数小于500，说明前面的数据还没有收集完，直接返回，显示当前还有任务在进行
     */
    fun startToCollectFineTuneData(isAttack: Boolean): Boolean {
        //if not connected, return false
        if (connectedDevice.value == null) return false
        if (bagCounterForFineTuneDataCollect <= 500)
            return false
        isDepressionAttack = isAttack
        bagCounterForFineTuneDataCollect = 0
        return true
    }

    fun checkDepressionAttackFineTuneAttackDataExist(): Boolean {
        return IOUtil.checkDepressionAttackFineTunePeaceDataExist(userInfo!!.uid!!.toString());
    }

    fun checkDepressionAttackFineTunePeaceDataExist(): Boolean {
        return IOUtil.checkDepressionAttackFineTuneAttackDataExist(userInfo!!.uid!!.toString());
    }

    var dCallback: DepressionCallback? = null

    interface DepressionCallback {
        fun onDepression()
    }
}