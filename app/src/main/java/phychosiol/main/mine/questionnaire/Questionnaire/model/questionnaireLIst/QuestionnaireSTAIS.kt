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
 * STAIS问卷
 */
class QuestionnaireSTAIS : QuestionnaireBase() {
    //反向计分
    var indexReserveSTAI: List<Int> =
        mutableListOf(1, 2, 5, 8, 10, 11, 15, 16, 19, 20, 21, 23, 24, 26, 27, 30, 33, 34, 36, 39)

    override fun initQuestionnaire() {
        tag = QuestionnaireFactory.STAIS
        title = QuestionnaireFactory.STAIS
        isNeedProgress = true
        content =
            "下面列出的是一些人们常常用来描述他们自己的陈述，请阅读每一个陈述，然后选择相应的陈述来表示你现在最恰当的感觉" +
                    "也就是你此时此刻最恰当的没有对或错的回答,不要对任何一个陈述花太多的时间去考虑，但所给的回答应该是你现在最恰当的感觉。"
        questionList = ArrayList()
        val anwSTAI: List<String> = mutableListOf("完全没有", "有些", "中等", "非常明显")

        // 1- 20 状态焦虑量表 （S-AI）
        (questionList as ArrayList<QuestionBase>).add(
            QuestionSelect(
                "我感到心情平静",
                QuestionViewFactory.SINGLE_CHOICE,
                anwSTAI
            )
        )
        (questionList as ArrayList<QuestionBase>).add(QuestionSelect("我感到安全", QuestionViewFactory.SINGLE_CHOICE, anwSTAI))
        (questionList as ArrayList<QuestionBase>).add(QuestionSelect("我是紧张的", QuestionViewFactory.SINGLE_CHOICE, anwSTAI))
        (questionList as ArrayList<QuestionBase>).add(
            QuestionSelect(
                "我感到紧张束缚",
                QuestionViewFactory.SINGLE_CHOICE,
                anwSTAI
            )
        )
        (questionList as ArrayList<QuestionBase>).add(QuestionSelect("我感到安逸", QuestionViewFactory.SINGLE_CHOICE, anwSTAI))
        (questionList as ArrayList<QuestionBase>).add(QuestionSelect("我感到烦乱", QuestionViewFactory.SINGLE_CHOICE, anwSTAI))
        (questionList as ArrayList<QuestionBase>).add(
            QuestionSelect(
                "我现在正烦恼，感到这种烦恼超过了可能的不幸",
                QuestionViewFactory.SINGLE_CHOICE,
                anwSTAI
            )
        )
        (questionList as ArrayList<QuestionBase>).add(QuestionSelect("我感到满意", QuestionViewFactory.SINGLE_CHOICE, anwSTAI))
        (questionList as ArrayList<QuestionBase>).add(QuestionSelect("我感到害怕", QuestionViewFactory.SINGLE_CHOICE, anwSTAI))
        (questionList as ArrayList<QuestionBase>).add(QuestionSelect("我感到舒适", QuestionViewFactory.SINGLE_CHOICE, anwSTAI))
        (questionList as ArrayList<QuestionBase>).add(QuestionSelect("我有自信心", QuestionViewFactory.SINGLE_CHOICE, anwSTAI))
        (questionList as ArrayList<QuestionBase>).add(
            QuestionSelect(
                "我觉得神经过敏",
                QuestionViewFactory.SINGLE_CHOICE,
                anwSTAI
            )
        )
        (questionList as ArrayList<QuestionBase>).add(
            QuestionSelect(
                "我极度紧张不安",
                QuestionViewFactory.SINGLE_CHOICE,
                anwSTAI
            )
        )
        (questionList as ArrayList<QuestionBase>).add(QuestionSelect("我优柔寡断", QuestionViewFactory.SINGLE_CHOICE, anwSTAI))
        (questionList as ArrayList<QuestionBase>).add(QuestionSelect("我是轻松的", QuestionViewFactory.SINGLE_CHOICE, anwSTAI))
        (questionList as ArrayList<QuestionBase>).add(
            QuestionSelect(
                "我感到心满意足",
                QuestionViewFactory.SINGLE_CHOICE,
                anwSTAI
            )
        )
        (questionList as ArrayList<QuestionBase>).add(QuestionSelect("我是烦恼的", QuestionViewFactory.SINGLE_CHOICE, anwSTAI))
        (questionList as ArrayList<QuestionBase>).add(QuestionSelect("我感到慌乱", QuestionViewFactory.SINGLE_CHOICE, anwSTAI))
        (questionList as ArrayList<QuestionBase>).add(QuestionSelect("我感到镇定", QuestionViewFactory.SINGLE_CHOICE, anwSTAI))
        (questionList as ArrayList<QuestionBase>).add(QuestionSelect("我感到愉快", QuestionViewFactory.SINGLE_CHOICE, anwSTAI))

        //21-40 特殊焦虑量表（T-AI）
        (questionList as ArrayList<QuestionBase>).add(QuestionSelect("我感到愉快", QuestionViewFactory.SINGLE_CHOICE, anwSTAI))
        (questionList as ArrayList<QuestionBase>).add(
            QuestionSelect(
                "我感到神经过敏和不安",
                QuestionViewFactory.SINGLE_CHOICE,
                anwSTAI
            )
        )
        (questionList as ArrayList<QuestionBase>).add(
            QuestionSelect(
                "我感到自我满足",
                QuestionViewFactory.SINGLE_CHOICE,
                anwSTAI
            )
        )
        (questionList as ArrayList<QuestionBase>).add(
            QuestionSelect(
                "我希望能像别人那样高兴",
                QuestionViewFactory.SINGLE_CHOICE,
                anwSTAI
            )
        )
        (questionList as ArrayList<QuestionBase>).add(
            QuestionSelect(
                "我感到我像衰竭一样",
                QuestionViewFactory.SINGLE_CHOICE,
                anwSTAI
            )
        )
        (questionList as ArrayList<QuestionBase>).add(QuestionSelect("我感到很宁静", QuestionViewFactory.SINGLE_CHOICE, anwSTAI))
        (questionList as ArrayList<QuestionBase>).add(
            QuestionSelect(
                "我是平静的、冷静的和泰然自若的",
                QuestionViewFactory.SINGLE_CHOICE,
                anwSTAI
            )
        )
        (questionList as ArrayList<QuestionBase>).add(
            QuestionSelect(
                "我感到困难意义堆集起来，因此无法克服",
                QuestionViewFactory.SINGLE_CHOICE,
                anwSTAI
            )
        )
        (questionList as ArrayList<QuestionBase>).add(
            QuestionSelect(
                "我过分忧虑一件事，实际这些事无关紧要",
                QuestionViewFactory.SINGLE_CHOICE,
                anwSTAI
            )
        )
        (questionList as ArrayList<QuestionBase>).add(QuestionSelect("我是高兴的", QuestionViewFactory.SINGLE_CHOICE, anwSTAI))
        (questionList as ArrayList<QuestionBase>).add(
            QuestionSelect(
                "我的思想处于混乱状态",
                QuestionViewFactory.SINGLE_CHOICE,
                anwSTAI
            )
        )
        (questionList as ArrayList<QuestionBase>).add(QuestionSelect("我缺乏自信心", QuestionViewFactory.SINGLE_CHOICE, anwSTAI))
        (questionList as ArrayList<QuestionBase>).add(QuestionSelect("我感到安全", QuestionViewFactory.SINGLE_CHOICE, anwSTAI))
        (questionList as ArrayList<QuestionBase>).add(
            QuestionSelect(
                "我容易做出决断",
                QuestionViewFactory.SINGLE_CHOICE,
                anwSTAI
            )
        )
        (questionList as ArrayList<QuestionBase>).add(QuestionSelect("我感到不合适", QuestionViewFactory.SINGLE_CHOICE, anwSTAI))
        (questionList as ArrayList<QuestionBase>).add(QuestionSelect("我是满足的", QuestionViewFactory.SINGLE_CHOICE, anwSTAI))
        (questionList as ArrayList<QuestionBase>).add(
            QuestionSelect(
                "一些不重要的思想总缠绕着我，并打扰我",
                QuestionViewFactory.SINGLE_CHOICE,
                anwSTAI
            )
        )
        (questionList as ArrayList<QuestionBase>).add(
            QuestionSelect(
                "我产生的沮丧时如此强烈，以至于我不能从思想中排出它们",
                QuestionViewFactory.SINGLE_CHOICE,
                anwSTAI
            )
        )
        (questionList as ArrayList<QuestionBase>).add(
            QuestionSelect(
                "我是一个镇定的人",
                QuestionViewFactory.SINGLE_CHOICE,
                anwSTAI
            )
        )
        (questionList as ArrayList<QuestionBase>).add(
            QuestionSelect(
                "当我考虑我目前的事情和利益时，我就陷入紧张状态",
                QuestionViewFactory.SINGLE_CHOICE,
                anwSTAI
            )
        )
    }

    override fun getResult(): String {
        return ""
    }

    override fun saveFile() {
        val fileOutputStream = initFile()
        if (fileOutputStream != null && questionList != null) {
            var S_AI = 0
            var T_AI = 0
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
                        if (i < 20) {
                            S_AI += if (indexReserveSTAI.contains(i + 1)) {
                                4 - question.index
                            } else {
                                question.index + 1
                            }
                        } else {
                            T_AI += if (indexReserveSTAI.contains(i + 1)) {
                                4 - question.index
                            } else {
                                question.index + 1
                            }
                        }
                    }
                }
                fileOutputStream.write("\r\n".toByteArray())
                fileOutputStream.write("S_AI: $S_AI\r\n".toByteArray())
                fileOutputStream.write("T_AI: $T_AI\r\n".toByteArray())

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