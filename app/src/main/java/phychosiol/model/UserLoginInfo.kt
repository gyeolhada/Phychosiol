package com.example.phychosiolz.model


//用来存储用户登录信息
class UserLoginInfo(
    var uid: Int?,
    var pid: Int?,
    var uname: String?,//姓名
    var usex: String?,//性别
    var uavatar: String?,//头像
    var pname: String?,//真实姓名
    var pbirthday: String?,//生日
    var pheight: Double?,//身高
    var pweight: Double?,//体重
) :Cloneable{
    fun changeBirthday(birthday: String?): UserLoginInfo {
        pbirthday = birthday
        return this
    }

    fun changeHeight(height: Double?): UserLoginInfo {
        pheight = height
        return this
    }

    fun changeWeight(weight: Double?): UserLoginInfo {
        pweight = weight
        return this
    }

    fun changeRealName(realName: String?): UserLoginInfo {
        pname = realName
        return this
    }

    fun changeAvatar(avatar: String?): UserLoginInfo {
        uavatar = avatar
        return this
    }

    fun changeName(name: String?): UserLoginInfo {
        uname = name
        return this
    }

    fun changeSex(): UserLoginInfo {
        usex = if (usex == "男") "女" else "男"
        return this
    }
    public override fun clone(): UserLoginInfo {
        return super.clone() as UserLoginInfo
    }
}