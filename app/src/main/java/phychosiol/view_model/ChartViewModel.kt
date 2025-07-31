package com.example.phychosiolz.view_model

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.example.phychosiolz.MyApplication
import com.example.phychosiolz.data.Graph
import com.example.phychosiolz.data.room.model.User
import com.example.phychosiolz.model.UserLoginInfo
import com.example.phychosiolz.repo.UserRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Calendar
import java.util.LinkedList

class ChartViewModel(private val userId: Int, _repository: UserRepository) : ViewModel() {
    private val dao by lazy { Graph.db.testdataDao() }
    val queryTime = MutableLiveData(Calendar.getInstance()) //查询的日期，初始化为当前日期
    val pointHeartData = MutableLiveData<LinkedList<Float>>()
    val pointTempData = MutableLiveData<LinkedList<Float>>()
    val pointSpO2Data = MutableLiveData<LinkedList<Float>>()
    val pointSkinData = MutableLiveData<LinkedList<Float>>()
    val page = MutableLiveData(0)

    val currentuser = _repository.loginedUser
    val aveHeartData: MutableLiveData<Float> by lazy {
        MutableLiveData<Float>(0f)
    }
    val aveTempData: MutableLiveData<Float> by lazy {
        MutableLiveData<Float>(0f)
    }
    val aveSpO2Data: MutableLiveData<Float> by lazy {
        MutableLiveData<Float>(0f)
    }

    init {
        pointHeartData.value = LinkedList()
        pointTempData.value = LinkedList()
        pointSpO2Data.value = LinkedList()
        pointSkinData.value = LinkedList()
    }

    fun submitHeartData(data: Float) {
        pointHeartData.value = pointHeartData.value!!.let {
            it.offer(data)
            if (it.size >= QUEUE_SIZE) {
                it.poll()
            }
            it
        }
    }

    fun submitSpO2Data(data: Float) {
        pointSpO2Data.value = pointSpO2Data.value!!.let {
            it.offer(data)
            if (it.size >= QUEUE_SIZE) {
                it.poll()
            }
            it
        }
    }


    fun submitTempData(data: Float) {
        pointTempData.value = pointTempData.value!!.let {
            it.offer(data)
            if (it.size >= QUEUE_SIZE) {
                it.poll()
            }
            it
        }
    }

    fun submitSkinData(data: Float) {
        pointSkinData.value = pointSkinData.value!!.let {
            it.offer(data)
            if (it.size >= QUEUE_SIZE) {
                it.poll()
            }
            it
        }
    }


    fun getTestDataListInDay(type: Int) =
        dao.getTestDataListInDay(
            userId,
            queryTime.value!!.get(Calendar.YEAR),
            queryTime.value!!.get(Calendar.MONTH),
            queryTime.value!!.get(Calendar.DAY_OF_MONTH),
            type
        ).asLiveData().map {
            it.let {
                //组成48个点的数组，period缺失的点用0补齐
                val list = mutableListOf<Float>()
                for (i in 0..47) {
                    list.add(0f)
                }
                it?.forEach { testData ->
                    list[testData.period] = testData.avg
                }
                list
            }
        }

    fun submitAvgHeartData(entry: Float) {
        aveHeartData.value = entry
    }

    fun submitAvgTempData(entry: Float) {
        aveTempData.value = entry
    }

    fun submitAvgSpO2Data(entry: Float) {
        aveSpO2Data.value = entry
    }


    companion object {
        val Factory: ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(
                modelClass: Class<T>, extras: CreationExtras
            ): T {
                val application =
                    checkNotNull(extras[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY])
                return ChartViewModel(
                    (application as MyApplication).userRepository.loginedUser.value!!.uid!!,
                    application.userRepository
                ) as T
            }
        }
        const val QUEUE_SIZE = 60
    }
}
