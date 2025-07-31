package com.example.phychosiolz.utils.data_process

import com.example.phychosiolz.utils.IOUtil

object Spo2DataUtil {


    private const val FreqS = 25
    private const val MA4_SIZE = 4
    private const val BUFFER_SIZE = FreqS * 4

    //184个元素
    private val uch_spo2_table = intArrayOf(
        95,
        95,
        95,
        96,
        96,
        96,
        97,
        97,
        97,
        97,
        97,
        98,
        98,
        98,
        98,
        98,
        99,
        99,
        99,
        99,
        99,
        99,
        99,
        99,
        100,
        100,
        100,
        100,
        100,
        100,
        100,
        100,
        100,
        100,
        100,
        100,
        100,
        100,
        100,
        100,
        100,
        100,
        100,
        100,
        99,
        99,
        99,
        99,
        99,
        99,
        99,
        99,
        98,
        98,
        98,
        98,
        98,
        98,
        97,
        97,
        97,
        97,
        96,
        96,
        96,
        96,
        95,
        95,
        95,
        94,
        94,
        94,
        93,
        93,
        93,
        92,
        92,
        92,
        91,
        91,
        90,
        90,
        89,
        89,
        89,
        88,
        88,
        87,
        87,
        86,
        86,
        85,
        85,
        84,
        84,
        83,
        82,
        82,
        81,
        81,
        80,
        80,
        79,
        78,
        78,
        77,
        76,
        76,
        75,
        74,
        74,
        73,
        72,
        72,
        71,
        70,
        69,
        69,
        68,
        67,
        66,
        66,
        65,
        64,
        63,
        62,
        62,
        61,
        60,
        59,
        58,
        57,
        56,
        56,
        55,
        54,
        53,
        52,
        51,
        50,
        49,
        48,
        47,
        46,
        45,
        44,
        43,
        42,
        41,
        40,
        39,
        38,
        37,
        36,
        35,
        34,
        33,
        31,
        30,
        29,
        28,
        27,
        26,
        25,
        23,
        22,
        21,
        20,
        19,
        17,
        16,
        15,
        14,
        12,
        11,
        10,
        9,
        7,
        6,
        5,
        3,
        2,
        1
    )


    //缓存
    private lateinit var dataBufferIred: IntArray
    private lateinit var dataBufferIr: IntArray
    private lateinit var iredTotal: IntArray
    private lateinit var irTotal: IntArray
    private var bufferIndex = 0

    private lateinit var meanBuffer: FloatArray
    private var meanBufferIndex = 0
    private var meanCapacity = 0


    init{
        dataBufferIred = IntArray(1600)
        dataBufferIr = IntArray(1600)
        iredTotal = IntArray(100)
        irTotal = IntArray(100)
        meanCapacity = 5
        meanBuffer = FloatArray(5)
    }

    fun addEntry(maxData: ByteArray?,call:(Float,Float,Boolean)->Unit) {
        addData(maxData)
        if (isGetEntry()) {
            call(getEntry(), getMeanValue(), isValidData())
        }
    }
    fun addData(maxData: ByteArray?) {
        val Ired = ByteArray(40)
        val Ir = ByteArray(40)
        var tmp: Int
        System.arraycopy(maxData, 0, Ir, 0, 40)
        System.arraycopy(maxData, 40, Ired, 0, 40)
        var i = 0
        while (i < 40) {
            tmp = IOUtil.twoByteToIntReverse(Ir[i], Ir[i + 1], 16)
            dataBufferIr[bufferIndex] = tmp
            tmp = IOUtil.twoByteToIntReverse(Ired[i], Ired[i + 1], 16)
            dataBufferIred[bufferIndex] = tmp
            bufferIndex++
            i = i + 2
        }
    }

    //满足计算条件
    fun isGetEntry(): Boolean {
        return bufferIndex == 1600
    }

