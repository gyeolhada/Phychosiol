package com.example.phychosiolz.main.mine.questionnaire.Question.model;

import java.util.List;

/**
 * 程度题
 * 包含每个程度的描述、选择结果
 */
public class QuestionLevel extends QuestionBase {

    private final List<String> levelTextList;
    private int answer = 0;

    public QuestionLevel(String content, String type, List<String> levelTextList) {
        super(content, type);
        this.levelTextList = levelTextList;
    }

    @Override
    public boolean isFinish() {
        return true;
    }

    @Override
    public String getAnswer() {
        return answer + "";
    }

    @Override
    public int getIndex() {
        return -1;
    }

    public List<String> getLevelList() {
        return levelTextList;
    }

    public void setAnswer(int answer) {
        this.answer = answer;
    }

}
