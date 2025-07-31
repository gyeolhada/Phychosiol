package com.example.phychosiolz.main.mine.questionnaire.Questionnaire.manger

import com.example.phychosiolz.R
import com.example.phychosiolz.main.mine.questionnaire.Questionnaire.manger.QuestionnaireFactory.BDIII
import com.example.phychosiolz.main.mine.questionnaire.Questionnaire.manger.QuestionnaireFactory.BIG5
import com.example.phychosiolz.main.mine.questionnaire.Questionnaire.manger.QuestionnaireFactory.EPQ
import com.example.phychosiolz.main.mine.questionnaire.Questionnaire.manger.QuestionnaireFactory.GAD7
import com.example.phychosiolz.main.mine.questionnaire.Questionnaire.manger.QuestionnaireFactory.PANAS
import com.example.phychosiolz.main.mine.questionnaire.Questionnaire.manger.QuestionnaireFactory.PANASX
import com.example.phychosiolz.main.mine.questionnaire.Questionnaire.manger.QuestionnaireFactory.PHQ9
import com.example.phychosiolz.main.mine.questionnaire.Questionnaire.manger.QuestionnaireFactory.QuestionName
import com.example.phychosiolz.main.mine.questionnaire.Questionnaire.manger.QuestionnaireFactory.SDS
import com.example.phychosiolz.main.mine.questionnaire.Questionnaire.manger.QuestionnaireFactory.STAIS
import com.example.phychosiolz.main.mine.questionnaire.Questionnaire.model.questionnarieItem.QuestionnaireItem

/**
 * 问卷项管理类，主要管理MineFragment上的问卷项显示
 *
 */
@Deprecated(
    "这是一个自定义view的例子，但是因为当时时间紧，使用了更简陋的方法重写。--zyd"
)
object QuestionnaireItemManger {
    //问卷数
    private const val questionnaireNum = 9

    //问卷标识
    @QuestionName
    private val tags = arrayOf<String>(
        PHQ9, GAD7, BDIII,
        PANAS, STAIS, PANASX,
        BIG5, SDS, EPQ
    )

    //图标
    private val icon = intArrayOf(
        R.drawable.icon_mine_phq,
        R.drawable.icon_mine_gad,
        R.drawable.icon_mine_bdi,
        R.drawable.icon_mine_panas,
        R.drawable.icon_mine_stais,
        R.drawable.icon_mine_panas_x,
        R.drawable.icon_mine_big5,
        R.drawable.icon_mine_sds,
        R.drawable.icon_mine_epq
    )

    //边框颜色
    private val color = intArrayOf( //            R.color.color_71CCD4,
        //            R.color.color_F8B996,
        //            R.color.color_70A3D8,
        //            R.color.color_CD5C5C,
        //            R.color.color_4EBCF2,
        //            R.color.color_98FB98,
        //            R.color.color_CC99FF,
        //            R.color.color_20b2aa,
        //            R.color.color_4169E1,
        R.color.angry,
        R.color.angry,
        R.color.angry,
        R.color.angry,
        R.color.angry,
        R.color.angry,
        R.color.angry,
        R.color.angry,
        R.color.angry,
        R.color.angry,
        R.color.angry
    )
    val questionnaireItemList: List<QuestionnaireItem>
        get() {
            val questionnaireItems: MutableList<QuestionnaireItem> = ArrayList()
            for (i in 0 until questionnaireNum) {
                questionnaireItems.add(QuestionnaireItem(icon[i], color[i], tags[i], tags[i]))
            }
            return questionnaireItems
        }

    /**
     * @param tag 问卷类型
     * @return 是否需要显示分数
     */
    fun isNeedScore(@QuestionName tag: String?): Boolean {
        return when (tag) {
            PHQ9, GAD7, BDIII, SDS -> true
            else -> false
        }
    }
}