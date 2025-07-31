package com.example.phychosiolz.data.room.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.phychosiolz.data.room.model.Attack
import com.example.phychosiolz.data.room.model.Diary
import com.example.phychosiolz.data.room.model.User
import kotlinx.coroutines.flow.Flow
import java.util.Date

@Dao
interface AttackDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(attack: Attack)
    @Delete
    fun delete(vararg attack: Attack)
    @Query("DELETE  FROM attack WHERE uid=:uid")
    fun deleteByUid(uid:Int)
    @Query("DELETE  FROM attack WHERE aid=:aid")
    fun deleteByAid(aid:Int)
    @Query("SELECT * FROM attack where uid=:uid ORDER BY aid DESC")
    fun getAllAttacksByUid(uid: Int): List<Attack>
}
