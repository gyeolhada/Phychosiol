package com.example.phychosiolz.main.mine.questionnaire.Question.model;

import android.util.Pair;

/**
 * Big5 问题
 * 程度型问题，包含最小、最大两种描述，使用pair存储
 * index 记录结果
 */
public class QuestionBig5 extends QuestionBase {

    private Pair<String, String> mark;

    private int index;

    public QuestionBig5(String content, String type, Pair<String, String> mark) {
        super(content, type);
        this.content = content;
        this.type = type;
        this.mark = mark;

    }

    @Override
    public boolean isFinish() {
        return index != 0;
    }

    @Override
    public String getAnswer() {
        return index + "";
    }

    public void setAnswer(int index) {
        this.index = index;
    }

    @Override
    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public Pair<String, String> getMark() {
        return mark;
    }

    public void setMark(Pair<String, String> mark) {
        this.mark = mark;
    }

}
