package com.example.phychosiolz.data.room.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "attack")
data class Attack (
    @PrimaryKey(autoGenerate = true)
    val aid: Int?,
    val startTime: String,
    val endTime: String,
    val feelings: String,
    val uid: Int?
)