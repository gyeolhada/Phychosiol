package com.example.phychosiolz.data.room.model

import androidx.room.Entity

@Entity(tableName = "test_data",primaryKeys = ["uid", "year", "month", "day","period", "type"])
data class TestData(
    var uid: Int,//用户id
    var year: Int,//年
    var month: Int,//月
    var day: Int,//日
    var period:Int,//0~47
    var num: Int,//数据个数
    var avg: Float,
    var type: Int//数据类型(0:心率,1:血氧,2:体温）
)
