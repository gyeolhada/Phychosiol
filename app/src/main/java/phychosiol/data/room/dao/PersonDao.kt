package com.example.phychosiolz.data.room.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.phychosiolz.data.room.model.Person

@Dao
interface PersonDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(vararg persons: Person)

    @Delete
    fun delete(vararg persons: Person)

    @Update
    fun updatePerson(persons: Person)

    @Query("SELECT * FROM person WHERE uid = :uid")
    fun getPersonByUid(uid: Int?): Person?

    @Query("DELETE FROM person WHERE uid=:uid")
    fun deleteByUid(uid: Int?)
}