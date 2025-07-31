package com.example.phychosiolz.main.mine.questionnaire.Questionnaire.model.questionnaireLIst

import android.util.Pair
import com.example.phychosiolz.main.mine.questionnaire.Question.manger.QuestionViewFactory
import com.example.phychosiolz.main.mine.questionnaire.Question.model.QuestionBase
import com.example.phychosiolz.main.mine.questionnaire.Question.model.QuestionBig5
import com.example.phychosiolz.main.mine.questionnaire.Questionnaire.manger.QuestionnaireBase
import com.example.phychosiolz.main.mine.questionnaire.Questionnaire.manger.QuestionnaireFactory
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date

/**
 * Big5问卷
 */
class QuestionnaireBIG5 : QuestionnaireBase() {
    override fun initQuestionnaire() {
        tag = QuestionnaireFactory.BIG5
        title = QuestionnaireFactory.BIG5
        isNeedProgress = true
        content = "在以下的每个程度表中，指出你一般最想描述的点。假使态度中等，就将记号打在中点。"
        questionList = ArrayList()
        val pairList: MutableList<Pair<String, String>> = ArrayList()
        //1-5
        pairList.add(Pair("迫切的", "冷静的"))
        pairList.add(Pair("群居的", "独处的"))
        pairList.add(Pair("爱幻想的", "现实的"))
        pairList.add(Pair("礼貌的", "粗鲁的"))
        pairList.add(Pair("整洁的", "混乱的"))
        //6-10
        pairList.add(Pair("谨慎的", "自信的"))
        pairList.add(Pair("乐观的", "悲观的"))
        pairList.add(Pair("理论的", "实践的"))
        pairList.add(Pair("大方的", "自私的"))
        pairList.add(Pair("果断的", "开放的"))
        //11-15
        pairList.add(Pair("泄气的", "乐观的"))
        pairList.add(Pair("外显的", "内隐的"))
        pairList.add(Pair("跟从想象的", "服从权威的"))
        pairList.add(Pair("热情的", "冷漠的"))
        pairList.add(Pair("自制的", "易受干扰的"))
        //16-20
        pairList.add(Pair("易难堪的", "老练的"))
        pairList.add(Pair("开朗的", "冷淡的"))
        pairList.add(Pair("追求新奇的", "追求常规的"))
        pairList.add(Pair("合作的", "独立的"))
        pairList.add(Pair("喜欢次序的", "适应喧闹的"))
        //21-25
        pairList.add(Pair("易分心的", "镇静的"))
        pairList.add(Pair("保守的", "有思想的"))
        pairList.add(Pair("适于模棱两可的", "适于轮廓清楚的"))
        pairList.add(Pair("信任的", "怀疑的"))
        pairList.add(Pair("守时的", "拖延的"))
        for (i in pairList.indices) {
            val pair = pairList[i]
            val content = pair.first + "->" + pair.second
            (questionList as ArrayList<QuestionBase>).add(QuestionBig5(content, QuestionViewFactory.BIG_5, pair))
        }
    }

    override fun getResult(): String {
        return ""
    }

    override fun saveFile() {
        val fileOutputStream = initFile()
        if (fileOutputStream != null && questionList != null) {
            var index: Int
            var tmp: Int
            val scores = mutableListOf(0, 0, 0, 0, 0)
            val label: List<String> = mutableListOf("道德感", "适应性", "社交性", "开放性", "利他性")
            try {
                for (i in questionList!!.indices) {
                    index = i + 1
                    val question = questionList!![i]
                    if (question.answer != null) {
                        fileOutputStream.write("$index、".toByteArray())
                        fileOutputStream.write(question.content.toByteArray())
                        fileOutputStream.write(" ".toByteArray())
                        fileOutputStream.write(question.answer!!.toByteArray())
                        fileOutputStream.write("\r\n".toByteArray())
                        tmp = i % 5
                        scores[tmp] = scores[tmp] + question.index
                    }
                }
                for (i in label.indices) {
                    fileOutputStream.write(
                        """${label[i]} ${scores[i]}
""".toByteArray()
                    )
                }

                val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                val currentTime = sdf.format(Date())
                fileOutputStream.write("Current Time: $currentTime\r\n".toByteArray())

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