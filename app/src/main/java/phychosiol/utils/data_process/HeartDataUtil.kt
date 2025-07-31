package com.example.phychosiolz.utils.data_process

import android.util.Log
import com.example.phychosiolz.utils.IOUtil
import java.util.Arrays

object HeartDataUtil {
    /**
     * 心率数据处理
     * 参考https://pulsesensor.com/pages/pulse-sensor-amped-arduino-v1dot1 算法
     * 本质是 寻找 1/2 振幅，同时更新 波峰、波谷、阈值。使用了第一次心跳、第二次心跳作为标识
     */
    private var meanCapacity: Int = 50

    //计算心率
    private var IBI = 600.0
    private val rate = DoubleArray(10)
    private var sampleCounter = 0.0
    private var lastBeatTime = 0.0
    private var P = 2048 // 波峰
    private var T = 2048 // 波谷
    private var thresh = 2048 //阈值
    private var firstBeat = true
    private var secondBeat = false
    private var Pulse = false
    private var isValid = false
    private const val dataCapacity = 1000
    private val heartData = FloatArray(dataCapacity)
    private var dataIndex = 0

    //求均值
    private val meanBuffer: FloatArray
    private var current_entry: Int

    init {
        meanBuffer = FloatArray(meanCapacity)
        current_entry = 0
    }

    init {
        initData()
    }

    //初始化
    private fun initData() {
        thresh = 2048
        P = 2048
        T = 2048
        lastBeatTime = sampleCounter
        firstBeat = true
        secondBeat = false
        IBI = 600.0
        Pulse = false
        isValid = false
        Arrays.fill(rate, 0.0)
    }

    fun addEntry(pulData: ByteArray, heartCall:(Float,Float,Boolean)->Unit){
        val data = byteToFloat(pulData)
        val entry = getEntryData(data)//可视化数据，将一组数据转化为一个数据

        val handledList = handlerHeartData(data, data.size)
        val meanValue = getMeanValue(handledList)
        val isValid = isValidData(handledList)
        Log.i("HeartDataUtil", "entry:${entry},meanValue:${meanValue},isValid:${isValid}")
        heartCall(entry,meanValue,isValid)
    }

    //初步处理，byte转float
    fun byteToFloat(pulData: ByteArray): FloatArray {
        val data = FloatArray(pulData.size / 2)
        var i = 0
        while (i < pulData.size) {
            data[i / 2] = IOUtil.twoByteToIntReverse(pulData[i], pulData[i + 1], 12).toFloat()
            i = i + 2
        }
        return data
    }

    //处理后的数据用来画图
    fun getEntryData(data: FloatArray): Float {
        var tmp = 0f
        for (pulData in data) {
            tmp += pulData
        }
        return tmp / data.size
    }

    //异常数据判断（合理心率应该在50-150之间）
    fun isValidData(data: List<Float>): Boolean {
        return if (!isValid) {
            false
        } else {
            for (dataNum in data) {
                if (dataNum > 150 || dataNum < 50) {
                    return false
                }
            }
            true
        }
    }

    //求均值，即平均心率
    fun getMeanValue(floatList: List<Float>): Float {
        var tmpValue = 0f
        for (i in floatList.indices) {
            tmpValue += floatList[i]
        }
        if (floatList.size > 0) {
            tmpValue = tmpValue / floatList.size
            meanBuffer[current_entry % meanCapacity] = tmpValue
            current_entry++
        }
        val num = Math.min(current_entry, meanCapacity)
        tmpValue = 0f
        for (i in 0 until num) {
            tmpValue += meanBuffer[i]
        }
        return tmpValue / num
    }


    //滑动平均 heartData数据主要用在异常判断
    private fun addCheckData(tmp: Float) {
        heartData[dataIndex++] = tmp
        if (dataIndex == dataCapacity) {
            System.arraycopy(heartData, dataCapacity / 2, heartData, 0, dataCapacity / 2)
            dataIndex = dataCapacity / 2
        }
    }

    //异常判断，是否明显的波峰波谷趋势
    private fun checkData() {
        var average = 0f
        var count = 0f
        for (i in 0 until dataIndex) {
            average += heartData[i]
        }
        average = average / dataIndex
        for (i in 0 until dataIndex) {
            if (Math.abs(average - heartData[i]) < 100) {
                count++
            }
        }
        if (count > 0.8 * dataIndex) {
            isValid = false
        }
    }

    fun handlerHeartData(data: FloatArray, length: Int): List<Float> {
        isValid = true
        val floatList: MutableList<Float> = ArrayList()
        var tmp: Float
        var N: Int
        for (i in 0 until length) {
            tmp = data[i]
            addCheckData(tmp)
            sampleCounter += 2.5

            //时间间隔
            N = (sampleCounter - lastBeatTime).toInt()

            //更新波谷
            if (tmp < thresh && N > IBI * 3 / 5.0) {
                if (tmp < T) {
                    T = tmp.toInt()
                }
            }
            //更新波峰
            if (tmp > thresh && tmp > P) {
                P = tmp.toInt()
            }

            //时间间隔大于250ms
            if (N >= 250) {
                //大于阈值
                if (tmp > thresh && !Pulse && N > IBI * 3 / 5.0) {
                    Pulse = true
                    IBI = sampleCounter - lastBeatTime
                    lastBeatTime = sampleCounter
                }
                //第二次心跳
                if (secondBeat) {
                    secondBeat = false
                    for (j in 0..9) {
                        rate[j] = IBI
                    }
                }
                //第一次心跳
                if (firstBeat) {
                    firstBeat = false
                    secondBeat = true
                    continue
                }


                //通过10次均值计算心率
                var runningTotal: Long = 0
                var result: Float
                for (j in 0..8) {
                    rate[j] = rate[j + 1]
                    runningTotal = (runningTotal + rate[j]).toLong()
                }
                rate[9] = IBI
                runningTotal = (runningTotal + rate[9]).toLong()
                result = (runningTotal / 10.0).toFloat()
                result = 60000 / result
                floatList.add(result)
            }

            //上升结束、更新波峰波谷
            if (tmp < thresh && Pulse) {
                Pulse = false
                tmp = (P - T).toFloat()
                thresh = (tmp / 2 + T).toInt()
                P = thresh
                T = thresh
            }

            //长期未监测到心跳，重新初始化
            if (N > 2500) {
                initData()
                isValid = false
            }
        }
        checkData()
        return floatList
    }


}