package com.example.phychosiolz.data.room.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.phychosiolz.data.enums.EmotionType

@Entity(tableName = "emotion")
data class Emotion(
    @PrimaryKey(autoGenerate = true)
    val eid: Int?,
    val uid: String,
    val emotionType: Int,
    var year: Int,//年
    var month: Int,//月
    var day: Int,//日
    var num: Int,//数据个数
)
