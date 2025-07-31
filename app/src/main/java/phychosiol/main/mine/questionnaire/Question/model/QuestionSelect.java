package com.example.phychosiolz.main.mine.questionnaire.Question.model;

import java.util.List;

/**
 * 选择题
 * 每个选项描述以及结果
 */
//选择题
public class QuestionSelect extends QuestionBase {
    private List<String> answerList;

    private int selectIndex = -1;


    public QuestionSelect(String content, String type, List<String> answerList) {
        super(content, type);
        this.answerList = answerList;
    }

    public List<String> getAnswerList() {
        return answerList;
    }

    public void setAnswerList(List<String> answerList) {
        this.answerList = answerList;
    }

    @Override
    public boolean isFinish() {
        return selectIndex != -1;
    }

    public String getAnswer() {
        if (isFinish()) {
            return answerList.get(selectIndex);
        } else {
            return null;
        }
    }

    @Override
    public int getIndex() {
        return selectIndex;
    }

    public void selectAnswer(int selectIndex) {
        this.selectIndex = selectIndex;
    }

}
