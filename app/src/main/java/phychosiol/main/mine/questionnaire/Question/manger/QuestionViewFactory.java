package com.example.phychosiolz.main.mine.questionnaire.Question.manger;

import android.content.Context;

import com.example.phychosiolz.main.mine.questionnaire.Question.model.QuestionBase;
import com.example.phychosiolz.main.mine.questionnaire.Question.view.QuestionBaseView;
import com.example.phychosiolz.main.mine.questionnaire.Question.view.QuestionBig5View;
import com.example.phychosiolz.main.mine.questionnaire.Question.view.QuestionLevelView;
import com.example.phychosiolz.main.mine.questionnaire.Question.view.QuestionSelectView;


/**
 * 工厂模式
 * 问卷 问题view
 * 单选题、程度题、以及基于Big5问卷的view
 */
public class QuestionViewFactory {

    public static final String SINGLE_CHOICE = "Single choice";
    public static final String LEVEL_CHOICE = "level choice";
    public static final String BIG_5 = "BIG_5";

    /**
     *
     * @param context 上下文
     * @param index  问题序号
     * @param questionBase 问题数据（描述、选项等）
     * @return
     */
    public static QuestionBaseView getQuestion(Context context, int index, QuestionBase questionBase) {
        String type = questionBase.type;
        switch (type) {
            case SINGLE_CHOICE:
                QuestionBaseView questionSelectView = new QuestionSelectView(context);
                questionSelectView.setQuestion(index, questionBase);
                return questionSelectView;
            case BIG_5:
                QuestionBaseView questionBig5View = new QuestionBig5View(context);
                questionBig5View.setQuestion(index, questionBase);
                return questionBig5View;
            case LEVEL_CHOICE:
                QuestionBaseView questionLevelView = new QuestionLevelView(context);
                questionLevelView.setQuestion(index, questionBase);
                return questionLevelView;
            default:
                return null;
        }
    }
}
