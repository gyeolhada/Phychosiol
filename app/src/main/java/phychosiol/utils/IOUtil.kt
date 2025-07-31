package com.example.phychosiolz.utils

import android.icu.text.DecimalFormat
import android.os.Environment
import android.util.Log
import com.example.phychosiolz.utils.data_process.HeartDataUtil
import com.example.phychosiolz.utils.data_process.SkinDataUtil
import com.example.phychosiolz.utils.data_process.Spo2DataUtil
import com.example.phychosiolz.utils.data_process.TemperatureDataUtil
import java.io.BufferedReader
import java.io.File
import java.io.FileReader
import java.io.FileWriter
import java.io.IOException
import java.text.DateFormat
import java.util.Calendar
import java.util.Date


object IOUtil {
    val JY901 = "/JY901.txt"//IMU,在情绪识别中不需要，为其他项目保留

    val Grove = "/Grove.txt"//皮肤电原始数据
    val Max30102 = "/Max30102.txt"//血氧原始数据
    val Pulse_sensor = "/Pulse_sensor.txt"//心率原始数据
    val LMT70 = "/LMT70.txt"//体温原始数据

    val DEP_ATT_FINE_TUNE = "/DepressionAttackFineTune"//抑郁发作的模型微调
    val DEP_ATT_FINE_TUNE_PEACE = "/peace"//抑郁发作的模型微调，平静数据（15s），为了保证数据量，采25s
    val DEP_ATT_FINE_TUNE_ATTACK = "/attack"//抑郁发作的模型微调，发作数据（15s），为了保证数据量，采25s
    val DEP_ATT_FINE_TUNE_CAT = "/cat"
    val DEP_ATT_FINE_TUNE_MODEL = "/new_model.pth"//新模型


    /***
     * 存放说明：
    |---uid
    |---|---2021-08-01
    |---|---|---Grove.txt
    |---|---|---Max30102.txt
    |---|---|---。。。
    |---|---DepressionAttackFineTune
    |---|---|---attack
    |---|---|---|---Grove.txt
    |---|---|---|---Max30102.txt
    |---|---|---peace
    |---|---|---|---Grove.txt
    |---|---|---|---Max30102.txt
    |---|---|---cat
    |---|---|---|---Grove.txt
    |---|---|---|---Max30102.txt
    |---|---|---new_model.pth
     *
     */

    private var ElcTotal = 0
    private var ElcCount = 0

    private val ROOT = (Environment.getExternalStorageDirectory().absolutePath) + "/PhychosiolZ/"

    //root/uid/time/type.txt
    private fun getFilePath(uid: String, type: String): String {
        //if dir not exist, create it
        try {
            var path = ROOT
            if (!File(path).exists()) File(path).mkdirs()
            path = "$path$uid"
            if (!File(path).exists()) File(path).mkdirs()
            //yyyy-MM-dd
            val now = Date()
            val dateFormat: DateFormat = DateFormat.getDateInstance(DateFormat.MEDIUM)
            path += "/" + dateFormat.format(now)
            if (!File(path).exists()) File(path).mkdirs()
            path = "$path$type"
//            val file = File(path)
//            if (!file.exists()) file.createNewFile()
            return path
        } catch (e: Exception) {
            Log.i("IOUtil", e.toString())
            return ""
        }
    }

    /***
     * 用于抑郁发作的模型微调
     * 写入时，根据数据类型调用这个函数
     * 格式：root/uid/DepressionAttackFineTune/attack/，，，.txt
     */
    private fun getFilePathForDepressAttackFineTuneData(
        uid: String, type: String?, status: String
    ): String {
        //if dir not exist, create it
        try {
            var path = ROOT
            if (!File(path).exists()) File(path).mkdirs()
            path = "$path$uid"
            if (!File(path).exists()) File(path).mkdirs()
            path += DEP_ATT_FINE_TUNE
            if (!File(path).exists()) File(path).mkdirs()
            path += status
            if (!File(path).exists()) File(path).mkdirs()
            if (type.isNullOrBlank()) {
                return path
            }
            path = "$path$type"
            return path
        } catch (e: Exception) {
            Log.i("IOUtil", e.toString())
            return ""
        }
    }

