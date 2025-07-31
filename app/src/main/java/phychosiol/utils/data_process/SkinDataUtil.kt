package com.example.phychosiolz.utils.data_process

import com.example.phychosiolz.utils.IOUtil

object SkinDataUtil {
    fun addEntry(data: ByteArray,  skinCall: (Float) -> Unit) {
        skinCall(getEntryValue(data))
    }
    private fun getEntryValue(gsrData: ByteArray): Float {
        var meanValue = 0f
        var i = 0
        while (i < gsrData.size) {
            meanValue += IOUtil.twoByteToIntReverse(gsrData[i], gsrData[i + 1], 12)
            i = i + 2
        }
        meanValue = (meanValue / (gsrData.size / 2.0)).toFloat()
        return meanValue
    }
}