package com.example.phychosiolz.model

//UDP传输用的数据类
data class UserTransInfo(
    val userName: String,
    val userBirth: String?,
    val userSex: String,
    val userIp: String
)