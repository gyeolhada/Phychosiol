package com.example.phychosiolz.data.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.phychosiolz.data.room.model.Diary
import com.example.phychosiolz.data.room.model.Emotion
import kotlinx.coroutines.flow.Flow

@Dao
interface EmotionDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(emotion: Emotion)

    @Query("SELECT * FROM emotion where uid=:uid and year=:y and month=:m and day=:d")
    fun getEmotionDataInDayByUid(uid: Int,y:Int,m:Int,d:Int): List<Emotion>

    @Query("SELECT * FROM emotion where uid=:uid and year=:y and month=:m and day=:d and emotionType=:type")
    fun getASpecificEmotionDataByUid(uid: Int,y:Int,m:Int,d:Int,type:Int): Emotion?

    @Query("UPDATE emotion SET num=num+1 where eid=:eid")
    fun updateEmotionData(eid: Int)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertEmotionData(emotion: Emotion)
}
