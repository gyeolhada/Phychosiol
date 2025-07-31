package com.example.phychosiolz.repo

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.example.phychosiolz.data.room.dao.DiaryDao
import com.example.phychosiolz.data.room.dao.EmotionDao
import com.example.phychosiolz.data.room.model.Diary
import com.example.phychosiolz.data.room.model.Emotion
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import java.text.DateFormat
import java.util.Calendar
import java.util.Date

class EmotionAndDiaryRepository(
    private val _diaryDao: DiaryDao,
    private val _userRepo: UserRepository,
    private val _emotionDao: EmotionDao,
    private val defaultDispatcher: CoroutineDispatcher = Dispatchers.Default
) {
    val currentDairy = MutableLiveData<Diary?>()

    suspend fun saveDiary(title: String, content: String, code: Int, images: List<String>) {
        val uid = _userRepo.getCurrentUserID()!!
        val time = DateFormat.getDateTimeInstance().format(Date())
        val diary = Diary(
            null, title, content, Gson().toJson(images), false, time, code, uid
        )
        withContext(Dispatchers.IO) {
            _diaryDao.insert(diary)
        }
    }

    suspend fun deleteDiaryById() {
        withContext(Dispatchers.IO) {
            _diaryDao.deleteByDid(currentDairy.value!!.did!!)
        }
    }

    fun getCurrentUserAvatar(): String? {
        return _userRepo.loginedUser.value?.uavatar
    }

    val localDiaryFlow: Flow<List<Diary>> =
        _diaryDao.getAllDialogByUid(_userRepo.getCurrentUserID()!!)

    //旧代码
//    fun getEmotionPieChartData(): List<PieEntry> {
//        val uid = _userRepo.getCurrentUserID()!!
//        val emotionList = _emotionDao.getAllEmotionDataByUid(uid)
//        val totalEmotions = emotionList.size.toFloat()
//        return emotionList.map { entry ->
//            val percentage = entry.getPercentage(totalEmotions)
//            PieEntry(percentage, entry.emotionType)
//        }
//    }

    //新代码 这一层为资源层，原则上负责资源获取即可
    fun getEmotionDataFlowInDay(year: Int, month: Int, day: Int): List<Emotion> =
        _emotionDao.getEmotionDataInDayByUid(_userRepo.getCurrentUserID()!!, year, month, day)

    fun getDiaryInDay(it: Calendar): List<Diary>? {
        val uid = _userRepo.getCurrentUserID()!!
        val date = DateFormat.getDateInstance().format(it.time)
        Log.i("EmotionAndDiaryRepository", "getDiaryInDay: $date")
        return _diaryDao.getDiaryInDayByUid(uid, date).let {
            Log.d("EmotionAndDiaryRepository", "getDiaryInDay: $it")
            it
        }
    }
}