    //获取血氧值
    fun getEntry(): Float {
        val spo2Result: Float
        var tmp1: Int
        var tmp2: Int
        for (i in 0..99) {
            tmp1 = 0
            tmp2 = 0
            for (j in 0..15) {
                tmp1 += dataBufferIr[i * 16 + j]
                tmp2 += dataBufferIred[i * 16 + j]
            }
            irTotal[i] = tmp1 / 16
            iredTotal[i] = tmp2 / 16
        }
        spo2Result = handleSpo2(irTotal, iredTotal, 100)
        for (i in 200..1599) {
            dataBufferIr[i - 200] = dataBufferIr[i]
            dataBufferIred[i - 200] = dataBufferIred[i]
        }
        bufferIndex = 1400
        meanBuffer[meanBufferIndex % meanCapacity] = spo2Result
        meanBufferIndex++
        return spo2Result
    }

    //血氧局长你hi
    fun getMeanValue(): Float {
        val size = Math.min(meanBufferIndex, meanCapacity)
        var tmp = 0f
        var count = 0
        for (i in 0 until size) {
            if (meanBuffer[i] > 70) {
                tmp += meanBuffer[i]
                count++
            }
        }
        return if (count != 0) {
            tmp / count
        } else {
            (-99).toFloat()
        }
    }

    //异常判断
    fun isValidData(): Boolean {
        var count = 0
        for (i in 0 until meanCapacity) {
            if (meanBuffer[i] < 50) {
                count++
            }
        }
        //缓冲区中 过半为不理想数据
        return count <= 0.5 * meanCapacity
    }