    /***
     * 拿attack和peace的数据进行拼接，放入cat文件夹
     */
    //Max30102采样率为400, LMT70采样率为100, Pluse_sensor采样率为400, Grove采样率为200
    fun catFineTuneData(uid:String):Boolean {
        //TODO 如果数据量不够，则拼接失败，将数据量不够的数据删除
        var numGrove = 200 * 15
        var numMax30102 = 400 * 15
        var numLMT70 = 100 * 15
        var numPulseSensor = 400 * 15

        val peacePath = getFilePathForDepressAttackFineTuneData(uid, "", DEP_ATT_FINE_TUNE_PEACE)
        val attackPath =
            getFilePathForDepressAttackFineTuneData(uid, "", DEP_ATT_FINE_TUNE_ATTACK)
        val catPath = getCatFineTuneDataPath(uid)
        var isSuccess = true

        val peaceValid = writeDirectoryContent(
            peacePath,
            numGrove,
            numMax30102,
            numLMT70,
            numPulseSensor,
            catPath
        )
        val attackValid = writeDirectoryContent(
            attackPath,
            numGrove,
            numMax30102,
            numLMT70,
            numPulseSensor,
            catPath
        )

        if (!peaceValid || !attackValid) {
            deleteFiles(peacePath)
            deleteFiles(attackPath)
            deleteFiles(catPath)
            isSuccess = false
        }
        return isSuccess
    }

    private fun writeDirectoryContent(
        path:String, numGrove: Int, numMax30102: Int, numLMT70: Int, numPulseSensor: Int,catPath:String
    ):Boolean{
        val directory = File(path)
        if(directory.exists()&&directory.isDirectory){
            val files = directory.listFiles()
            files!!.let{
                for(file in it){
                    val fileType = file.name.substringBefore(".")
                    when(fileType){
                        "Grove"->{if(!writeFileContent(file,numGrove,catPath))return false}
                        "Max30102"->{if(!writeFileContent(file,numMax30102,catPath))return false}
                        "LMT70"->{if(!writeFileContent(file,numLMT70,catPath))return false}
                        "Pulse_sensor"->{if(!writeFileContent(file,numPulseSensor,catPath))return false}
                    }
                }
            }
        }
        return true
    }

    private fun writeFileContent(file:File,limitNum:Int,catPath: String):Boolean{
        var bufferedReader : BufferedReader? = null
        try{
            bufferedReader = BufferedReader(FileReader(file))
            var count = 0
            var data:String?
            var catFile:File?=null
            val fileType = file.name.substringBefore(".")
            when(fileType){
                "Grove"->{catFile= File(catPath, "Grove.txt")}
                "Max30102"->{catFile= File(catPath, "Max30102.txt")}
                "LMT70"->{catFile= File(catPath, "LMT70.txt")}
                "Pulse_sensor"->{catFile= File(catPath, "Pulse_sensor.txt")}
            }
            if (catFile != null && !catFile.exists()) {
                catFile.createNewFile()
            }
            val fileWriter = FileWriter(catFile, true)
            while(bufferedReader.readLine().also{data = it.toString() }!=null && count < limitNum){
               fileWriter.write(data)
                fileWriter.write("\n")
                count++
            }
            fileWriter.close()
            return count >= limitNum
        }catch (e:Exception){
            e.printStackTrace()
            return false
        }finally {
            bufferedReader!!.close()
        }
    }

    private fun deleteFiles(path:String){
        val directory = File(path)
        if(directory.exists()&&directory.isDirectory){
            val files = directory.listFiles()
            files!!.let{
                for(file in it) file.delete()
            }
        }
    }
    /***
     * 获取拼接后的数据文件夹
     */
    fun getCatFineTuneDataPath(uid: String): String {
        return getFilePathForDepressAttackFineTuneData(uid, null, DEP_ATT_FINE_TUNE_CAT)
    }

