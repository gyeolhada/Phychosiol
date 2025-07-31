package com.example.phychosiolz.main.mine.questionnaire.Questionnaire.model.questionnaireLIst

import com.example.phychosiolz.main.mine.questionnaire.Question.manger.QuestionViewFactory
import com.example.phychosiolz.main.mine.questionnaire.Question.model.QuestionBase
import com.example.phychosiolz.main.mine.questionnaire.Question.model.QuestionLevel
import com.example.phychosiolz.main.mine.questionnaire.Questionnaire.manger.QuestionnaireBase
import com.example.phychosiolz.main.mine.questionnaire.Questionnaire.manger.QuestionnaireFactory
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date

/**
 * PANASX问卷
 */
class QuestionnairePANASX : QuestionnaireBase() {
    override fun initQuestionnaire() {
        tag = QuestionnaireFactory.PANASX
        title = QuestionnaireFactory.PANASX
        isNeedProgress = false
        content =
            "你现在的情绪怎么样？请在标尺上标注你现在的情绪状态，并用数值为自己的情绪状况评分，0代表最弱，100代表最强"
        questionList = ArrayList()
        val anwPANX: List<String> = mutableListOf(
            "0", "10", "20", "30", "40", "50", "60", "70", "80", "90", "100"
        )
        (questionList as ArrayList<QuestionBase>).add(QuestionLevel("愤怒", QuestionViewFactory.LEVEL_CHOICE, anwPANX))
        (questionList as ArrayList<QuestionBase>).add(QuestionLevel("厌恶", QuestionViewFactory.LEVEL_CHOICE, anwPANX))
        (questionList as ArrayList<QuestionBase>).add(QuestionLevel("悲伤", QuestionViewFactory.LEVEL_CHOICE, anwPANX))
        (questionList as ArrayList<QuestionBase>).add(QuestionLevel("快乐", QuestionViewFactory.LEVEL_CHOICE, anwPANX))
        (questionList as ArrayList<QuestionBase>).add(QuestionLevel("恐惧", QuestionViewFactory.LEVEL_CHOICE, anwPANX))
        (questionList as ArrayList<QuestionBase>).add(QuestionLevel("担忧", QuestionViewFactory.LEVEL_CHOICE, anwPANX))
        (questionList as ArrayList<QuestionBase>).add(QuestionLevel("恶心", QuestionViewFactory.LEVEL_CHOICE, anwPANX))
        (questionList as ArrayList<QuestionBase>).add(QuestionLevel("心烦意乱", QuestionViewFactory.LEVEL_CHOICE, anwPANX))
        (questionList as ArrayList<QuestionBase>).add(QuestionLevel("紧张不安", QuestionViewFactory.LEVEL_CHOICE, anwPANX))
        (questionList as ArrayList<QuestionBase>).add(QuestionLevel("骄傲自满", QuestionViewFactory.LEVEL_CHOICE, anwPANX))
        (questionList as ArrayList<QuestionBase>).add(QuestionLevel("身体不适", QuestionViewFactory.LEVEL_CHOICE, anwPANX))
    }

    override fun getResult(): String {
        return ""
    }

    override fun saveFile() {
        val fileOutputStream = initFile()
        if (fileOutputStream != null && questionList != null) {
            try {
                for (i in questionList!!.indices) {
                    val index = (i + 1).toString() + "、"
                    val question = questionList!![i]
                    if (question.answer != null) {
                        fileOutputStream.write(index.toByteArray())
                        fileOutputStream.write(question.content.toByteArray())
                        fileOutputStream.write(" ".toByteArray())
                        fileOutputStream.write(question.answer!!.toByteArray())
                        fileOutputStream.write("\r\n".toByteArray())
                    }
                }

                val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                val currentTime = sdf.format(Date())
                fileOutputStream.write("Current Time: $currentTime\r\n".toByteArray())

                fileOutputStream.flush()
            } catch (e: IOException) {
                e.printStackTrace()
            } finally {
                try {
                    fileOutputStream.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }
    }

}