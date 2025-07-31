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
 * EPQ问卷
 */
class QuestionnaireEPQ : QuestionnaireBase() {
    // E分量表（正 18 反3, 共21）
    private val EindexList: List<Int> = mutableListOf(
        1, 5, 10, 13, 14,
        17, 25, 33, 37, 41,
        49, 53, 55, 61, 65,
        71, 80, 84
    )
    private val EindexReverseList: List<Int> = mutableListOf(21, 29, 45)

    //N分量表（正24 反0 共24）
    private val NindexList: List<Int> = mutableListOf(
        3, 7, 12, 15, 19,
        23, 27, 31, 35, 39,
        43, 47, 51, 57, 59,
        63, 67, 69, 73, 74,
        77, 78, 82, 86
    )

    //P分量表（正12，反11，共23）
    private val PindexList: List<Int> = mutableListOf(
        22, 26, 30, 34, 46,
        50, 66, 68, 75, 76,
        81, 85
    )
    private val PindexReverseList: List<Int> = mutableListOf(
        2, 6, 9, 11, 18,
        38, 42, 56, 62, 72,
        88
    )

    //L分量表（正5, 反15，共20）
    private val LindexList: List<Int> = mutableListOf(20, 32, 36, 58, 87)
    private val LindexReverseList: List<Int> = mutableListOf(
        4, 8, 16, 24, 28,
        40, 44, 48, 52, 54,
        60, 64, 70, 79, 83
    )

    override fun initQuestionnaire() {
        tag = QuestionnaireFactory.EPQ
        title = QuestionnaireFactory.EPQ
        isNeedProgress = true
        content =
            "请回答下列问题。每个答案无所谓正确与错误。这里没有对你不利的题目。请尽快回答，不要在每道题目上太多思索。" +
                    "回答时不要考虑应该怎样，只回答你平时是怎样的"
        val anwEPQ: List<String> = mutableListOf("是", "否")
        questionList = ArrayList()
        val contentEPQ: List<String> = mutableListOf( //1-10
            "你是否有许多不同的业余爱好？",
            "你是否在做任何事情以前都要停下来仔细思考？",
            "你的心境是否常有起伏？",
            "你曾有过明知是别人的功劳而你去接受奖励的事吗？",
            "你是否健谈？",
            "欠债会使你不安吗？",
            "你曾无缘无故觉得“真是难受”吗？",
            "你曾贪图过份外之物吗？",
            "你是否在晚上小心翼翼地关好门窗？",
            "你是否比较活跃？",  //11-20
            "你在见到一小孩或一动物受折磨时是否会感到非常难过？",
            "你是否常常为自己不该作而作了的事，不该说而说了的话而紧张吗？",
            "你喜欢跳降落伞吗？",
            "通常你能在热闹联欢会中尽情地玩吗？",
            "你容易激动吗？",
            "你曾经将自己的过错推给别人吗？",
            "你喜欢会见陌生人吗？",
            "你是否相信保险制度是一种好办法？",
            "你是一个容易伤感情的人吗？",
            "你所有的习惯都是好的吗？",  //21-30
            "在社交场合你是否总不愿露头角？",
            "你会服用奇异或危险作用的药物吗？",
            "你常有“厌倦”之感吗？",
            "你曾拿过别人的东西吗（哪怕一针一线）？",
            "你是否常爱外出？",
            "你是否从伤害你所宠爱的人而感到乐趣？",
            "你常为有罪恶之感所苦恼吗？",
            "你在谈论中是否有时不懂装懂？",
            "你是否宁愿去看书而不愿去多见人？",
            "你有要伤害你的仇人吗？",  //31-40
            "你觉得自己是一个神经过敏的人吗？",
            "对人有所失礼时你是否经常要表示歉意？",
            "你有许多朋友吗？",
            "你是否喜爱讲些有时确能伤害人的笑话？",
            "你是一个多忧多虑的人吗？",
            "你在童年是否按照吩咐要做什么便做什么，毫无怨言？",
            "你认为你是一个乐天派吗？",
            "你很讲究礼貌和整洁吗？",
            "你是否总在担心会发生可怕的事情？",
            "你曾损坏或遗失过别人的东西吗？",  //41-50
            "交新朋友时一般是你采取主动吗？",
            "当别人向你诉苦时，你是否容易理解他们的苦衷？",
            "你认为自己很紧张，如同“拉紧的弦”一样吗？",
            "在没有废纸篓时，你是否将废纸扔在地板上？",
            "当你与别人在一起时，你是否言语很少？",
            "你是否认为结婚制度是过时了，应该废止？",
            "你是否有时感到自己可怜？",
            "你是否有时有点自夸？",
            "你是否很容易将一个沉寂的集会搞得活跃起来？",
            "你是否讨厌那种小心翼翼地开车的人？",  //51-60
            "你为你的健康担忧吗？",
            "你曾讲过什么人的坏话吗？",
            "你是否喜欢对朋友讲笑话和有趣的故事？",
            "你小时候曾对父母粗暴无礼吗？",
            "你是否喜欢与人混在一起？",
            "你如知道自己工作有错误，这会使你感到难过吗？",
            "你患失眠吗？",
            "你吃饭前必定洗手吗？",
            "你常无缘无故感到无精打采和倦怠吗？",
            "和别人玩游戏时，你有过欺骗行为吗？",  //61-70
            "你是否喜欢从事一些动作迅速的工作？",
            "你的母亲是一位善良的妇人吗？",
            "你是否常常觉得人生非常无味？",
            "你曾利用过某人为自己取得好处吗？",
            "你是否常常参加许多活动，超过你的时间所允许？",
            "是否有几个人总在躲避你？",
            "你是否为你的容貌而非常烦恼？",
            "你是否觉得人们为了未来有保障而办理储蓄和保险所花的时间太多？",
            "你曾有过不如死了为好的愿望吗？",
            "如果有把握永远不会被别人发现，你会逃税吗？",  //71-80
            "你能使一个集会顺利进行吗？",
            "你能克制自己不对人无礼吗？",
            "遇到一次难堪的经历后，你是否在一段很长的时间内还感到难受？",
            "你患有“神经过敏”吗？",
            "你曾经故意说些什么来伤害别人的感情吗？",
            "你与别人的友谊是否容易破裂，虽然不是你的过错？",
            "你常感到孤单吗？",
            "当人家寻你的差错,找你工作中的缺点时,你是否容易在精神上受挫伤？",
            "你赴约会或上班曾迟到过吗？",
            "你喜欢忙忙碌碌地过日子吗？",  //81-88
            "你愿意别人怕你吗？",
            "你是否觉得有时浑身是劲，而有时又是懒洋洋的吗？",
            "你有时把今天应做的事拖到明天去做吗？",
            "别人认为你是生气勃勃吗？",
            "别人是否对你说了许多谎话？",
            "你是否容易对某些事物容易冒火？",
            "当你犯了错误时，你是否常常愿意承认它？",
            "你会为一动物落入圈套被捉拿而感到很难过吗？"
        )
        for (i in contentEPQ.indices) {
            (questionList as ArrayList<QuestionBase>).add(
                QuestionSelect(
                    contentEPQ[i],
                    QuestionViewFactory.SINGLE_CHOICE,
                    anwEPQ
                )
            )
        }
    }

