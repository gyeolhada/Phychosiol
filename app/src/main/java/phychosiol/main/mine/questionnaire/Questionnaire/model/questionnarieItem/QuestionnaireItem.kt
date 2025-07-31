package com.example.phychosiolz.main.mine.questionnaire.Questionnaire.model.questionnarieItem

import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import com.example.phychosiolz.main.mine.questionnaire.Questionnaire.manger.QuestionnaireFactory.QuestionName

/**
 * 外部问卷子项
 * 图标、边框颜色、标题以及标识
 */
class QuestionnaireItem(
    @JvmField @field:DrawableRes var resId: Int,
    @JvmField @field:ColorRes var color: Int,
    @JvmField var title: String,
    @field:QuestionName @param:QuestionName var tag: String
)