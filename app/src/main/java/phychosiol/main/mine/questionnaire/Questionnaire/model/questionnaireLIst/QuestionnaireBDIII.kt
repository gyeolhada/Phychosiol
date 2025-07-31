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
 * BDIII问卷
 */
class QuestionnaireBDIII : QuestionnaireBase() {
    override fun initQuestionnaire() {
        tag = QuestionnaireFactory.BDIII
        title = QuestionnaireFactory.BDIII
        content =
            "请根据你最近两周(包括今天)的感觉，选择一条最适合您情况的项目(如有两条以上适合您，请选择最严重的一条)"
        questionList = ArrayList()
        isNeedProgress = true
        val mContent: List<String> = mutableListOf(
            "心情", "悲观", "失败感", "不满", "自罪感",
            "惩罚感", "自厌", "自责", "自杀倾向", "痛苦",
            "易激动", "社会退缩", "犹豫不决", "形象歪曲", "活动受抑制",
            "睡眠障碍", "疲劳", "食欲下降", "体重减轻", "有关躯体的健康观念",
            "性欲减退"
        )
        val mAnwBDI = listOf<List<String>>(
            mutableListOf(
                "我不感到悲伤。",
                "我感到悲伤。",
                "我始终悲伤，不能自制。",
                "我太悲伤或不愉快，不堪忍受。"
            ),
            mutableListOf(
                "我对将来并不失望。",
                "对未来我感到心灰意冷。",
                "我感到前景黯淡。",
                "我觉得将来毫无希望，无法改善。"
            ),
            mutableListOf(
                "我没有感到失败。",
                "我觉得比一般人失败要多些。",
                "回首往事，我能看到的是很多次失败。",
                "我觉得我是一个完全失败的人。"
            ),
            mutableListOf(
                "我从各种事件中得到很多满足。",
                "我不能从各种事件中感受到乐趣。",
                "我不能从各种事件中得到真正的满足。",
                "我对一切事情不满意或感到枯燥无味。"
            ),
            mutableListOf(
                "我不感到有罪过。",
                "我在相当的时间里感到有罪过。",
                "我在大部分时间里觉得有罪。",
                "我在任何时候都觉得有罪。"
            ),
            mutableListOf(
                "我没有觉得受到惩罚。",
                "我觉得可能会受到惩罚。",
                "我预料将受到惩罚。",
                "我觉得正受到惩罚。"
            ),
            mutableListOf("我对自己并不失望。", "我对自己感到失望。", "我讨厌自己。", "我恨自己。"),
            mutableListOf(
                "我觉得并不比其他人更不好。",
                "我要批判自己的弱点和错误",
                "我在所有的时间里都责备自己的错误。",
                "我责备自己把所有的事情都弄坏了。"
            ),
            mutableListOf(
                "我没有任何想弄死自己的想法。",
                "我有自杀想法，但我不会去做。",
                "我想自杀。",
                "如果有机会我就自杀。"
            ),
            mutableListOf(
                "我哭泣与往常一样。",
                "我比往常哭得多。",
                "我现在一直要哭。",
                "我过去能哭，但现在要哭也哭不出来。"
            ),
            mutableListOf(
                "和过去相比，我现在生气并不更多。",
                "我现在比往常更容易生气发火。",
                "我觉得现在所有的时间都容易生气。",
                "过去使我生气的事，现在一点也不能使我生气了。"
            ),
            mutableListOf(
                "我对其他人没有失去兴趣。",
                "和过去相比，我对别人的兴趣减少了。",
                "我对别人的兴趣大部分失去了。",
                "我对别人的兴趣已全部丧失了。"
            ),
            mutableListOf(
                "我作出决定没什么困难。",
                "我推迟作出决定比过去多了。",
                "我作决定比以前困难大得多。",
                "我再也不能作出决定了。"
            ),
            mutableListOf(
                "觉得我的外表看上去并不比过去更差。",
                "我担心自己看上去显得老了，没有吸引力。",
                "我觉得我的外貌有些变化，使我难看了。",
                "我相信我看起来很丑陋"
            ),
            mutableListOf(
                "我工作和以前一样好。",
                "要着手做事，我现在需额外花些力气。",
                "无论做什么我必须努力催促自己才行。",
                "我什么工作也不能做了。"
            ),
            mutableListOf(
                "我睡觉与往常一样好。",
                "我睡眠不如过去好。",
                "我比往常早醒 1～2 小时，难以再睡。",
                "我比往常早醒几个小时，不能再睡。"
            ),
            mutableListOf(
                "我并不感到比往常更疲乏。",
                "我比过去更容易感到疲乏无力。",
                "几乎不管做什么，我都感到疲乏无力。",
                "我太疲乏无力，不能做任何事情。"
            ),
            mutableListOf(
                "我的食欲和往常一样。",
                "我的食欲不如过去好。",
                "我现在的食欲差得多了。",
                "我一点也没有食欲了。"
            ),
            mutableListOf(
                "最近我的体重并无很大减轻。",
                "我体重下降2.27 千克以上。",
                "我体重下降5.54 千克以上。",
                "我体重下降7.81 千克以上。"
            ),
            mutableListOf(
                "我对健康状况并不比往常更担心。",
                "我担心身体上的问题，如疼痛、胃不适或便秘。",
                "我很担心身体问题，想别的事情很难。",
                "我对身体问题如此担忧，以致不能想其他任何事情。"
            ),
            mutableListOf(
                "我没有发现自己对性的兴趣最近有什么变化。",
                "我对性的兴趣比过去降低了。",
                "我现在对性的兴趣大大下降。",
                "我对性的兴趣已经完全丧失。"
            )
        )
        for (i in mContent.indices) {
            (questionList as ArrayList<QuestionBase>).add(
                QuestionSelect(
                    mContent[i],
                    QuestionViewFactory.SINGLE_CHOICE,
                    mAnwBDI[i]
                )
            )
        }
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
        var score = 0
        if (fileOutputStream != null && questionList != null) {
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
                fileOutputStream!!.write(sum.toByteArray())
                val totalContent = getTotalContent(score)
                fileOutputStream.write(totalContent.toByteArray())
                fileOutputStream.write("\r\n".toByteArray())

                val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                val currentTime = sdf.format(Date())
                fileOutputStream.write("Current Time: $currentTime\r\n".toByteArray())

                fileOutputStream.flush()
                fileOutputStream.close()
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
     * 0-13 无抑郁 14-19 轻度抑郁
     * 20-28 中度抑郁 29-63 严重抑郁
     * @param total 得分
     * @return 结论
     */
    fun getTotalContent(total: Int): String {
        return if (total >= 0 && total < 14) {
            "无抑郁"
        } else if (total >= 14 && total < 20) {
            "轻度抑郁"
        } else if (total >= 20 && total < 29) {
            "中度抑郁"
        } else {
            "严重抑郁"
        }
    }
}