    /***
     * 获取抑郁发作的模型微调的新模型路径，包含文件名
     */
    fun getDepressionAttackFineTuneModelPath(uid: String): String {
        return getFilePathForDepressAttackFineTuneData(uid, DEP_ATT_FINE_TUNE_MODEL, "")
    }
    fun handleData(
        data: ByteArray,
        uid: String,
        needToSave: Boolean,
        heartCall: (Float, Float, Boolean) -> Unit,//第一个参数是心电图的点，第二个参数是心率，第三个参数是是该数据是否合法
        tempCall: (Float, Float, Boolean) -> Unit,//第一个参数是平均体温，第二个参数是最高体温，第三个参数是该数据是否合法
        oxiCall: (Float, Float, Boolean) -> Unit,//第一个参数是图的点，第二个参数是平均血氧，第三个参数是该数据是否合法
        skinCall: (Float) -> Unit,//皮肤电
        deviceCall: (Int) -> Unit,
        saveForFineTune: Boolean,
        isDepressionAttack: Boolean
//        , refreshDeviceData: (Float,Float,Float,Float,Float,Float,Float,Boolean,Boolean,Boolean,Float,Int)->Unit
    ) {
        //去除报头 报尾
        val bytes = ByteArray(213)
        System.arraycopy(data, 4, bytes, 0, 213)

        //数据分割
        val IMU_DATA = ByteArray(60)
        val GSR_DATA = ByteArray(20)
        val MAX_DATA = ByteArray(80)
        val PLU_DATA = ByteArray(40)
        val LMT_DATA = ByteArray(10)
        val DEVICE_DATA = ByteArray(3)
        System.arraycopy(bytes, 0, IMU_DATA, 0, 60)
        System.arraycopy(bytes, 60, GSR_DATA, 0, 20)
        System.arraycopy(bytes, 80, MAX_DATA, 0, 80)
        System.arraycopy(bytes, 160, PLU_DATA, 0, 40)
        System.arraycopy(bytes, 200, LMT_DATA, 0, 10)
        System.arraycopy(bytes, 210, DEVICE_DATA, 0, 3)

        /**记录原始数据，研究组可能需要，**/
//        handleCommonData(GSR_DATA, Grove, uid)//皮肤电
//        handleMax30102Data(MAX_DATA, uid)//血氧
//        handleCommonData(PLU_DATA, Pulse_sensor, uid)//心率
//        handleCommonData(LMT_DATA, LMT70, uid)//体温
        /**记录即时数据**/
        if (needToSave) {
            handleCommonDataForRuntime(GSR_DATA, getRuntimeFilePath(uid, Grove))//皮肤电
            handleMax30102DataForRuntime(MAX_DATA, getRuntimeFilePath(uid, Max30102))//血氧
            handleCommonDataForRuntime(PLU_DATA, getRuntimeFilePath(uid, Pulse_sensor))//心率
            handleCommonDataForRuntime(LMT_DATA, getRuntimeFilePath(uid, LMT70))//体温
        } else {
            //不需要保存，直接删除
            val file = File(getRuntimeFilePath(uid, Grove))
            if (file.exists()) file.delete()
            val file1 = File(getRuntimeFilePath(uid, Max30102))
            if (file1.exists()) file1.delete()
            val file2 = File(getRuntimeFilePath(uid, Pulse_sensor))
            if (file2.exists()) file2.delete()
            val file3 = File(getRuntimeFilePath(uid, LMT70))
            if (file3.exists()) file3.delete()
        }
        /***记录微调数据**/
        if (saveForFineTune) {
            val status =
                if (isDepressionAttack) DEP_ATT_FINE_TUNE_ATTACK else DEP_ATT_FINE_TUNE_PEACE
            handleCommonDataForRuntime(
                GSR_DATA,
                getFilePathForDepressAttackFineTuneData(uid, Grove, status)
            )//皮肤电
            handleMax30102DataForRuntime(
                MAX_DATA,
                getFilePathForDepressAttackFineTuneData(uid, Max30102, status)
            )//血氧
            handleCommonDataForRuntime(
                PLU_DATA,
                getFilePathForDepressAttackFineTuneData(uid, Pulse_sensor, status)
            )//心率
            handleCommonDataForRuntime(
                LMT_DATA,
                getFilePathForDepressAttackFineTuneData(uid, LMT70, status)
            )//体温
        }

        handleEleAndMode(DEVICE_DATA, deviceCall)//电量和模式
        //处理成可以阅读的数据并回传
        HeartDataUtil.addEntry(PLU_DATA, heartCall)//心率
        TemperatureDataUtil.addEntry(LMT_DATA, tempCall)//体温
        Spo2DataUtil.addEntry(MAX_DATA, oxiCall)//血氧
        SkinDataUtil.addEntry(GSR_DATA, skinCall)//皮肤电
    }


