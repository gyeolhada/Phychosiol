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

class UserRepository(
    private val _userDao: UserDao,
    private val _personDao: PersonDao,
    private val dataStoreRepo: DataStoreRepository,
    private val defaultDispatcher: CoroutineDispatcher = Dispatchers.Default
) {
    val usersFlow: Flow<List<UserExistInfo>> = _userDao.getAllUsers().map {
        it.map { user ->
            UserExistInfo(
                user.uid,
                user.uname,
                user.usex,
                user.uavatar,
                dataStoreRepo.readStringFromDataStore(LAST_LOGIN + user.uid).first()
            )
        }
    }


    val loginedUser: MutableLiveData<UserLoginInfo?> by lazy {
        MutableLiveData<UserLoginInfo?>()
    }//当前app登录的用户的用户信息

    fun getCurrentUserID(): Int? {
        return loginedUser.value?.uid
    }

    suspend fun register(user: User) {
        _userDao.insert(user)
    }

    suspend fun login(name: String): UserLoginInfo? {
        //查询用户信息
        val user = _userDao.getUserByUsername(name)
            ?: //用户不存在
            return null
        //查询用户的个人信息
        val person = _personDao.getPersonByUid(user.uid)

        //写入DataStore
        //time key
        val tkey = LAST_LOGIN + user.uid
        // key=last_login_uid,value=当前时间(yyyy-MM-dd HH:mm:ss)
        withContext(Dispatchers.IO) {
            //yyyy-MM-dd HH:mm:ss
            val time = TEXT_PREFIX + DateFormat.getDateTimeInstance().format(Date()).map {
                //change ' ' to '_'
                if (it == ' ') '\n' else it
            }.joinToString("")
            dataStoreRepo.writeString2DataStore(tkey, time)
            // user key
            dataStoreRepo.writeString2DataStore(LAST_LOGIN, user.uid.toString())
        }
        return UserLoginInfo(
            user.uid,
            person?.pid,
            user.uname,
            user.usex,
            user.uavatar,
            person?.pname,
            person?.pbitrh,
            person?.pheight,
            person?.pweight
        )
    }

    suspend fun readLastLogin(): UserLoginInfo? {
        var uid = ""
        var user: User? = null
        var person: Person? = null
        withContext(Dispatchers.IO) {
            uid = dataStoreRepo.readStringFromDataStore(LAST_LOGIN).first()
                ?: ""//读取最后登录的uid
        }
        if (uid == "") {
            return null//没有登录过,不需要做任何操作
        }
        withContext(Dispatchers.IO) {
            user = _userDao.getUserByUid(uid.toInt())//查询用户信息
        }
        if (user == null)
            return null
        //查询用户的个人信息
        withContext(Dispatchers.IO) {
            person = _personDao.getPersonByUid(user!!.uid)
        }
        return UserLoginInfo(
            user!!.uid,
            person?.pid,
            user!!.uname,
            user!!.usex,
            user!!.uavatar,
            person?.pname,
            person?.pbitrh,
            person?.pheight,
            person?.pweight
        )
    }

    suspend fun logout() {
        dataStoreRepo.writeString2DataStore(LAST_LOGIN, "")
        loginedUser.value = null
    }

    suspend fun deleteUser() {
        withContext(Dispatchers.IO) {
            _userDao.delete(loginedUser.value!!.uid!!)
            _personDao.deleteByUid(loginedUser.value!!.uid!!)
        }
    }

    /**
     * 检查用户名是否存在
     * if true,用户名不存在
     * if false,用户名已存在
     */
    fun checkUserExist(username: String): Boolean {
        val user = _userDao.getUserByUsername(username) == null
        Log.d("LoginRepository", "checkUserExist: $user")
        return user
    }

    fun updateUserInfo(
        user: UserLoginInfo
    ) {
        //更新用户信息

        loginedUser.value = user
        val u = User(
            user.uid,
            user.uname,
            user.usex,
            user.uavatar
        )
        Log.d("UserRepository", "updateUserInfo: $u")
        _userDao.updateUser(u)
        //更新个人信息
        val p = Person(
            user.pid,
            user.pname,
            user.pbirthday,
            user.usex,
            user.pheight,
            user.pweight,
            user.uid
        )
        if (user.pid == null) {
            _personDao.insert(p)
            val pid = _personDao.getPersonByUid(user.uid!!)?.pid

            loginedUser.value = loginedUser.value?.let {
                it.pid = pid
                it
            }

        } else {
            _personDao.updatePerson(p)
        }


    }

    companion object {
        private const val LAST_LOGIN = "last_login_"
        private const val TEXT_PREFIX = "上一次登录\n"
    }
}

