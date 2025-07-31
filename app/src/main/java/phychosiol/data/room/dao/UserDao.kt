package com.example.phychosiolz.data.room.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.phychosiolz.data.room.model.Person
import com.example.phychosiolz.data.room.model.User
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(user: User)

    @Delete
    fun delete(vararg users: User)

    @Query("DELETE  FROM user WHERE uid=:uid")
    fun delete(uid:Int)

    @Update
    fun updateUser(user: User)

    @Query("SELECT * FROM user")
    fun getAllUsers(): Flow<List<User>>

    @Query("SELECT * FROM user WHERE uname = :username")
    fun getUserByUsername(username: String): User?

    @Query("SELECT * FROM user WHERE uid = :uid")
    fun getUserByUid(uid: Int): User?

    @Query("SELECT uid FROM user WHERE uname=:uname")
    fun getUidByUname(uname: String):Int
}