    override fun getResult(): String {
        return ""
    }

    override fun saveFile() {
        val fileOutputStream = initFile()
        if (fileOutputStream != null && questionList != null) {
            var Escore = 0
            var Nscore = 0
            var Pscore = 0
            var Lscore = 0
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
                        if (question.index == 0) {
                            if (EindexList.contains(index)) {
                                Escore++
                            } else if (NindexList.contains(index)) {
                                Nscore++
                            } else if (PindexList.contains(index)) {
                                Pscore++
                            } else if (LindexList.contains(index)) {
                                Lscore++
                            }
                        } else {
                            if (EindexReverseList.contains(index)) {
                                Escore++
                            } else if (PindexReverseList.contains(index)) {
                                Pscore++
                            } else if (LindexReverseList.contains(index)) {
                                Lscore++
                            }
                        }
                    }
                }
                fileOutputStream!!.write("Escore: $Escore\r\n".toByteArray())
                fileOutputStream.write("Nscore: $Nscore\r\n".toByteArray())
                fileOutputStream.write("Pscore: $Pscore\r\n".toByteArray())
                fileOutputStream.write("Lscore: $Lscore\r\n".toByteArray())
                val totalContent = getTotalContent(Escore, Nscore, Pscore, Lscore)
                fileOutputStream.write(totalContent.toByteArray())

                val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                val currentTime = sdf.format(Date())
                fileOutputStream.write("Current Time: $currentTime\r\n".toByteArray())

                fileOutputStream.flush()
                fileOutputStream.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }


    private fun getTotalContent(Escore: Int, Nscore: Int, Pscore: Int, Lscore: Int): String {
        val content = StringBuilder()
        if (Escore > 15) {
            content.append("表示人格外向，可能是好交际，渴望刺激和冒险，情感易于冲动。\n")
        } else if (Escore < 8) {
            content.append("表示人格内向，如好静，富于内省，不喜欢刺激，喜欢有秩序的生活方式，情绪比较稳定。\n")
        }
        if (Nscore > 14) {
            content.append("焦虑、忧心仲仲、常郁郁不乐，有强烈情绪反应，甚至出现不够理智的行为\n")
        } else if (Nscore < 9) {
            content.append("情绪稳定。\n")
        }
        if (Pscore > 8) {
            content.append("可能是孤独、不关心他人，难以适应外部环境，不近人情，与别人不友好，喜欢寻衅搅扰，喜欢干奇特的事情，并且不顾危险。\n")
        }
        if (Lscore > 18) {
            content.append("被试者有掩饰倾向，测验结果可能失真。\n")
        }
        return content.toString()
    }
}