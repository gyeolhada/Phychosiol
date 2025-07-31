package com.example.phychosiolz.data.room.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.phychosiolz.data.room.model.Diary
import com.example.phychosiolz.data.room.model.User
import kotlinx.coroutines.flow.Flow
import java.util.Date

@Dao
interface DiaryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(diary: Diary)

    @Delete
    fun delete(vararg diary: Diary)

    @Query("DELETE  FROM diary WHERE uid=:uid")
    fun deleteByUid(uid:Int)

    @Query("DELETE  FROM diary WHERE did=:did")
    fun deleteByDid(did:Int)

    @Update
    fun updateDiary(diary: Diary)

    @Query("SELECT * FROM diary where uid=:uid ORDER BY did DESC")
    fun getAllDialogByUid(uid: Int): Flow<List<Diary>>

    //contains the date
    @Query("SELECT * FROM diary where uid=:uid and time like'%' || :time || '%'ORDER BY did DESC")
    fun getDiaryInDayByUid(uid: Int, time: String): List<Diary>?
}
