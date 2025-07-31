package com.example.phychosiolz

import android.app.Application
import android.content.Context
import androidx.datastore.preferences.preferencesDataStore
import com.example.phychosiolz.data.Graph
import com.example.phychosiolz.repo.DataStoreRepository
import com.example.phychosiolz.repo.EmotionAndDiaryRepository
import com.example.phychosiolz.repo.ManagerRepository
import com.example.phychosiolz.repo.QuestionnaireRepository
import com.example.phychosiolz.repo.UserAttackRepository
import com.example.phychosiolz.repo.UserGraphDataRepository
import com.example.phychosiolz.repo.UserRepository
import com.example.phychosiolz.repo.UserUdpRepository

private val Context.dataStore by preferencesDataStore(
    name = "phychosiol",

    )

class MyApplication : Application() {
    companion object {
        lateinit var instance: MyApplication
    }

    var testStartTime = System.currentTimeMillis()

    private val userDao by lazy { Graph.db.userDao() }
    private val personDao by lazy { Graph.db.personDao() }
    private val diaryDao by lazy { Graph.db.diaryDao() }
    private val emotionDao by lazy { Graph.db.emotionDao() }
    private val attackDao by lazy {Graph.db.attackDao()}
    val dataStoreRepository by lazy {
        DataStoreRepository(
            instance.dataStore
        )
    }
    val userRepository by lazy {
        UserRepository(
            userDao,
            personDao,
            dataStoreRepository
        )
    }
    val emotionAndDiaryRepository by lazy {
        EmotionAndDiaryRepository(
            diaryDao,
            userRepository,
            emotionDao
        )
    }
    val userAttackRepository by lazy {
        UserAttackRepository(
            userRepository,
            attackDao
        )
    }
    val userUdpRepository by lazy {
        UserUdpRepository()
    }
    val userGraphDataRepository by lazy {
        UserGraphDataRepository()
    }
    val managerRepository by lazy {
        ManagerRepository(dataStoreRepository)
    }
    val questionnaireRepository by lazy {
        QuestionnaireRepository(dataStoreRepository)
    }
    override fun onCreate() {
        super.onCreate()
        instance = this
        Graph.provide(this)
    }
}