    fun twoByteToIntReverse(b1: Byte, b2: Byte, i: Int): Int {
        var mask = 1 shl i
        mask -= 1
        return (oneByteToInt(b2) shl 8) + oneByteToInt(b1) and mask
    }

    private fun twoByteToInt(b1: Byte, b2: Byte, i: Int): Int {
        var mask = 1 shl i
        mask -= 1
        return (oneByteToInt(b1) shl 8) + oneByteToInt(b2) and mask
    }

    fun twoByteToSignIntReserve(b1: Byte, b2: Byte): Int {
        return b2.toInt() shl 8 or (b1.toInt() and 0xFF)
    }

    private fun oneByteToInt(b: Byte): Int {
        return 0xff and b.toInt()
    }

    private fun addLength(data: Int, length: Int): String? {
        val str = StringBuilder(data.toString() + "")
        for (i in str.length..length) {
            str.append(" ")
        }
        return str.toString()
    }

    private fun createFileWriter(dir: String?): FileWriter? {
        try {
            val file = File(dir)
            if (!file.exists()) {
                file.createNewFile()
            }
            return FileWriter(dir, true)
        } catch (e: IOException) {
            Log.i("IOUtil", e.toString())
        }
        return null
    }

    private fun handleTimeData(now: Calendar, fileWriter: FileWriter) {
        try {
            fileWriter.write(now[Calendar.YEAR].toString() + "  ")
            fileWriter.write(addLength(now[Calendar.MONTH] + 1, 2) + " ")
            fileWriter.write(addLength(now[Calendar.DAY_OF_MONTH], 2) + " ")
            fileWriter.write(addLength(now[Calendar.HOUR_OF_DAY], 2) + " ")
            fileWriter.write(addLength(now[Calendar.MINUTE], 2) + " ")
            fileWriter.write(addLength(now[Calendar.SECOND], 2) + " ")
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    private fun handleImuData(bytes: ByteArray, uid: String) {
        var path: String = getFilePath(uid, JY901)
        val decimalFormat = DecimalFormat("0.00")
        var tmp: Double
        try {
            val fileWriter = createFileWriter(path)
            for (i in 0..4) {
                val now: Calendar = Calendar.getInstance()
                handleTimeData(now, fileWriter!!)
                run {
                    var j = 0
                    while (j < 6) {
                        tmp = twoByteToSignIntReserve(
                            bytes[i * 12 + j], bytes[i * 12 + j + 1]
                        ) / (32768 * 1.0) * 16 * 9.8
                        fileWriter.write(decimalFormat.format(tmp) + "  ")
                        j += 2
                    }
                }
                var j = 6
                while (j < 12) {
                    tmp = twoByteToSignIntReserve(
                        bytes[i * 12 + j], bytes[i * 12 + j + 1]
                    ) / (32768 * 1.0) * 2000
                    fileWriter!!.write(decimalFormat.format(tmp) + "  ")
                    j += 2
                }
                fileWriter!!.write("\r\n")
            }
            fileWriter!!.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    private fun handleMax30102Data(bytes: ByteArray, uid: String) {
        var path: String = getFilePath(uid, Max30102)
        var tmp: Int
        try {
            val fileWriter = createFileWriter(path)
            for (i in 0..19) {
                val now = Calendar.getInstance()
                handleTimeData(now, fileWriter!!)
                tmp = twoByteToIntReverse(bytes[i * 2], bytes[i * 2 + 1], 16)
                fileWriter.write(addLength(tmp, 6))
                fileWriter.write("  ")
                tmp = twoByteToIntReverse(bytes[i * 2 + 40], bytes[i * 2 + 40 + 1], 16)
                fileWriter.write(addLength(tmp, 6))
                fileWriter.write("\r\n")
            }
            fileWriter!!.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    private fun handleCommonData(bytes: ByteArray, type: String, uid: String) {
        var path: String = getFilePath(uid, type)
        var tmp: Int
        try {
            val fileWriter = createFileWriter(path)
            var i = 0
            while (i < bytes.size) {
                val now = Calendar.getInstance()
                handleTimeData(
                    now, fileWriter!!
                )
                tmp = twoByteToIntReverse(bytes[i], bytes[i + 1], 12)
                fileWriter.write(addLength(tmp, 6) + " ")
                fileWriter.write("\r\n")
                i += 2
            }
            fileWriter!!.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    //即时数据,不需要时间戳，每个文件保存2s的数据
    private fun getRuntimeFilePath(uid: String, type: String): String {
        try {
            var path = ROOT
            if (!File(path).exists()) File(path).mkdirs()
            path = "$path$uid"
            if (!File(path).exists()) File(path).mkdirs()
            path = "$path$type"
            //如果存在，删除
            val file = File(path)
            return path
        } catch (e: Exception) {
            Log.i("IOUtil", e.toString())
            return ""
        }
    }

    //获取用户的文件夹路径，交给chaquopy
    fun getUserDir(uid: String): String {
        try {
            var path = ROOT
            if (!File(path).exists()) File(path).mkdirs()
            path = "$path$uid"
            if (!File(path).exists()) File(path).mkdirs()
            return "$path/"
        } catch (e: Exception) {
            Log.i("IOUtil", e.toString())
            return ""
        }
    }


    //用来进行识别的数据
    private fun handleMax30102DataForRuntime(bytes: ByteArray, path: String) {
        var tmp: Int
        try {
            val fileWriter = createFileWriter(path)
            for (i in 0..19) {
                val now = Calendar.getInstance()
                handleTimeData(now, fileWriter!!)
                tmp = twoByteToIntReverse(bytes[i * 2], bytes[i * 2 + 1], 16)
                fileWriter.write(addLength(tmp, 6))
                fileWriter.write("  ")
                tmp = twoByteToIntReverse(bytes[i * 2 + 40], bytes[i * 2 + 40 + 1], 16)
                fileWriter.write(addLength(tmp, 6))
                fileWriter.write("\r\n")
            }
            fileWriter!!.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    private fun handleCommonDataForRuntime(bytes: ByteArray, path: String) {
        var tmp: Int
        try {
            val fileWriter = createFileWriter(path)
            var i = 0
            while (i < bytes.size) {
                val now = Calendar.getInstance()
                handleTimeData(
                    now, fileWriter!!
                )
                tmp = twoByteToIntReverse(bytes[i], bytes[i + 1], 12)
                fileWriter.write(addLength(tmp, 6) + " ")
                fileWriter.write("\r\n")
                i += 2
            }
            fileWriter!!.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    private fun handleEleAndMode(data: ByteArray, action: (Int) -> Unit) {
        var tmp: Int = twoByteToInt(data[0], data[1], 12)
        val mode = oneByteToInt(data[2])
        ElcTotal += tmp
        ElcCount++
        //20组更新一次
        if (ElcCount == 40) {
            tmp = ((ElcTotal / (ElcCount * 1.0)).toInt())
            var tmpElc = (tmp * 3.3 / 4095 - 3.7 * 0.661157) / ((4.2 - 3.7) * 0.661157)
            ElcCount = 0
            ElcTotal = 0
            tmpElc *= (-100)
            action(tmpElc.toInt())
        }
    }

    fun checkDepressionAttackModelExist(uid: String): Boolean {
        val path = getDepressionAttackFineTuneModelPath(uid)
        val file = File(path)
        return file.exists()
    }

    fun checkDepressionAttackFineTuneAttackDataExist(uid: String): Boolean {
        val path = getFilePathForDepressAttackFineTuneData(uid, null, DEP_ATT_FINE_TUNE_ATTACK)
        // if dir is not exist, or dir is empty, return false
        val file = File(path)
        return file.exists() && file.listFiles()?.isNotEmpty() ?: false
    }

    fun checkDepressionAttackFineTunePeaceDataExist(uid: String): Boolean {
        val path = getFilePathForDepressAttackFineTuneData(uid, null, DEP_ATT_FINE_TUNE_PEACE)
        val file = File(path)
        return file.exists() && file.listFiles()?.isNotEmpty() ?: false
    }
}