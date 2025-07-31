package com.example.phychosiolz.main.mine.questionnaire.Questionnaire.manger

import androidx.annotation.StringDef
import com.example.phychosiolz.data.room.model.User
import com.example.phychosiolz.main.mine.questionnaire.Questionnaire.model.questionnaireLIst.QuestionnaireBDIII
import com.example.phychosiolz.main.mine.questionnaire.Questionnaire.model.questionnaireLIst.QuestionnaireBIG5
import com.example.phychosiolz.main.mine.questionnaire.Questionnaire.model.questionnaireLIst.QuestionnaireEPQ
import com.example.phychosiolz.main.mine.questionnaire.Questionnaire.model.questionnaireLIst.QuestionnaireGAD7
import com.example.phychosiolz.main.mine.questionnaire.Questionnaire.model.questionnaireLIst.QuestionnairePANAS
import com.example.phychosiolz.main.mine.questionnaire.Questionnaire.model.questionnaireLIst.QuestionnairePANASX
import com.example.phychosiolz.main.mine.questionnaire.Questionnaire.model.questionnaireLIst.QuestionnairePHQ9
import com.example.phychosiolz.main.mine.questionnaire.Questionnaire.model.questionnaireLIst.QuestionnaireSDS
import com.example.phychosiolz.main.mine.questionnaire.Questionnaire.model.questionnaireLIst.QuestionnaireSTAIS
import java.util.Arrays

//简单工厂
object QuestionnaireFactory {
    private var tagList: List<String>? = null

    //抑郁焦虑问卷
    const val PHQ9 = "PHQ-9"
    const val GAD7 = "GAD-7"
    const val BDIII = "BDI-II"
    const val STAIS = "STAIS"
    const val SDS = "SDS"

    //性格问卷
    const val PANAS = "PANAS"
    const val PANASX = "PANAX"

    //情绪问卷
    const val BIG5 = "BIG5"
    const val EPQ = "EPQ"

    //空问卷 异常
    const val NONE = "NONE"

    init {
        tagList = Arrays.asList(PHQ9, GAD7, BDIII, STAIS, SDS, EPQ, BIG5, PANAS, PANASX)
    }

    fun getQuestionnaire(questionnaire: String?): QuestionnaireBase? {
        when (questionnaire) {
            PHQ9 -> return QuestionnairePHQ9()
            GAD7 -> return QuestionnaireGAD7()
            BDIII -> return QuestionnaireBDIII()
            PANAS -> return QuestionnairePANAS()
            PANASX -> return QuestionnairePANASX()
            STAIS -> return QuestionnaireSTAIS()
            EPQ -> return QuestionnaireEPQ()
            SDS -> return QuestionnaireSDS()
            BIG5 -> return QuestionnaireBIG5()
        }
        return null
    }

    fun getFilePath(user: User, @QuestionName questionnaire: String?): String? {
//        val root: String = FileUtils.getInstance().getRootDir()
//        val ANXIETY = "anxiety/"
//        val CHARACTER = "character/"
//        val MOOD = "mood/"
//        return when (questionnaire) {
//            PHQ9, GAD7, BDIII, STAIS, SDS -> root + user.getName() + FileUtils.getInstance().QUESTIONNAIRE + ANXIETY
//            EPQ, BIG5 -> root + user.getName() + FileUtils.getInstance().QUESTIONNAIRE + CHARACTER
//            PANAS, PANASX -> root + user.getName() + FileUtils.getInstance().QUESTIONNAIRE + MOOD
//            else -> null
//        }
        return null
    }

    fun isQuestionTag(@QuestionName questionnaire: String): Boolean {
//        for (str in tagList!!) {
//            if (questionnaire == str) {
//                return true
//            }
//        }
        return false
    }


    @StringDef(*[PHQ9, GAD7, BDIII, PANAS, STAIS, PANASX, SDS, BIG5, EPQ, NONE])
    @Retention(
        AnnotationRetention.SOURCE
    )
    annotation class QuestionName
}