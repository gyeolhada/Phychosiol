package com.example.phychosiolz.data.room.model

import androidx.room.Entity
import androidx.room.PrimaryKey


/**
 * 用户类
 * 记录用户的基本信息
 */
@Entity(tableName = "user")
data class User(
    @PrimaryKey(autoGenerate = true)
    val uid: Int?,//主键
    var uname: String?,//姓名
    var usex: String?,//性别
    var uavatar: String?//头像
)