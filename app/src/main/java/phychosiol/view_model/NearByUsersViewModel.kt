package com.example.phychosiolz.view_model

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.example.phychosiolz.MyApplication
import com.example.phychosiolz.repo.UserUdpRepository
import com.example.phychosiolz.network.ManagerNetworkController
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class NearByUsersViewModel(private val repository: UserUdpRepository) : ViewModel() {
    fun setup(context: Context?) {
        viewModelScope.launch {
            withContext(Dispatchers.IO){
                ManagerNetworkController.isManagerScanOn = true
                ManagerNetworkController.runManager(context!!)
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        ManagerNetworkController.isManagerScanOn = false//关闭扫描
    }

    val broadcastResponseIPData = repository.broadcastResponseIPFlow.asLiveData()

    companion object {
        val Factory: ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(
                modelClass: Class<T>, extras: CreationExtras
            ): T {
                val application =
                    checkNotNull(extras[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY])
                return NearByUsersViewModel(
                    (application as MyApplication).userUdpRepository
                ) as T
            }
        }
    }
}