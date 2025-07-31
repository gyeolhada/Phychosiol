package com.example.phychosiolz.repo

import android.util.Log
import com.example.phychosiolz.model.GraphDataPack
import com.example.phychosiolz.model.UserTransInfo
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

class UserGraphDataRepository {
    private val gson = Gson()

    //持续监听端口，接收用户的ip，流式返回，管理员（医生）使用
    val userGraphPackFlow: Flow<GraphDataPack> = channelFlow {
        try {
            // 缓冲区大小
            val bufferSize = 1024
            val buffer = ByteArray(bufferSize)

            // 创建DatagramPacket用于接收数据
            val receivePacket = DatagramPacket(buffer, bufferSize)
            while (isActive) {
                // 接收数据
                UDPUtil.rec_graphSocket.receive(receivePacket)
                launch {
                    //gson
                    val receivedData = String(receivePacket.data, 0, receivePacket.length)
                    send(gson.fromJson(receivedData, GraphDataPack::class.java).let {
                        Log.d("UserGraphDataRepository", "emit")
                        it
                    })
                }
            }
        } catch (e: Exception) {
            Log.e("UserGraphDataRepository", e.toString())
        }
    }.flowOn(Dispatchers.IO)
}