package com.example.phychosiolz.network

import android.content.Context
import android.net.DhcpInfo
import android.net.wifi.WifiManager
import android.os.Handler
import android.util.Log
import com.example.phychosiolz.manager.ManaMsg
import com.example.phychosiolz.model.ManagerTransInfo
import com.example.phychosiolz.model.UserLoginInfo
import com.example.phychosiolz.model.UserTransInfo
import com.example.phychosiolz.utils.HttpUtil
import com.example.phychosiolz.utils.UDPUtil
import com.google.gson.Gson
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Response
import okio.IOException
import java.net.DatagramPacket
import java.net.InetAddress

object ManagerNetworkController {
    var isManagerScanOn = false
    const val scanGap = 3000L
    private var isManagerListeningOn = false
    private var keepAliveUserIp = ""
    fun shutDown() {
        isManagerListeningOn = false
    }

    fun runManager(context: Context) {//广播自己的ip,每3秒广播一次，同时监听端口，接收用户的ip
        while (isManagerScanOn) {
            sendBroadcastForManager(context)
            Thread.sleep(scanGap)
        }
    }

    //广播自己的ip,寻找用户
    private fun sendBroadcastForManager(context: Context) {
        try {
            Log.d("UDPUtil", "sendBroadcastForManager")
            // 获取WiFi管理器
            val wifiManager = context.getSystemService(Context.WIFI_SERVICE) as WifiManager
            // 获取DHCP信息
            val dhcpInfo: DhcpInfo = wifiManager.dhcpInfo
            // 获取广播地址
            val broadcastAddress =
                InetAddress.getByAddress(UDPUtil.intToByteArray(dhcpInfo.ipAddress or (dhcpInfo.netmask.inv())))
            // 构造要发送的数据
            val message = UDPUtil.getLocalIpAddress(context)//直接发送 ip
            val sendData = message.toByteArray()

            // 创建DatagramPacket
            val packet =
                DatagramPacket(
                    sendData,
                    sendData.size,
                    broadcastAddress,
                    UDPUtil.BROADCAST_RECEIVE_PORT
                )
            // 发送数据
            UDPUtil.sendSocket.send(packet)
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
        }
    }

    //选中用户后调用，进入生理图标，向用户服务器发送请求，获取生理数据
    fun sendRequestForGraph(ip: String, callback: Callback) {
        try {
            Log.d("UDPUtil", "http://$ip:$port$HTTP_URI_GET_GRAPH")
            HttpUtil.get("http://$ip:$port$HTTP_URI_GET_GRAPH", callback)
            keepAliveUserIp = ip
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
        }
    }

    fun keepAlive(callback: Callback) {
//        try {
//            HttpUtil.get("http://$keepAliveUserIp:$port$HTTP_URI_GET_GRAPH", callback)
//        } catch (e: Exception) {
//            e.printStackTrace()
//        } finally {
//        }
    }

    fun runListenForHelp(handler:Handler) {
        try {
            // 缓冲区大小
            val bufferSize = 1024
            val buffer = ByteArray(bufferSize)
            isManagerListeningOn = true
            // 创建DatagramPacket用于接收数据
            val receivePacket = DatagramPacket(buffer, bufferSize)
            GlobalScope.launch {
                while (isManagerListeningOn) {
                    Log.d("runListenForHelp", "runListenForHelp")
                    // 接收数据
                    UDPUtil.listen_for_helpSocket.receive(receivePacket)
                    // 处理接收到的数据
                    val receivedData = String(receivePacket.data, 0, receivePacket.length)
                    //获取抑郁症用户ip
                    //gson
                    val userTransInfo = Gson().fromJson(receivedData, UserTransInfo::class.java)
                    Log.d("runListenForHelp", "runListenForHelp: ${userTransInfo.userIp}")
                    handler.obtainMessage(ManaMsg.RECEIVE_HELP.code, userTransInfo).sendToTarget()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun doctorResponse(
        ip: String, //用户ip
        info: ManagerTransInfo,
        accept: (Unit) -> Unit,
        reject: (String) -> Unit
    ) {
        try {
            HttpUtil.post("http://$ip:$port$HTTP_URI_DOCTOR_RESPONSE?${Gson().toJson(info)}".let {
                                                                          Log.d("ManagerNetworkController", it)
                it
            }, "", object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                }

                override fun onResponse(call: Call, response: Response) {
                    val name = response.body?.string()
                    if (response.code ==200)
                        accept(Unit)
                    else
                        reject(name?:"")
                }
            })
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
        }
    }
}