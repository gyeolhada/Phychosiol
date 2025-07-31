package com.example.phychosiolz.data.room.db

import android.R.attr.version
import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.phychosiolz.data.room.dao.AttackDao
import com.example.phychosiolz.data.room.dao.DiaryDao
import com.example.phychosiolz.data.room.dao.EmotionDao
import com.example.phychosiolz.data.room.dao.PersonDao
import com.example.phychosiolz.data.room.dao.TestDataDao
import com.example.phychosiolz.data.room.dao.UserDao
import com.example.phychosiolz.data.room.model.Attack
import com.example.phychosiolz.data.room.model.Diary
import com.example.phychosiolz.data.room.model.Emotion
import com.example.phychosiolz.data.room.model.Person
import com.example.phychosiolz.data.room.model.TestData
import com.example.phychosiolz.data.room.model.User


@Database(
    entities = [
        User::class,
        Person::class,
        Diary::class,
        TestData::class,
        Emotion::class,
        Attack::class,
    ], version = 2
)
abstract class MyDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun personDao(): PersonDao
    abstract fun diaryDao(): DiaryDao
    abstract fun testdataDao(): TestDataDao
    abstract  fun emotionDao():EmotionDao
    abstract  fun attackDao(): AttackDao
}