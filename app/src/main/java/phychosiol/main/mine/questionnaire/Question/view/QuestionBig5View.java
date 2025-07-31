package com.example.phychosiolz.main.mine.questionnaire.Question.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.phychosiolz.R;
import com.example.phychosiolz.main.mine.questionnaire.Question.model.QuestionBase;
import com.example.phychosiolz.main.mine.questionnaire.Question.model.QuestionBig5;


/**
 * big5 问题
 * 包含程度条、问题描述、最小描述、最大描述
 */

public class QuestionBig5View extends QuestionBaseView {

    private QuestionBig5 questionBig5;
    private Pair<String, String> mark;

    public QuestionBig5View(@NonNull Context context) {
        this(context, null);
    }

    public QuestionBig5View(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public QuestionBig5View(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void setQuestion(int index, QuestionBase questionnaireBase) {
        questionBig5 = (QuestionBig5) questionnaireBase;
        mark = questionBig5.getMark();
        initView();
    }

    private void initView() {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.question_big5_view, this, true);
        TextView tvMin = view.findViewById(R.id.tv_min_str);
        TextView tvMax = view.findViewById(R.id.tv_max_str);
        QuestionBig5Level questionBig5Level = view.findViewById(R.id.level);
        questionBig5Level.setIActionUpListener(level -> {
            if (!questionBig5.isFinish()) {
                iClickListener.onClick(true);
            }
            questionBig5.setAnswer(level);
        });
        tvMin.setText(mark.first);
        tvMax.setText(mark.second);

    }
}
