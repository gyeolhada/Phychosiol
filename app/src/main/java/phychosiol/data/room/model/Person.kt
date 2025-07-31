package com.example.phychosiolz.data.room.model

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * 人员身份类
 * 记录人的具体信息，与用户对应
 */
@Entity(tableName = "person")
data class Person(
    @PrimaryKey(autoGenerate = true)
    val pid: Int?,//主键
    var pname: String?,//真实姓名
    var pbitrh: String?,//出生日期
    var psex: String?,//性别
    var pheight: Double?,//身高
    var pweight: Double?,//体重

    var uid : Int?//用户id,外键
)