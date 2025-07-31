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
 * SD5问卷
 */
class QuestionnaireSDS : QuestionnaireBase() {
    override fun initQuestionnaire() {
        tag = QuestionnaireFactory.SDS
        title = QuestionnaireFactory.SDS
        isNeedProgress = true
        questionList = ArrayList()
        content =
            "以下列出有些人可能会有的问题，请仔细阅读每一条，然后根据最近一星期以内下述情况影响您的实际感觉，选择该题的程度得分"
        val anwSDS: List<String> = mutableListOf("从无或偶尔有", "很少有", "经常有", "总是如此")
        val contentSDS: List<String> = mutableListOf(
            "我感到情绪沮丧，郁闷",
            "我感到早晨心情最好",
            "我要哭或想哭",
            "我夜间睡眠不好",
            "我吃饭像平时一样多",
            "我的性功能正常",
            "我感到体重减轻",
            "我为便秘烦恼",
            "我的心跳比平时快",
            "我无故感到疲劳",
            "我的头脑像往常一样清楚",
            "我做事像平时一样不感到困难",
            "我坐卧不安,难以保持平静",
            "我对未来感到有希望",
            "我比平时更容易激怒",
            "我觉得决定什么事很容易",
            "我感到自己是有用的和不可缺少的人",
            "我的生活很有意义",
            "假若我死了别人会过得更好",
            "我仍旧喜爱自己平时喜爱的东西"
        )
        for (i in contentSDS.indices) {
            (questionList as ArrayList<QuestionBase>).add(
                QuestionSelect(
                    contentSDS[i],
                    QuestionViewFactory.SINGLE_CHOICE,
                    anwSDS
                )
            )
        }
    }

    override fun getResult(): String {
        val reverseSDSList: List<Int> = mutableListOf(2, 5, 6, 11, 12, 14, 16, 17, 18, 20)
        var score = 0
        var index: Int
        for (i in questionList!!.indices) {
            index = i + 1
            val question = questionList!![i]
            score += if (reverseSDSList.contains(index)) {
                4 - question.index
            } else {
                question.index + 1
            }
        }
        return score.toString() + ""
    }

    override fun saveFile() {
        val fileOutputStream = initFile()
        if (fileOutputStream != null && questionList != null) {
            val reverseSDSList: List<Int> = mutableListOf(2, 5, 6, 11, 12, 14, 16, 17, 18, 20)
            var score = 0
            try {
                for (i in questionList!!.indices) {
                    val index = i + 1
                    val question = questionList!![i]
                    if (question.answer != null) {
                        fileOutputStream.write("$index、".toByteArray())
                        fileOutputStream.write(question.content.toByteArray())
                        fileOutputStream.write("\r\n".toByteArray())
                        fileOutputStream.write(question.answer!!.toByteArray())
                        fileOutputStream.write("\r\n".toByteArray())
                        score += if (reverseSDSList.contains(index)) {
                            4 - question.index
                        } else {
                            question.index + 1
                        }
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
     * 0-49 无抑郁   50-59 轻微至轻度抑郁
     * 60-69 中至重度抑郁 70及以上 重度抑郁
     * @param total 得分
     * @return 结论
     */
    fun getTotalContent(total: Int): String {
        return if (total >= 0 && total < 50) {
            "无抑郁"
        } else if (total >= 50 && total < 60) {
            "轻微至轻度抑郁"
        } else if (total >= 60 && total < 70) {
            "中至重度抑郁"
        } else {
            "重度抑郁"
        }
    }
}