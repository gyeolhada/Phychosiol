package com.example.phychosiolz.view_model

import android.util.Log
import androidx.core.text.isDigitsOnly
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.example.phychosiolz.MyApplication
import com.example.phychosiolz.model.UserLoginInfo
import com.example.phychosiolz.repo.UserRepository
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MineEditViewModel(private val _repository: UserRepository) : ViewModel() {
    val currentUser by lazy {
        MutableLiveData<UserLoginInfo>()
    }//当前页面的用户数据

    init {
        //clone
        currentUser.value = _repository.loginedUser.value!!.clone()//初始化当前用户数据
    }

    //异常处理,处理协程中的异常
    private val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        Log.e("LoginAndRegisterViewModel", "doRegister: ${throwable.message}")
    }

    fun logout() {
        viewModelScope.launch(exceptionHandler) {
            //切换到主线程,保证清空数据后再跳转
            withContext(Dispatchers.Main) {
                _repository.logout()
            }
        }
    }

    fun deleteUser(action: (Unit) -> Unit) {
        viewModelScope.launch(exceptionHandler) {
            withContext(Dispatchers.Main) {
                _repository.deleteUser()
                action(Unit)
            }
        }
    }

    fun updateUserBirthday(it: String?) {
        currentUser.value = currentUser.value?.changeBirthday(it)
        Log.d("LoginAndRegisterViewModel", "updateUserBirthday: ${currentUser.value}")
    }

    fun exchangeSex() {
        currentUser.value = currentUser.value?.changeSex()
    }

    fun changeUname(toString: String) {
        currentUser.value = currentUser.value?.changeName(toString)
    }

    fun changePname(toString: String) {
        currentUser.value = currentUser.value?.changeRealName(toString)
    }

    fun changeHeight(toString: String) {
        currentUser.value =
            currentUser.value?.changeHeight(if (toString.isNotBlank() && toString.isDigitsOnly()) toString.toDouble() else null)
    }

    fun changeWeight(toString: String) {
        currentUser.value =
            currentUser.value?.changeWeight(if (toString.isNotBlank() && toString.isDigitsOnly()) toString.toDouble() else null)
    }

    fun updateUserInfo(action: () -> Unit) {
        _repository.updateUserInfo(currentUser.value!!)
        action()
    }

    fun changeAvatar(toString: String) {
        currentUser.value =
            currentUser.value?.changeAvatar(toString)
    }

    companion object {
        val Factory: ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(
                modelClass: Class<T>, extras: CreationExtras
            ): T {
                val application = checkNotNull(extras[APPLICATION_KEY])
                return MineEditViewModel(
                    (application as MyApplication).userRepository
                ) as T
            }
        }
    }
}