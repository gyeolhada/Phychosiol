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
import com.example.phychosiolz.network.ManagerNetworkController
import com.example.phychosiolz.repo.UserGraphDataRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.Callback
import okio.IOException
import java.util.Calendar
import java.util.LinkedList

class OthersOverviewViewModel(val repo: UserGraphDataRepository) : ViewModel() {

    val pointHeartData = MutableLiveData<LinkedList<Float>>()
    val pointTempData = MutableLiveData<LinkedList<Float>>()
    val pointSpO2Data = MutableLiveData<LinkedList<Float>>()
    val pointSkinData = MutableLiveData<LinkedList<Float>>()

    init {
        pointHeartData.value = LinkedList()
        pointTempData.value = LinkedList()
        pointSpO2Data.value = LinkedList()
        pointSkinData.value=LinkedList()
    }

    fun submitHeartData(data: Float) {
        pointHeartData.value?.let {
            it.offer(data)
            if (it.size >= ChartViewModel.QUEUE_SIZE) {
                it.poll()
            }
        }
//        Log.i("ChartViewModel", "submitHeartData: ${pointHeartData.value!!.size}")
        pointHeartData.value = pointHeartData.value
    }

    fun submitSpO2Data(data: Float) {
        pointSpO2Data.value?.let {
            it.offer(data)
            if (it.size >= ChartViewModel.QUEUE_SIZE) {
                it.poll()
            }
        }
//        Log.i("ChartViewModel", "submitSpO2Data: ${pointSpO2Data.value!!.size}")
        pointSpO2Data.value = pointSpO2Data.value
    }


    fun submitTempData(data: Float) {
        pointTempData.value?.let {
            it.offer(data)
            if (it.size >= ChartViewModel.QUEUE_SIZE) {
                it.poll()
            }
        }
//        Log.i("ChartViewModel", "submitTempData: ${pointTempData.value!!.size}")
        pointTempData.value = pointTempData.value
    }
    fun submitSkinData(data: Float) {
        pointSkinData.value?.let {
            it.offer(data)
            if (it.size >= ChartViewModel.QUEUE_SIZE) {
                it.poll()
            }
        }
//        Log.i("ChartViewModel", "submitTempData: ${pointTempData.value!!.size}")
        pointSkinData.value = pointSkinData.value
    }


    val recGraphPack = repo.userGraphPackFlow.asLiveData()//接收到的数据包,接受到就会更新
    //重新请求
    init {
        viewModelScope.launch(Dispatchers.IO) {
            while (isActive) {
                ManagerNetworkController.keepAlive(object : Callback {
                    override fun onFailure(call: okhttp3.Call, e: IOException) {
                        Log.d("OthersOverviewFragment", "KEEP ALIVE onFailure: ")
                    }

                    override fun onResponse(call: okhttp3.Call, response: okhttp3.Response) {
                        Log.d("OthersOverviewFragment", "KEEP ALIVE onResponse: ")
                    }
                })
                    Thread.sleep(4500)
            }
        }
    }


    companion object {
        val Factory: ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(
                modelClass: Class<T>, extras: CreationExtras
            ): T {
                val application =
                    checkNotNull(extras[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY])
                return OthersOverviewViewModel(
                    (application as MyApplication).userGraphDataRepository
                ) as T
            }
        }
    }
}