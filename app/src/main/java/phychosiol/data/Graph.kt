package com.example.phychosiolz.data

import androidx.room.Room
import com.example.phychosiolz.MyApplication
import com.example.phychosiolz.data.room.db.MyDatabase


object Graph {
    lateinit var db: MyDatabase
    fun provide(context: MyApplication) {
        db = Room.databaseBuilder(context, MyDatabase::class.java, "phychosiolZ")
            .allowMainThreadQueries()
            .build()
    }
}