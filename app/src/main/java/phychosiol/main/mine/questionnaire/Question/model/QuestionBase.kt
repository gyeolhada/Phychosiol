package com.example.phychosiolz.main.mine.questionnaire.Question.model

/**
 * 问题基类
 * 包含问题描述及问题类型
 */
abstract class QuestionBase(@JvmField var content: String, @JvmField var type: String) {
    abstract val isFinish: Boolean
    abstract val answer: String?
    abstract val index: Int
}