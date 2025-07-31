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
 * PANAS问卷
 */
class QuestionnairePANAS : QuestionnaireBase() {
    override fun initQuestionnaire() {
        tag = QuestionnaireFactory.PANAS
        title = QuestionnaireFactory.PANAS
        isNeedProgress = true
        content = "请阅读每一个词语并根据你近”1-2星期”的实际情况在选择相应的答案"
        val anwPAN: List<String> =
            mutableListOf("几乎没有", "比较少", "中等程度", "比较多", "极其多")
        questionList = ArrayList()
        (questionList as ArrayList<QuestionBase>).add(QuestionSelect("感兴趣的", QuestionViewFactory.SINGLE_CHOICE, anwPAN))
        (questionList as ArrayList<QuestionBase>).add(QuestionSelect("心烦的", QuestionViewFactory.SINGLE_CHOICE, anwPAN))
        (questionList as ArrayList<QuestionBase>).add(QuestionSelect("精神活力高的", QuestionViewFactory.SINGLE_CHOICE, anwPAN))
        (questionList as ArrayList<QuestionBase>).add(QuestionSelect("心神不宁的", QuestionViewFactory.SINGLE_CHOICE, anwPAN))
        (questionList as ArrayList<QuestionBase>).add(QuestionSelect("劲头足的", QuestionViewFactory.SINGLE_CHOICE, anwPAN))
        (questionList as ArrayList<QuestionBase>).add(QuestionSelect("内疚的", QuestionViewFactory.SINGLE_CHOICE, anwPAN))
        (questionList as ArrayList<QuestionBase>).add(QuestionSelect("恐惧的", QuestionViewFactory.SINGLE_CHOICE, anwPAN))
        (questionList as ArrayList<QuestionBase>).add(QuestionSelect("敌意的", QuestionViewFactory.SINGLE_CHOICE, anwPAN))
        (questionList as ArrayList<QuestionBase>).add(QuestionSelect("热情的", QuestionViewFactory.SINGLE_CHOICE, anwPAN))
        (questionList as ArrayList<QuestionBase>).add(QuestionSelect("自豪的", QuestionViewFactory.SINGLE_CHOICE, anwPAN))
        (questionList as ArrayList<QuestionBase>).add(QuestionSelect("易怒的", QuestionViewFactory.SINGLE_CHOICE, anwPAN))
        (questionList as ArrayList<QuestionBase>).add(QuestionSelect("警觉性高的", QuestionViewFactory.SINGLE_CHOICE, anwPAN))
        (questionList as ArrayList<QuestionBase>).add(QuestionSelect("害羞的", QuestionViewFactory.SINGLE_CHOICE, anwPAN))
        (questionList as ArrayList<QuestionBase>).add(QuestionSelect("备受鼓舞的", QuestionViewFactory.SINGLE_CHOICE, anwPAN))
        (questionList as ArrayList<QuestionBase>).add(QuestionSelect("紧张的", QuestionViewFactory.SINGLE_CHOICE, anwPAN))
        (questionList as ArrayList<QuestionBase>).add(QuestionSelect("意志坚定的", QuestionViewFactory.SINGLE_CHOICE, anwPAN))
        (questionList as ArrayList<QuestionBase>).add(QuestionSelect("注意力集中的", QuestionViewFactory.SINGLE_CHOICE, anwPAN))
        (questionList as ArrayList<QuestionBase>).add(QuestionSelect("坐立不安的", QuestionViewFactory.SINGLE_CHOICE, anwPAN))
        (questionList as ArrayList<QuestionBase>).add(QuestionSelect("有活力的", QuestionViewFactory.SINGLE_CHOICE, anwPAN))
        (questionList as ArrayList<QuestionBase>).add(QuestionSelect("害怕的", QuestionViewFactory.SINGLE_CHOICE, anwPAN))
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
                    if (question.answer != null) { // 添加非空检查
                        fileOutputStream.write(index.toByteArray())
                        fileOutputStream.write(question.content.toByteArray())
                        fileOutputStream.write("\r\n".toByteArray())
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