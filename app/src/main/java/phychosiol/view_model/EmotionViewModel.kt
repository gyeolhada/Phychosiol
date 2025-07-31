package com.example.phychosiolz.view_model

import android.content.Context
import android.graphics.Color
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.content.res.ResourcesCompat
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.example.phychosiolz.MyApplication
import com.example.phychosiolz.R
import com.example.phychosiolz.data.enums.EmotionType
import com.example.phychosiolz.data.room.model.Diary
import com.example.phychosiolz.repo.EmotionAndDiaryRepository
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.PieEntry
import kotlinx.coroutines.launch
import java.text.DateFormat
import java.util.Calendar
import java.util.Date

class EmotionViewModel(private val _repository: EmotionAndDiaryRepository) : ViewModel() {
    val localDiaryList = _repository.localDiaryFlow.asLiveData() // 本地日记列
    val localDiaryWithImagesList = localDiaryList.map {
        it.filter { diary -> diary.images!!.isNotBlank() }
    }
    val viewMode = MutableLiveData(ViewMode.LIST)
    val currentDairy = _repository.currentDairy

    private var happyPercentages = 0.0f
    private var angryPercentages = 0.0f
    private var sadPercentages = 0.0f
    private var sickPercentages = 0.0f
    private var scaredPercentages = 0.0f
    private var neutralPercentages = 0.0f

    // 设定一个代表被选中情绪的livedata，初始时/切换日期时让他的值为null,
    // 当其为空时，右侧的cl_emo的visible = GONE否则为visible
    val selectedEmotionEntry = MutableLiveData<Entry?>(null)
    val diaryInSelectedDay = MutableLiveData<List<Diary>>()

    //查询的日期，初始化为当前日期
    val queryTime = MutableLiveData(Calendar.getInstance())
    val emotionPieChartData = MutableLiveData<List<PieEntry>?>(null)
    fun submitDiary(it: Diary, action: (Unit) -> Unit) {
        currentDairy.postValue(it)
        action(Unit)
    }

    fun deleteById(action: (Unit) -> Unit) {
        viewModelScope.launch {
            _repository.deleteDiaryById()
            action(Unit)
        }
    }

    fun getUserAvatar(): String? {
        return _repository.getCurrentUserAvatar()
    }

    enum class ViewMode {
        LIST, GRAPH
    }

    //旧代码
//    private fun updateEmotionPieChartData() {
//        viewModelScope.launch {
//            val data = _repository.getEmotionPieChartData()
//            _emotionPieChartData.postValue(data)
//        }
//    }


    fun getColorForEmotionType(emotionType: EmotionType, context: Context): Int {
        return when (emotionType) {
            EmotionType.HAPPY -> ActivityCompat.getColor(context, R.color.happy)
            EmotionType.ANGRY -> ActivityCompat.getColor(context, R.color.angry)
            EmotionType.SAD -> ActivityCompat.getColor(context, R.color.sad)
            EmotionType.SICK -> ActivityCompat.getColor(context, R.color.sick)
            EmotionType.SCARED -> ActivityCompat.getColor(context, R.color.scared)
            EmotionType.NEUTRAL -> ActivityCompat.getColor(context, R.color.neutral)
        }
    }

    fun updateEmotionPieChartData(it: Calendar) {
        val emotionList = _repository.getEmotionDataFlowInDay(
            it.get(Calendar.YEAR), it.get(Calendar.MONTH), it.get(Calendar.DAY_OF_MONTH)
        )
        //计算总数
        val totalEmotions = emotionList.let {
            var sum = 0
            for (emotion in it) {
                sum += emotion.num
            }
            sum.toFloat()//转换为浮点数
        }
        happyPercentages = 0.0f
        angryPercentages = 0.0f
        sadPercentages = 0.0f
        sickPercentages = 0.0f
        scaredPercentages = 0.0f
        neutralPercentages = 0.0f

        val data = emotionList.map { entry ->
            val percentage = entry.num / totalEmotions
            when (entry.emotionType) {
                EmotionType.HAPPY.code -> happyPercentages = percentage
                EmotionType.ANGRY.code -> angryPercentages = percentage
                EmotionType.SAD.code -> sadPercentages = percentage
                EmotionType.SICK.code -> sickPercentages = percentage
                EmotionType.SCARED.code -> scaredPercentages = percentage
                EmotionType.NEUTRAL.code -> neutralPercentages = percentage
            }
            PieEntry(percentage, entry.emotionType.let {
                when (it) {
                    EmotionType.HAPPY.code -> "开心"
                    EmotionType.ANGRY.code -> "生气"
                    EmotionType.SAD.code -> "伤心"
                    EmotionType.SICK.code -> "恶心"
                    EmotionType.SCARED.code -> "害怕"
                    EmotionType.NEUTRAL.code -> "中立"
                    else -> {
                        throw IllegalArgumentException("Unknown emotion type")
                    }
                }
            })
        }
        emotionPieChartData.postValue(data)

        // refresh the diary list
        viewModelScope.launch {
            diaryInSelectedDay.postValue(_repository.getDiaryInDay(it))
        }
    }

    init {
        val initialEntries = listOf(
            PieEntry(0.2f, EmotionType.HAPPY),
            PieEntry(0.3f, EmotionType.ANGRY),
            PieEntry(0.1f, EmotionType.SAD),
            PieEntry(0.2f, EmotionType.NEUTRAL),
            PieEntry(0.1f, EmotionType.SCARED),
        )
        emotionPieChartData.postValue(initialEntries)
    }


    companion object {
        val Factory: ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(
                modelClass: Class<T>, extras: CreationExtras
            ): T {
                val application =
                    checkNotNull(extras[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY])
                return EmotionViewModel(
                    (application as MyApplication).emotionAndDiaryRepository
                ) as T
            }
        }
    }
}