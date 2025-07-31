package com.example.phychosiolz.utils.data_process

import com.example.phychosiolz.utils.IOUtil
import kotlin.math.min

object TemperatureDataUtil {
    private var maxTemperature = 0f

    private const val BUFFER_SIZE = 150
    private var dataBuffer: FloatArray = FloatArray(BUFFER_SIZE)
    private var bufferIndex = 0
    private var bufferSize = 0

    private var isValid = true

    fun addEntry(lmtData: ByteArray, call: (Float, Float, Boolean) -> Unit) {
        val value = getEntryValue(lmtData)
        call(getMeanTemperature(value), getMaxTemperature(value), isValidData())
    }
    private fun getEntryValue(lmtData: ByteArray): Float {
        var tmpValue: Int
        var tmpResult: Float
        var meanResult: Float
        var totalResult = 0f
        var i = 0
        while (i < lmtData.size) {
            tmpValue = IOUtil.twoByteToIntReverse(lmtData[i], lmtData[i + 1], 12)
            tmpResult = threeRankFormulaII(tmpValue)
            totalResult += tmpResult
            i = i + 2
        }
        meanResult = (totalResult / (lmtData.size / 2))
        isValid = meanResult.toInt() >= 29 && meanResult.toInt() <= 42
        meanResult = Math.min(41.8f, meanResult)
        meanResult = Math.max(26.2f, meanResult)
        return meanResult
    }

    private fun isValidData(): Boolean {
        return isValid
    }

    private fun getMeanTemperature(value: Float): Float {
        if (bufferSize == 0) {
            bufferSize = BUFFER_SIZE
        }
        dataBuffer[bufferIndex % bufferSize] = value
        bufferIndex++
        val size = min(bufferIndex, bufferSize)
        var tmp = 0f
        for (i in 0 until size) {
            tmp += dataBuffer[i]
        }
        return tmp / size
    }


    private fun getMaxTemperature(value: Float): Float {
        maxTemperature = Math.max(maxTemperature, value)
        return maxTemperature
    }


    //一阶公式 20 - 50 摄氏度
    fun oneRankFormula(value: Int): Float {
        val tmpValue = (value * 3300 / 4096.0).toFloat()
        return (-0.193 * tmpValue + 212.009).toFloat()
    }

    //二阶公式 -55 - 150 摄氏度
    fun twoRankFormulaI(value: Int): Float {
        val tmpValue = (value * 3300 / 4096.0).toFloat()
        return (-8.451576 * Math.pow(10.0, -6.0) * Math.pow(
            tmpValue.toDouble(),
            2.0
        ) - 1.769281 * Math.pow(10.0, -1.0) * tmpValue + 204.3937).toFloat()
    }

    //二阶公式 -10 - 110 摄氏度
    fun twoRankFormulaII(value: Int): Float {
        val tmpValue = (value * 3300 / 4096.0).toFloat()
        return (-7.857923 * Math.pow(10.0, -6.0) * Math.pow(
            tmpValue.toDouble(),
            2.0
        ) - 1.777501 * Math.pow(10.0, -1.0) * tmpValue + 204.6398).toFloat()
    }


    //三阶公式 -55 - 150 摄氏度
    fun threeRankFormulaI(value: Int): Float {
        val tmpValue = (value * 3300 / 4096.0).toFloat()
        return (-1.064200 * Math.pow(10.0, -9.0) * Math.pow(
            tmpValue.toDouble(),
            3.0
        ) - 5.759725 * Math.pow(10.0, -6.0) * Math.pow(
            tmpValue.toDouble(),
            2.0
        ) - 0.1789883 * tmpValue + 204.8570).toFloat()
    }


    //三阶公式 -10 - 110 摄氏度
    fun threeRankFormulaII(value: Int): Float {
        val tmpValue = (value * 3300 / 4096.0).toFloat()
        return (-1.809628 * Math.pow(10.0, -9.0) * Math.pow(
            tmpValue.toDouble(),
            3.0
        ) - 3.325395 * Math.pow(10.0, -6.0) * Math.pow(
            tmpValue.toDouble(),
            2.0
        ) - 0.1814103 * tmpValue + 205.5894).toFloat()
    }
}