    //处理算法
    fun handleSpo2(
        pun_ir_buffer: IntArray,
        pun_red_buffer: IntArray,
        n_ir_buffer_length: Int
    ): Float {
        //ir
        val an_x = IntArray(BUFFER_SIZE)
        //red
        val an_y = IntArray(BUFFER_SIZE)
        var un_ir_mean: Int
        var k: Int
        var n_i_ratio_count: Int
        var i: Int
        val n_exact_ir_valley_locs_count: Int
        val n_middle_idx: Int
        var n_th1: Int
        var n_npks = 0
        val an_ir_valley_locs: IntArray
        var n_peak_interval_sum: Int
        var n_y_ac: Int
        var n_x_ac: Int
        val n_spo2_calc: Int
        var n_y_dc_max: Int
        var n_x_dc_max: Int
        var n_y_dc_max_idx = 0
        var n_x_dc_max_idx = 0
        val an_ratio: IntArray
        var n_ratio_average: Int
        var n_nume: Int
        var n_denom: Int
        val pn_heart_rate: Int
        val pch_hr_valid: Int
        val pn_spo2: Int
        val pch_spo2_valid: Int
        an_ir_valley_locs = IntArray(15)
        an_ratio = IntArray(5)

        // calculates DC mean and subtract DC from ir
        un_ir_mean = 0
        k = 0
        while (k < n_ir_buffer_length) {
            un_ir_mean += pun_ir_buffer[k]
            k++
        }
        un_ir_mean = un_ir_mean / n_ir_buffer_length

        // remove DC and invert signal so that we can use peak detector as valley detector
        k = 0
        while (k < n_ir_buffer_length) {
            an_x[k] = -1 * (pun_ir_buffer[k] - un_ir_mean)
            k++
        }

        // 4 pt Moving Average
        k = 0
        while (k < BUFFER_SIZE - MA4_SIZE) {
            an_x[k] = (an_x[k] + an_x[k + 1] + an_x[k + 2] + an_x[k + 3]) / 4
            k++
        }
        // calculate threshold
        n_th1 = 0
        k = 0
        while (k < BUFFER_SIZE) {
            n_th1 += an_x[k]
            k++
        }
        n_th1 = n_th1 / BUFFER_SIZE
        // min allowed
        if (n_th1 < 30) {
            n_th1 = 30
        }
        // max allowed
        if (n_th1 > 60) {
            n_th1 = 60
        }
        k = 0
        while (k < 15) {
            an_ir_valley_locs[k] = 0
            k++
        }
        n_npks = maxim_find_peaks(
            an_ir_valley_locs,
            0,
            an_x,
            BUFFER_SIZE,
            n_th1,
            4,
            15
        ) //peak_height, peak_distance, max_num_peaks
        n_peak_interval_sum = 0
        if (n_npks >= 2) {
            k = 1
            while (k < n_npks) {
                n_peak_interval_sum += an_ir_valley_locs[k] - an_ir_valley_locs[k - 1]
                k++
            }
            n_peak_interval_sum = n_peak_interval_sum / (n_npks - 1)
            pn_heart_rate = (FreqS * 60 / n_peak_interval_sum)
            pch_hr_valid = 1
        } else {
            pn_heart_rate = -999 // unable to calculate because # of peaks are too small
            pch_hr_valid = 0
        }

        //  load raw value again for SPO2 calculation : RED(=y) and IR(=X)
        k = 0
        while (k < n_ir_buffer_length) {
            an_x[k] = pun_ir_buffer[k]
            an_y[k] = pun_red_buffer[k]
            k++
        }

        // find precise min near an_ir_valley_locs
        n_exact_ir_valley_locs_count = n_npks

        //using exact_ir_valley_locs , find ir-red DC andir-red AC for SPO2 calibration an_ratio
        //finding AC/DC maximum of raw
        n_ratio_average = 0
        n_i_ratio_count = 0
        k = 0
        while (k < 5) {
            an_ratio[k] = 0
            k++
        }
        k = 0
        while (k < n_exact_ir_valley_locs_count) {
            if (an_ir_valley_locs[k] > BUFFER_SIZE) {
                pn_spo2 = -99 // do not use SPO2 since valley loc is out of range
                pch_spo2_valid = 0
                return pn_spo2.toFloat()
            }
            k++
        }

        // find max between two valley locations
        // and use an_ratio betwen AC compoent of Ir & Red and DC compoent of Ir & Red for SPO2
        k = 0
        while (k < n_exact_ir_valley_locs_count - 1) {
            n_y_dc_max = -16777216
            n_x_dc_max = -16777216
            if (an_ir_valley_locs[k + 1] - an_ir_valley_locs[k] > 3) {
                i = an_ir_valley_locs[k]
                while (i < an_ir_valley_locs[k + 1]) {
                    if (an_x[i] > n_x_dc_max) {
                        n_x_dc_max = an_x[i]
                        n_x_dc_max_idx = i
                    }
                    if (an_y[i] > n_y_dc_max) {
                        n_y_dc_max = an_y[i]
                        n_y_dc_max_idx = i
                    }
                    i++
                }
                n_y_ac =
                    (an_y[an_ir_valley_locs[k + 1]] - an_y[an_ir_valley_locs[k]]) * (n_y_dc_max_idx - an_ir_valley_locs[k]) //red
                n_y_ac =
                    an_y[an_ir_valley_locs[k]] + n_y_ac / (an_ir_valley_locs[k + 1] - an_ir_valley_locs[k])
                n_y_ac = an_y[n_y_dc_max_idx] - n_y_ac // subracting linear DC compoenents from raw
                n_x_ac =
                    (an_x[an_ir_valley_locs[k + 1]] - an_x[an_ir_valley_locs[k]]) * (n_x_dc_max_idx - an_ir_valley_locs[k]) // ir
                n_x_ac =
                    an_x[an_ir_valley_locs[k]] + n_x_ac / (an_ir_valley_locs[k + 1] - an_ir_valley_locs[k])
                n_x_ac = an_x[n_x_dc_max_idx] - n_x_ac // subracting linear DC compoenents from raw
                n_nume = n_y_ac * n_x_dc_max shr 7 //prepare X100 to preserve floating value
                n_denom = n_x_ac * n_y_dc_max shr 7
                if (n_denom > 0 && n_i_ratio_count < 5 && n_nume != 0) {
                    an_ratio[n_i_ratio_count] =
                        n_nume * 100 / n_denom //formular is ( n_y_ac *n_x_dc_max) / ( n_x_ac *n_y_dc_max) ;
                    n_i_ratio_count++
                }
            }
            k++
        }
        // choose median value since PPG signal may varies from beat to beat
        maxim_sort_ascend(an_ratio, n_i_ratio_count)
        n_middle_idx = n_i_ratio_count / 2
        n_ratio_average =
            if (n_middle_idx > 1) (an_ratio[n_middle_idx - 1] + an_ratio[n_middle_idx]) / 2 // use median
            else an_ratio[n_middle_idx]
        if (n_ratio_average > 2 && n_ratio_average < 183) {
            n_spo2_calc = uch_spo2_table[n_ratio_average]
            pn_spo2 = n_spo2_calc
            pch_spo2_valid =
                1 //  float_SPO2 =  -45.060*n_ratio_average* n_ratio_average/10000 + 30.354 *n_ratio_average/100 + 94.845 ;  // for comparison with table
        } else {
            pn_spo2 = -99 // do not use SPO2 since signal an_ratio is out of range
            pch_spo2_valid = 0
        }
        return pn_spo2.toFloat()
    }

