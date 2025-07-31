package com.example.phychosiolz.utils

import android.content.Context
import android.net.wifi.WifiManager
import java.net.DatagramPacket
import java.net.DatagramSocket

object UDPUtil {
    val sendSocket: DatagramSocket by lazy {
        DatagramSocket()
    }

    val rec_broadcast_recSocket: DatagramSocket by lazy {
        DatagramSocket(BROADCAST_RECEIVE_PORT)
    }

    val rec_user_infoSocket: DatagramSocket by lazy {
        DatagramSocket(FIND_USER_INFO_RECEIVE_PORT)
    }
    val rec_graphSocket: DatagramSocket by lazy {
        DatagramSocket(RECEIVE_GRAPH_PORT)
    }

    val listen_for_helpSocket: DatagramSocket by lazy {
        DatagramSocket(LISTEN_FOR_HELP_PORT)
    }

    const val BROADCAST_RECEIVE_PORT = 7778
    const val FIND_USER_INFO_RECEIVE_PORT = 7779
    const val RECEIVE_GRAPH_PORT = 7780
    const val LISTEN_FOR_HELP_PORT = 7781

    fun getLocalIpAddress(context: Context): String {
        // 获取WiFi管理器
        val wifiManager = context.getSystemService(Context.WIFI_SERVICE) as WifiManager
        // 获取连接的IP地址
        val ipAddress = wifiManager.connectionInfo.ipAddress
        // 将整数形式的IP地址转换为字符串形式
        return String.format(
            "%d.%d.%d.%d",
            ipAddress and 0xff,
            ipAddress shr 8 and 0xff,
            ipAddress shr 16 and 0xff,
            ipAddress shr 24 and 0xff
        )
    }

    fun intToByteArray(value: Int): ByteArray {
        return byteArrayOf(
            (value and 0xFF).toByte(),
            (value shr 8 and 0xFF).toByte(),
            (value shr 16 and 0xFF).toByte(),
            (value shr 24 and 0xFF).toByte()
        )
    }

    fun send(pack:DatagramPacket){
        sendSocket.send(pack)
    }
}