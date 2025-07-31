package com.example.phychosiolz.main.mine.questionnaire.Questionnaire.manger

import com.example.phychosiolz.MyApplication
import com.example.phychosiolz.data.room.model.User
import com.example.phychosiolz.main.mine.questionnaire.Question.model.QuestionBase
import com.example.phychosiolz.utils.FileUtil
import java.io.File
import java.io.FileOutputStream
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * 问卷基类
 * 包含问卷标识、标题、描述、问题列表 以及 是否需要进度条的属性
 * 需要重写initQuestionnaire、getResult以及saveFile
 */
abstract class QuestionnaireBase {
    //唯一标识
    var tag: String
        protected set

    //标题
    var title: String
        protected set

    //描述
    var content: String
        protected set

    //问题列表
    var questionList: List<QuestionBase>? = null
        protected set

    //是否需要进度条
    var isNeedProgress = false
        protected set

    //初始化问卷（加入问题）
    abstract fun initQuestionnaire()

    //保存结果文件
    abstract fun saveFile()

    abstract fun getResult(): String

    init {
        tag = QuestionnaireFactory.NONE
        title = QuestionnaireFactory.NONE
        content = QuestionnaireFactory.NONE
        initQuestionnaire()
    }

    val isFinish: Boolean
        get() {
            for (i in questionList!!.indices) {
                val question = questionList!![i]
                if (!question.isFinish) {
                    return false
                }
            }
            return true
        }

    fun initFile(): FileOutputStream? {
        val user = MyApplication.instance.userRepository.loginedUser.value ?: return null
        val filePath = FileUtil.getQuestionnairSavedPath(user.uid!!, tag)
        val outputFile = File(filePath)

        try {
            val fileOutputStream = FileOutputStream(outputFile)
//            val pname = user.pname ?: ""
//            val pbirthday = user.pbirthday ?: ""
//            val usex = user.usex ?: ""
//
//            // 写入用户信息
//            fileOutputStream.write("姓名：$pname\r\n".toByteArray())
//            fileOutputStream.write("出生日期：$pbirthday\r\n".toByteArray())
//            fileOutputStream.write("性别：$usex\r\n".toByteArray())

            fileOutputStream.flush()
            return fileOutputStream
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }
}