package com.example.phychosiolz.view_model

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.example.phychosiolz.MyApplication
import com.example.phychosiolz.R
import com.example.phychosiolz.data.room.model.User
import com.example.phychosiolz.model.UserExistInfo
import com.example.phychosiolz.model.UserLoginInfo
import com.example.phychosiolz.repo.ManagerRepository
import com.example.phychosiolz.repo.UserRepository
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ManagerLoginViewModel(private val _repository: ManagerRepository) : ViewModel() {
    private val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        Log.e("LoginAndRegisterViewModel", "doRegister: ${throwable.message}")
    }

    fun checkLogin(it: (Unit) -> Unit) {
        viewModelScope.launch(exceptionHandler) {
            val res = _repository.readLastLogin()
            if (res) {
                it(Unit)
            }
        }
    }

    fun login(name: String,number:String, action: (Unit) -> Unit) {
        viewModelScope.launch(exceptionHandler) {
            try {
                withContext(Dispatchers.IO){
                    _repository.login(name,number)
                }
                action(Unit)
            } catch (e: Exception) {
                Log.e("LoginAndRegisterViewModel", "login: ${e.message}")
            }
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(
                modelClass: Class<T>, extras: CreationExtras
            ): T {
                val application = checkNotNull(extras[APPLICATION_KEY])
                return ManagerLoginViewModel(
                    (application as MyApplication).managerRepository
                ) as T
            }
        }
    }
}