    //在最小高度以上找到最多最大峰值，间隔至少为最小距离
    //
    fun maxim_find_peaks(
        pn_locs: IntArray,
        n_npks: Int,
        pn_x: IntArray,
        n_size: Int,
        n_min_height: Int,
        n_min_distance: Int,
        n_max_num: Int
    ): Int {
        var tmp = maxim_peaks_above_min_height(pn_locs, n_npks, pn_x, n_size, n_min_height)
        tmp = maxim_remove_close_peaks(pn_locs, tmp, pn_x, n_min_distance)
        tmp = Math.min(tmp, n_max_num)
        return tmp
    }

    //找到最小高度以上的所有峰值
    fun maxim_peaks_above_min_height(
        pn_locs: IntArray,
        n_npks: Int,
        pn_x: IntArray,
        n_size: Int,
        n_min_height: Int
    ): Int {
        var n_npks = n_npks
        var i = 1
        var n_width: Int
        n_npks = 0
        while (i < n_size - 1) {
            if (pn_x[i] > n_min_height && pn_x[i] > pn_x[i - 1]) {      // find left edge of potential peaks
                n_width = 1
                while (i + n_width < n_size && pn_x[i] == pn_x[i + n_width]) { // find flat peaks
                    n_width++
                }
                if (i + n_width < n_size && pn_x[i] > pn_x[i + n_width] && n_npks < 15) {      // find right edge of peaks
                    pn_locs[n_npks++] = i
                    // for flat peaks, peak location is left edge
                    i += n_width + 1
                } else {
                    i += n_width
                }
            } else {
                i++
            }
        }
        return n_npks
    }

    //去除相隔小于最小距离的峰值
    fun maxim_remove_close_peaks(
        pn_locs: IntArray,
        pn_npks: Int,
        pn_x: IntArray,
        n_min_distance: Int
    ): Int {
        var pn_npks = pn_npks
        var i: Int
        var j: Int
        var n_old_npks: Int
        var n_dist: Int

        /* Order peaks from large to small */maxim_sort_indices_descend(pn_x, pn_locs, pn_npks)
        i = -1
        while (i < pn_npks) {
            n_old_npks = pn_npks
            pn_npks = i + 1
            j = i + 1
            while (j < n_old_npks) {
                n_dist =
                    pn_locs[j] - if (i == -1) -1 else pn_locs[i] // lag-zero peak of autocorr is at index -1
                if (n_dist > n_min_distance || n_dist < -n_min_distance) pn_locs[pn_npks++] =
                    pn_locs[j]
                j++
            }
            i++
        }

        // Resort indices int32_to ascending order
        maxim_sort_ascend(pn_locs, pn_npks)
        return pn_npks
    }

    //升序 插排
    fun maxim_sort_ascend(arr: IntArray, n_size: Int) {
        var i: Int
        var j: Int
        var n_temp: Int
        i = 1
        while (i < n_size) {
            n_temp = arr[i]
            j = i
            while (j > 0 && n_temp < arr[j - 1]) {
                arr[j] = arr[j - 1]
                j--
            }
            arr[j] = n_temp
            i++
        }
    }

    //降序下标数组 插排
    fun maxim_sort_indices_descend(pn_x: IntArray, pn_indx: IntArray, n_size: Int) {
        var i: Int
        var j: Int
        var n_temp: Int
        i = 1
        while (i < n_size) {
            n_temp = pn_indx[i]
            j = i
            while (j > 0 && pn_x[n_temp] > pn_x[pn_indx[j - 1]]) {
                pn_indx[j] = pn_indx[j - 1]
                j--
            }
            pn_indx[j] = n_temp
            i++
        }
    }
}