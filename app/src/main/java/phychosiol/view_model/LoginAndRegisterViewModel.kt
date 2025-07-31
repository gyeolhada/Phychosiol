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
import com.example.phychosiolz.repo.QuestionnaireRepository
import com.example.phychosiolz.repo.UserRepository
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Calendar


class LoginAndRegisterViewModel(
    private val _repository: UserRepository,
    private val _questionnaireRepository: QuestionnaireRepository
) : ViewModel() {
    val usersFlow: LiveData<List<UserExistInfo>> = _repository.usersFlow.asLiveData()//所有用户的信息

    val newUsername = MutableLiveData<String>() //新用户的用户名
    val newUserSex = MutableLiveData<String>() //新用户的性别
    val registerResult by lazy { MutableLiveData<LoginResult>() }//注册结果

    val currentUser by lazy { _repository.loginedUser }//当前用户

    val bdiScore by lazy { _questionnaireRepository.bdiScore }
    val gadScore by lazy { _questionnaireRepository.gadScore }
    val phqScore by lazy { _questionnaireRepository.phqScore }
    val sdsScore by lazy { _questionnaireRepository.sdsScore }

    val bdiTime by lazy { _questionnaireRepository.bdiTime }
    val bigTime by lazy { _questionnaireRepository.bigTime }
    val epqTime by lazy { _questionnaireRepository.epqTime }
    val gadTime by lazy { _questionnaireRepository.gadTime }
    val panasTime by lazy { _questionnaireRepository.panasTime }
    val panasxTime by lazy { _questionnaireRepository.panasxTime }
    val phqTime by lazy { _questionnaireRepository.phqTime }
    val sdsTime by lazy { _questionnaireRepository.sdsTime }
    val staisTime by lazy { _questionnaireRepository.staisTime }

    val uid by lazy{_repository.getCurrentUserID().toString()}

    init {
        registerResult.value = LoginResult.WAITING
        newUserSex.value = "男"
    }


    //异常处理,处理协程中的异常
    private val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        registerResult.value = LoginResult.FAILURE
        Log.e("LoginAndRegisterViewModel", "doRegister: ${throwable.message}")
    }


    fun doRegister() {
        viewModelScope.launch(exceptionHandler) {
            if (newUsername.value.isNullOrBlank()) {
                registerResult.value = LoginResult.BLANK
                return@launch
            }
            if (newUsername.value!!.length < 2) {
                registerResult.value = LoginResult.TOO_SHORT
                return@launch
            }
            if (newUsername.value!!.length > 10) {
                registerResult.value = LoginResult.TOO_LONG
                return@launch
            }
            _repository.checkUserExist(newUsername.value!!).let {
                if (!it) {
                    registerResult.value = LoginResult.EXISTED
                    return@launch
                }
            }
            val user = User(
                null,
                newUsername.value,
                newUserSex.value,
                if (newUserSex.value == "男") R.drawable.man.toString()
                else R.drawable.woman.toString()
            )
            _repository.register(user)
            val res = _repository.login(newUsername.value!!)
                ?: throw Exception("注册成功,但是登录失败")
            currentUser.postValue(res)
            registerResult.value = LoginResult.SUCCESS
        }
    }

    fun login(it: User, action: (Unit) -> Unit) {
        viewModelScope.launch(exceptionHandler) {
            try {
                val res = _repository.login(it.uname!!)
                withContext(Dispatchers.IO) {
                    currentUser.postValue(res)
                }
                _questionnaireRepository.login(res!!.uid.toString())
                action(Unit)
                Log.d("LoginAndRegisterViewModel", "login: ${currentUser.value}")
            } catch (e: Exception) {
                Log.e("LoginAndRegisterViewModel", "login: ${e.message}")
            }
        }
    }

    fun checkLogin(it: (Unit) -> Unit) {
        viewModelScope.launch(exceptionHandler) {
            val res = _repository.readLastLogin()
            Log.d("Login", "VM:checkLogin: $res")
            if (res != null && !res.uname.isNullOrBlank()) {
                currentUser.postValue(res)
                _questionnaireRepository.login(res!!.uid.toString())
                it(Unit)
            }
        }
    }

    fun logout(action: (Unit) -> Unit) {
        viewModelScope.launch(exceptionHandler) {
            //切换到主线程,保证清空数据后再跳转
            withContext(Dispatchers.Main) {
                _repository.logout()
                action(Unit)
            }
        }
    }

    fun subQuestionnaire(uid: String, tag: String, value: String) {
        viewModelScope.launch(exceptionHandler) {
            _questionnaireRepository.subQuestionnaire(uid, tag, value)
        }
        Log.i("subQuestionnaire", "${bdiScore.value.toString()}")
    }

    fun getAge(it: String): String {
        val birth = it.split("-")
        val year = birth[0].toInt()
        val month = birth[1].toInt()
        val day = birth[2].toInt()
        val now = Calendar.getInstance()
        val nowYear = now.get(Calendar.YEAR)
        val nowMonth = now.get(Calendar.MONTH) + 1
        val nowDay = now.get(Calendar.DAY_OF_MONTH)
        var age = nowYear - year
        if (nowMonth < month || (nowMonth == month && nowDay < day)) {
            age--
        }
        return age.toString()
    }

    fun getCurrentUserId():String{
        return uid;
    }

    companion object {
        val Factory: ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(
                modelClass: Class<T>, extras: CreationExtras
            ): T {
                val application = checkNotNull(extras[APPLICATION_KEY])
                return LoginAndRegisterViewModel(
                    (application as MyApplication).userRepository,
                    application.questionnaireRepository
                ) as T
            }
        }
    }

    enum class LoginResult {
        SUCCESS, FAILURE, BLANK, EXISTED, TOO_SHORT, TOO_LONG, WAITING
    }
}
