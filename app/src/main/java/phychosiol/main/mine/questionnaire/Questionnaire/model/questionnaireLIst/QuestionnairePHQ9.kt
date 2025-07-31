package com.example.phychosiolz.main.mine.questionnaire.Questionnaire.model.questionnaireLIst

import android.util.Log
import com.example.phychosiolz.main.mine.questionnaire.Question.manger.QuestionViewFactory
import com.example.phychosiolz.main.mine.questionnaire.Question.model.QuestionBase
import com.example.phychosiolz.main.mine.questionnaire.Question.model.QuestionSelect
import com.example.phychosiolz.main.mine.questionnaire.Questionnaire.manger.QuestionnaireBase
import com.example.phychosiolz.main.mine.questionnaire.Questionnaire.manger.QuestionnaireFactory
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date

/**
 * PHQ9问卷
 */
class QuestionnairePHQ9 : QuestionnaireBase() {
    override fun initQuestionnaire() {
        tag = QuestionnaireFactory.PHQ9
        title = QuestionnaireFactory.PHQ9
        isNeedProgress = true
        content = "在过去的两周里，您感觉自己被以下症状所困扰的频率是?"
        questionList = ArrayList()
        val anwTime: List<String> = mutableListOf("没有", "有几天", "一半以上的时间", "几乎天天")
        (questionList as ArrayList<QuestionBase>).add(
            QuestionSelect(
                "做事时提不起劲或没有兴趣",
                QuestionViewFactory.SINGLE_CHOICE,
                anwTime
            )
        )
        (questionList as ArrayList<QuestionBase>).add(
            QuestionSelect(
                "感到心情低落，沮丧或绝望",
                QuestionViewFactory.SINGLE_CHOICE,
                anwTime
            )
        )
        (questionList as ArrayList<QuestionBase>).add(
            QuestionSelect(
                "入睡困难，睡得不安或睡眠时间过长",
                QuestionViewFactory.SINGLE_CHOICE,
                anwTime
            )
        )
        (questionList as ArrayList<QuestionBase>).add(
            QuestionSelect(
                "感到疲倦或没有活力",
                QuestionViewFactory.SINGLE_CHOICE,
                anwTime
            )
        )
        (questionList as ArrayList<QuestionBase>).add(
            QuestionSelect(
                "食欲不振或吃太多",
                QuestionViewFactory.SINGLE_CHOICE,
                anwTime
            )
        )
        (questionList as ArrayList<QuestionBase>).add(
            QuestionSelect(
                "觉得自己很糟或觉得自己很失败，或让自己，家人失望",
                QuestionViewFactory.SINGLE_CHOICE,
                anwTime
            )
        )
        (questionList as ArrayList<QuestionBase>).add(
            QuestionSelect(
                "对事物专注有困难，例如看报纸或看电视时",
                QuestionViewFactory.SINGLE_CHOICE,
                anwTime
            )
        )
        (questionList as ArrayList<QuestionBase>).add(
            QuestionSelect(
                "说话或行动速度缓慢到别人已经察觉，或刚好相反——变得比平时更烦躁或坐立不安，动来动去",
                QuestionViewFactory.SINGLE_CHOICE,
                anwTime
            )
        )
        (questionList as ArrayList<QuestionBase>).add(
            QuestionSelect(
                "有不如死掉或用某种方式伤害自己的念头",
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
        Log.i("QuestionnairePHQ9","$score")
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
     * 0-4 没有抑郁  5-9 轻度抑郁
     * 10-14 中度抑郁 15-19 中重度抑郁
     * 20-27 重度抑郁
     * @param total 得分
     * @return 结论
     */
    fun getTotalContent(total: Int): String {
        return if (total >= 0 && total < 5) {
            "没有抑郁症"
        } else if (total >= 5 && total < 10) {
            "可能有轻度抑郁症"
        } else if (total >= 10 && total < 15) {
            "可能有中度抑郁症"
        } else if (total >= 15 && total < 20) {
            "可能有中重度抑郁症"
        } else {
            "可能有重度抑郁症"
        }
    }
}