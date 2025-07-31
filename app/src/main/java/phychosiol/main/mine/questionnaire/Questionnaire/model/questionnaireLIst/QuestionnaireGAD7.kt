package com.example.phychosiolz.main.mine.questionnaire.Questionnaire.model.questionnaireLIst

import com.example.phychosiolz.main.mine.questionnaire.Question.manger.QuestionViewFactory
import com.example.phychosiolz.main.mine.questionnaire.Question.model.QuestionBase
import com.example.phychosiolz.main.mine.questionnaire.Question.model.QuestionSelect
import com.example.phychosiolz.main.mine.questionnaire.Questionnaire.manger.QuestionnaireBase
import com.example.phychosiolz.main.mine.questionnaire.Questionnaire.manger.QuestionnaireFactory
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date

/**
 * GAD7问卷
 */
class QuestionnaireGAD7 : QuestionnaireBase() {
    override fun initQuestionnaire() {
        tag = QuestionnaireFactory.GAD7
        title = QuestionnaireFactory.GAD7
        isNeedProgress = true
        content = "在过去两周里，您是否经常被以下问题所困扰"
        questionList = ArrayList()
        val anwTime: List<String> = mutableListOf("没有", "有几天", "一半以上的时间", "几乎天天")
        (questionList as ArrayList<QuestionBase>).add(
            QuestionSelect(
                "感到紧张、焦虑或紧张的",
                QuestionViewFactory.SINGLE_CHOICE,
                anwTime
            )
        )
        (questionList as ArrayList<QuestionBase>).add(
            QuestionSelect(
                "无法停止或控制焦虑",
                QuestionViewFactory.SINGLE_CHOICE,
                anwTime
            )
        )
        (questionList as ArrayList<QuestionBase>).add(
            QuestionSelect(
                "对不同的事情担心太多",
                QuestionViewFactory.SINGLE_CHOICE,
                anwTime
            )
        )
        (questionList as ArrayList<QuestionBase>).add(QuestionSelect("难以放松", QuestionViewFactory.SINGLE_CHOICE, anwTime))
        (questionList as ArrayList<QuestionBase>).add(
            QuestionSelect(
                "坐立不安的人很难安静地坐着",
                QuestionViewFactory.SINGLE_CHOICE,
                anwTime
            )
        )
        (questionList as ArrayList<QuestionBase>).add(
            QuestionSelect(
                "容易生气或急躁的",
                QuestionViewFactory.SINGLE_CHOICE,
                anwTime
            )
        )
        (questionList as ArrayList<QuestionBase>).add(
            QuestionSelect(
                "感到害怕，好像有什么可怕的事情要发生",
                QuestionViewFactory.SINGLE_CHOICE,
                anwTime
            )
        )
    }

    override fun getResult(): String {
        var score = 0
        for (questionBase in questionList!!) {
            score += questionBase.index
        }
        return score.toString() + ""
    }

    override fun saveFile() {
        val fileOutputStream = initFile()
        if (fileOutputStream != null && questionList != null) {
            var score = 0
            try {
                for (i in questionList!!.indices) {
                    val index = (i + 1).toString() + "、"
                    val question = questionList!![i]
                    if (question.answer != null) {
                        fileOutputStream.write(index.toByteArray())
                        fileOutputStream.write(question.content.toByteArray())
                        fileOutputStream.write("\r\n".toByteArray())
                        fileOutputStream.write(question.answer!!.toByteArray())
                        fileOutputStream.write("\r\n".toByteArray())
                        score += question.index
                    }
                }
                val sum = "the total score: $score\r\n"
                fileOutputStream.write(sum.toByteArray())
                val totalContent = getTotalContent(score)
                fileOutputStream.write(totalContent.toByteArray())
                fileOutputStream.write("\r\n".toByteArray())

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


    /**
     * 0-4 没有焦虑  5-9 轻度焦虑
     * 10-13 中度焦虑 14-18 中重度焦虑
     * 19-21 重度焦虑
     * @param total 得分
     * @return 结论
     */
    fun getTotalContent(total: Int): String {
        return if (total >= 0 && total < 4) {
            "没有焦虑症"
        } else if (total >= 5 && total < 10) {
            "可能有轻微焦虑症"
        } else if (total >= 10 && total < 14) {
            "可能有中度焦虑症"
        } else if (total >= 14 && total < 19) {
            "可能有中重度焦虑症"
        } else {
            "可能有重度焦虑症"
        }
    }
}