package com.example.phychosiolz.work_manager

import android.content.Context
import androidx.work.Data
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.phychosiolz.utils.ChaquopyUtil

class EmotionRecWorker(appContext: Context, workerParams: WorkerParameters):
    Worker(appContext, workerParams)  {
    override fun doWork(): Result {
        val path = inputData.getString("path")
        val res = ChaquopyUtil.runEmotionRecognition(path!!)
        Data.Builder()
            .putInt(RES_KEY, res.code)
            .build()
        return Result.success()
    }

    companion object{
        const val RES_KEY = "emo_res"
    }
}