package com.example.phychosiolz.utils

import android.content.Context
import android.util.Log
import com.chaquo.python.PyObject
import com.chaquo.python.Python
import com.chaquo.python.android.AndroidPlatform
import com.example.phychosiolz.MyApplication
import com.example.phychosiolz.data.enums.EmotionType
import kotlin.reflect.KFunction0
object ChaquopyUtil {
    private lateinit var instance: Python
    private lateinit var emotionModel: PyObject
    private lateinit var depressionAttackModel: PyObject
    private lateinit var depressionFineTuneModel: PyObject
    fun setup(context: Context) {
        if (Python.isStarted()) return
        Python.start(AndroidPlatform(context))
        instance = Python.getInstance()
        emotionModel = instance.getModule("emotion_run")
        depressionFineTuneModel = instance.getModule("fine_tune")
        depressionAttackModel = instance.getModule("depression_attack_run")
    }

    //一次要使用2s的数据
    fun runEmotionRecognition(path: String): EmotionType {
        //labels_name = ['0', '1', '2', '3', '4', '5']
        //labels_name = ['neutral', 'sad', 'happy', 'anger', 'disgust', 'fear']
        val res = emotionModel.callAttr("run", path)
        try {
            Log.d("ChaquopyUtil", "res=$res")
            return when (res.toJava(Int::class.javaObjectType)) {
                0 -> EmotionType.NEUTRAL
                1 -> EmotionType.SAD
                2 -> EmotionType.HAPPY
                3 -> EmotionType.ANGRY
                4 -> EmotionType.SICK
                5 -> EmotionType.SCARED
                else -> {
                    Log.e("ChaquopyUtil", "index out of range")
                    EmotionType.NEUTRAL
                }
            }
        } catch (e: Exception) {
            Log.e("ChaquopyUtil", "e=$res")
            return EmotionType.NEUTRAL
        }
    }

    //参数未定
    fun runDepressionAttackRecognition(doCallback: KFunction0<Unit>,dataDirPath: String,modelPath: String) {
        val resPy =depressionAttackModel.callAttr("run", dataDirPath, modelPath)
        val res = Integer.parseInt(resPy.toJava(String::class.javaObjectType))
        Log.d("ChaquopyUtil", "res=$res")
        if (res == 1) {
            //回调
            doCallback()
        }
    }

    /***
     *进行模型的迁移
     *@param dataPath 数据文件夹路径
     * @param newModelPath 新模型存放路径
     */

    fun runDepressionRecognitionMigration(dataPath: String, newModelPath: String) {
        depressionFineTuneModel.callAttr("fine_tuning", "$dataPath/", newModelPath)
    }

}

