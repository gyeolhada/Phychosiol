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

class QuestionnaireRepository(
    private val dataStoreRepo: DataStoreRepository,
) {

    val bdiScore: MutableLiveData<String?> = MutableLiveData<String?>()
    val gadScore: MutableLiveData<String?> = MutableLiveData<String?>()
    val phqScore: MutableLiveData<String?> = MutableLiveData<String?>()
    val sdsScore: MutableLiveData<String?> = MutableLiveData<String?>()
    val bdiTime: MutableLiveData<String?> = MutableLiveData<String?>()
    val bigTime: MutableLiveData<String?> = MutableLiveData<String?>()
    val epqTime: MutableLiveData<String?> = MutableLiveData<String?>()
    val gadTime: MutableLiveData<String?> = MutableLiveData<String?>()
    val panasTime: MutableLiveData<String?> = MutableLiveData<String?>()
    val panasxTime: MutableLiveData<String?> = MutableLiveData<String?>()
    val phqTime: MutableLiveData<String?> = MutableLiveData<String?>()
    val sdsTime: MutableLiveData<String?> = MutableLiveData<String?>()
    val staisTime: MutableLiveData<String?> = MutableLiveData<String?>()

    suspend fun subQuestionnaire(uid: String, tag: String, value: String) {
        val key= "$uid $tag"
        when(tag){
            BDIII-> {
                bdiScore.postValue(value.split(" ")[0])
                bdiTime.postValue("上次测试时间 "+value.split(" ")[1])
            }
            GAD7-> {
                gadScore.postValue(value.split(" ")[0])
                gadTime.postValue("上次测试时间 "+value.split(" ")[1])
            }
            PHQ9->{
                phqScore.postValue(value.split(" ")[0])
                phqTime.postValue("上次测试时间 "+value.split(" ")[1])
            }
            SDS->{
                sdsScore.postValue(value.split(" ")[0])
                sdsTime.postValue("上次测试时间 "+value.split(" ")[1])
            }
            BIG5->{bigTime.postValue("上次测试时间 $value")}
            EPQ->{epqTime.postValue("上次测试时间 $value")}
            PANAS->{panasTime.postValue("上次测试时间 $value")}
            PANASX->{panasxTime.postValue("上次测试时间 $value")}
            STAIS->{staisTime.postValue("上次测试时间 $value")}
        }
        withContext(Dispatchers.IO) {
            dataStoreRepo.writeString2DataStore(key, value)
        }
    }

    suspend fun login(uid: String) {
        suspend fun getScoreData(scoreType: String, defaultValue: String): String {
            val data = dataStoreRepo.readStringFromDataStore("$uid $scoreType").first()
            return if (data == "") defaultValue else data!!.split(" ").get(0)
        }

        suspend fun getTimeData(timeType: String, defaultValue: String): String {
            val data = dataStoreRepo.readStringFromDataStore("$uid $timeType").first()
            return if (data == "") defaultValue else "上次测试时间 " + data!!.split(" ").get(1)!!
        }

        withContext(Dispatchers.IO) {
            bdiScore.postValue(getScoreData(BDIII, "-"))
            gadScore.postValue(getScoreData(GAD7, "-"))
            phqScore.postValue(getScoreData(PHQ9, "-"))
            sdsScore.postValue(getScoreData(SDS, "-"))

            bdiTime.postValue(getTimeData(BDIII, "未测试"))
            bigTime.postValue(getTimeData(BIG5, "未测试"))
            epqTime.postValue(getTimeData(EPQ, "未测试"))
            gadTime.postValue(getTimeData(GAD7, "未测试"))
            panasTime.postValue(getTimeData(PANAS, "未测试"))
            panasxTime.postValue(getTimeData(PANASX, "未测试"))
            phqTime.postValue(getTimeData(PHQ9, "未测试"))
            sdsTime.postValue(getTimeData(SDS, "未测试"))
            staisTime.postValue(getTimeData(STAIS, "未测试"))
        }
    }

    companion object {
        const val PHQ9 = "PHQ-9"
        const val GAD7 = "GAD-7"
        const val BDIII = "BDI-II"
        const val STAIS = "STAIS"
        const val SDS = "SDS"

        //性格问卷
        const val PANAS = "PANAS"
        const val PANASX = "PANAX"

        //情绪问卷
        const val BIG5 = "BIG5"
        const val EPQ = "EPQ"

        //空问卷 异常
        const val NONE = "NONE"
    }
}

