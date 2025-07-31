package com.example.phychosiolz.repo

import android.net.wifi.WifiManager.LocalOnlyHotspotCallback
import android.util.Log
import com.example.phychosiolz.model.UserTransInfo
import com.example.phychosiolz.network.ManagerNetworkController
import com.example.phychosiolz.utils.UDPUtil
import com.google.gson.Gson
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.net.DatagramPacket

class UserUdpRepository {
    //持续监听端口，接收用户的ip，流式返回，管理员（医生）使用
    @OptIn(DelicateCoroutinesApi::class)
    val broadcastResponseIPFlow: Flow<List<UserTransInfo>> = channelFlow {
        try {
            // 缓冲区大小
            val bufferSize = 1024
            val buffer = ByteArray(bufferSize)

            // 创建DatagramPacket用于接收数据
            val receivePacket = DatagramPacket(buffer, bufferSize)
            // 启动子协程处理接收到的数据
            var time = System.currentTimeMillis()
            var listOfData = mutableListOf<UserTransInfo>()
            while (isActive) {
                // 接收数据
                UDPUtil.rec_user_infoSocket.receive(receivePacket)
                launch {
                    // 在子协程中处理接收到的数据
                    val receivedData =
                        Gson().fromJson(
                            String(receivePacket.data, 0, receivePacket.length),
                            UserTransInfo::class.java
                        )
                    if (!listOfData.contains(receivedData))//去重
                        listOfData.add(receivedData)
                    // 如果时间到了3s，发射数据给流
                    if (System.currentTimeMillis() - time > ManagerNetworkController.scanGap) {
                        Log.d("UserUdpRepository", "broadcastResponseIPFlow: ${listOfData.size}")
                        send(listOfData)
                        listOfData = mutableListOf()
                        time = System.currentTimeMillis()
                    }
                }

            }
        } catch (e: Exception) {
            Log.d("UserUdpRepository", "broadcastResponseIPFlow: ${e.message}")
        }
    }.flowOn(Dispatchers.IO)
}