package com.example.phychosiolz.model

//用来存储存在用户信息，内容为原始数据+上一次登录时间
data class UserExistInfo(
    var uid: Int?,
    var uname: String?,//姓名
    var usex: String?,//性别
    var uavatar: String?,//头像
    var lastLogin: String?//上一次登录时间
)