package com.example.phychosiolz.repo

import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.lifecycle.MutableLiveData
import com.example.phychosiolz.data.room.dao.PersonDao
import com.example.phychosiolz.data.room.dao.UserDao
import com.example.phychosiolz.data.room.model.Person
import com.example.phychosiolz.data.room.model.User
import com.example.phychosiolz.model.UserExistInfo
import com.example.phychosiolz.model.UserLoginInfo
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.io.IOException
import java.text.DateFormat
import java.util.Date

class ManagerRepository(
    private val dataStoreRepo: DataStoreRepository,
) {

    val loginedManagerName: MutableLiveData<String?> by lazy {
        MutableLiveData<String?>()
    }//当前app登录的管理员信息
    val loginedManagerNum: MutableLiveData<String?> by lazy {
        MutableLiveData<String?>()
    }//当前app登录的管理员信息

    val isManagerListeningOn: MutableLiveData<Boolean> by lazy {
        MutableLiveData<Boolean>(false)
    }//当前app登录的管理员信息



    suspend fun login(name: String, number: String) {
        //写入DataStore
        withContext(Dispatchers.IO) {
            dataStoreRepo. writeString2DataStore(LAST_LOGIN_NAME, name)
            dataStoreRepo.writeString2DataStore(LAST_LOGIN_NUM, number)
        }
        withContext(Dispatchers.Main){
        loginedManagerName.value = name
        loginedManagerNum.value = number}
    }

    suspend fun readLastLogin(): Boolean {
        var name = ""
        var num = ""
        withContext(Dispatchers.IO) {
            name = dataStoreRepo.readStringFromDataStore(LAST_LOGIN_NAME).first()
                ?: ""//读取最后登录的管理员姓名
        }
        if (name == "") {
            return false//没有登录过,不需要做任何操作
        }
        withContext(Dispatchers.IO) {
            num = dataStoreRepo.readStringFromDataStore(LAST_LOGIN_NUM).first()
                ?: ""
        }
        withContext(Dispatchers.Main){
            loginedManagerName.value = name
            loginedManagerNum.value = num}
        return true
    }

    suspend fun logout() {
        loginedManagerNum.value = null
        loginedManagerName.value = null
        withContext(Dispatchers.IO) {
            dataStoreRepo.writeString2DataStore(LAST_LOGIN_NAME, "")
            dataStoreRepo.writeString2DataStore(LAST_LOGIN_NUM, "")
        }
    }

    fun switchListeningState() {
        isManagerListeningOn.value = isManagerListeningOn.value!!.not()
    }

    companion object {
        private const val LAST_LOGIN_NAME = "manager_last_login_name"
        private const val LAST_LOGIN_NUM = "manager_last_login_num"
    }
}

