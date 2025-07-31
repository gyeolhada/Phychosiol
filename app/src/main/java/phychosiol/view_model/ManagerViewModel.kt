package com.example.phychosiolz.view_model

import android.os.Handler
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.example.phychosiolz.MyApplication
import com.example.phychosiolz.model.ManagerTransInfo
import com.example.phychosiolz.network.ManagerNetworkController
import com.example.phychosiolz.repo.ManagerRepository
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch

class ManagerViewModel(private val _repository: ManagerRepository) : ViewModel() {
    val currentManagerName: LiveData<String?> = _repository.loginedManagerName
    val currentManagerNumber: LiveData<String?> = _repository.loginedManagerNum
    val isMangerListeningOn: LiveData<Boolean> = _repository.isManagerListeningOn

    private val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        Log.e("LoginAndRegisterViewModel", "doRegister: ${throwable.message}")
    }


    fun logout() {
        viewModelScope.launch(exceptionHandler) {
            _repository.logout()
        }
    }

    fun switchListeningState() {
        _repository.switchListeningState()
    }

    fun setListening(handler: Handler, it: Boolean) {
        if (it) {
            ManagerNetworkController.runListenForHelp(handler)
        } else {
            ManagerNetworkController.shutDown()
        }
    }

    fun doResponse(ip: String,
                   accept: (Unit) -> Unit,
                   reject: (String) -> Unit) {
        ManagerNetworkController.doctorResponse(
            ip,
            ManagerTransInfo(currentManagerName.value!!, currentManagerNumber.value!!),
            accept,reject
        )

    }

    companion object {
        val Factory: ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(
                modelClass: Class<T>, extras: CreationExtras
            ): T {
                val application = checkNotNull(extras[APPLICATION_KEY])
                return ManagerViewModel(
                    (application as MyApplication).managerRepository
                ) as T
            }
        }
    }
}