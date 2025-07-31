package com.example.phychosiolz.network

import android.content.Context
import android.net.DhcpInfo
import android.net.wifi.WifiManager
import android.util.Log
import com.example.phychosiolz.model.GraphDataPack
import com.example.phychosiolz.model.UserLoginInfo
import com.example.phychosiolz.model.UserTransInfo
import com.example.phychosiolz.utils.UDPUtil
import com.google.gson.Gson
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.net.DatagramPacket
import java.net.InetAddress

object UserNetWorkController {
    var isAskingForHelp = false
    //启动协程，持续监听端口，如果有人（管理员）广播自己的ip，就返回自己的ip，用户使用
    fun runListen(context: Context, userLoginInfo: UserLoginInfo) {
        try {
            // 缓冲区大小
            val bufferSize = 1024
            val buffer = ByteArray(bufferSize)
            // 创建DatagramPacket用于接收数据
            val receivePacket = DatagramPacket(buffer, bufferSize)
            while (true) {
                // 接收数据
                UDPUtil.rec_broadcast_recSocket.receive(receivePacket)
                // 处理接收到的数据
                Log.d("UDPUtil", "found manager")
                val receivedData = String(receivePacket.data, 0, receivePacket.length)
                val myIp = UDPUtil.getLocalIpAddress(context)
                //如果是自己的ip，就不用返回了
                if (receivedData != myIp) {
                    //返回自己的ip
                    val sendData = Gson().toJson(
                        UserTransInfo(
                            userLoginInfo.uname!!,
                            userLoginInfo.pbirthday,
                            userLoginInfo.usex!!,
                            myIp
                        )
                    )
                    // 创建DatagramPacket
                    val packet = DatagramPacket(
                        sendData.toByteArray(),
                        sendData.toByteArray().size,
                        InetAddress.getByName(receivedData),
                        UDPUtil.FIND_USER_INFO_RECEIVE_PORT
                    )
                    // 发送数据
                    UDPUtil.sendSocket.send(packet)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    //发送生理数据给管理员，用来绘制图表
    private val sendGraphIPMap = MutableList(0) { "" }
    private val removeTaskMap = HashMap<String, Runnable>()
    private const val SEND_GRAPH_TIME = 5000L

    @OptIn(DelicateCoroutinesApi::class)
    fun addSendGraphIP(ip: String) {
//        if (sendGraphIPMap.contains(ip)) {
//            //如果已经存在，就删除之前的任务
//            removeTaskMap[ip]?.let {
//                GlobalScope.launch {
//                    it.run()
//                }
//            }
//            removeTaskMap.remove(ip)
//        }
        Log.d("UDPUtil", "addSendGraphIP:$ip")
        if (sendGraphIPMap.contains(ip)) {
            return
        }
        sendGraphIPMap.add(ip)
//        //5s后删除
//        val removeTask = Runnable {
//            sendGraphIPMap.remove(ip)
//        }
//        removeTaskMap[ip] = removeTask
//        GlobalScope.launch {
//            Thread.sleep(SEND_GRAPH_TIME)
//            removeTask.run()
//        }
    }

    fun sendGraphPack(pack: GraphDataPack) {
        //向所有注册的IP发送数据
        try {
            val sendData = Gson().toJson(pack)
            for (ip in sendGraphIPMap) {
                Log.d("UDPUtil", "sendGraphPack:$ip")
                // 创建DatagramPacket
                val packet = DatagramPacket(
                    sendData.toByteArray(),
                    sendData.toByteArray().size,
                    InetAddress.getByName(ip),
                    UDPUtil.RECEIVE_GRAPH_PORT
                )
                // 发送数据
                UDPUtil.send(packet)
            }
        } catch (e: Exception) {
            Log.i("UDPUtil", "sendGraphPackError:${e.message}")
        }
    }

    //发作求助
    fun sendBroadcastForUser(context: Context, userLoginInfo: UserLoginInfo) {
        try {
            isAskingForHelp = true
            Log.d("UDPUtil", "sendBroadcastForManager")
            // 获取WiFi管理器
            val wifiManager = context.getSystemService(Context.WIFI_SERVICE) as WifiManager
            // 获取DHCP信息
            val dhcpInfo: DhcpInfo = wifiManager.dhcpInfo
            // 获取广播地址
            val broadcastAddress =
                InetAddress.getByAddress(UDPUtil.intToByteArray(dhcpInfo.ipAddress or (dhcpInfo.netmask.inv())))
            // 构造要发送的数据
            val message = Gson().toJson(
                UserTransInfo(
                    userLoginInfo.uname!!,
                    userLoginInfo.pbirthday,
                    userLoginInfo.usex!!,
                    UDPUtil.getLocalIpAddress(context)
                )
            )
            val sendData = message.toByteArray()

            // 创建DatagramPacket
            val packet =
                DatagramPacket(
                    sendData,
                    sendData.size,
                    broadcastAddress,
                    UDPUtil.LISTEN_FOR_HELP_PORT
                )
            // 发送数据
            GlobalScope.launch{
                //每隔5s发送一次
                while (isAskingForHelp) {
                    UDPUtil.sendSocket.send(packet)
                    Thread.sleep(5000)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
        }
    }
}