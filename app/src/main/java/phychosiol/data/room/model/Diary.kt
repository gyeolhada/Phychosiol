package com.example.phychosiolz.data.room.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "diary")
data class Diary (
    //did：Int？，title：Sting？，content：String？，images：Sting？---(JSON)list(Sting)star:Boolean?,time：String？emotion：Int？uid:Int
    @PrimaryKey(autoGenerate = true)
    var did: Int?,//diary id
    var title: String?,//diary title
    var content: String?,//diary content
    var images: String?,//diary images, JSON list(String),max 9
    var star: Boolean?,//diary star
    var time: String?,//diary time,DateFormat.getDateTimeInstance().format(Date())
    var emotion: Int?,//diary emotion
    var uid: Int?//user id
)