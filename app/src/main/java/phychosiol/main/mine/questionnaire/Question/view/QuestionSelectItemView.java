package com.example.phychosiolz.main.mine.questionnaire.Question.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.phychosiolz.R;


/**
 * 选择题 选项
 * 通过单选框、textview组成
 */
public class QuestionSelectItemView extends ConstraintLayout {

    private TextView mAnswer;
    private RadioButton mRadioButton;
    private ConstraintLayout container;

    private boolean isSelector = false;

    public QuestionSelectItemView(@NonNull  Context context) {
        this(context, null);
    }

    public QuestionSelectItemView(@NonNull Context context, @Nullable  AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public QuestionSelectItemView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    private void initView() {
        LayoutInflater.from(getContext()).inflate(R.layout.question_select_item_new, this, true);
        mAnswer = findViewById(R.id.answer_content);
        mRadioButton= findViewById(R.id.radioButton);
        container = findViewById(R.id.item_container);

        mRadioButton.setChecked(false);
        container.setBackgroundResource(R.drawable.shape_question_item_uncheck);
    }


    public void setContent(String content) {
        mAnswer.setText(content);
    }

    //确认 或 取消
    public void updateView() {
        isSelector = !isSelector;
        if (isSelector) {
            mRadioButton.setChecked(true);
            container.setBackgroundResource(R.drawable.shape_question_item_check);
        } else {
            mRadioButton.setChecked(false);
            container.setBackgroundResource(R.drawable.shape_question_item_uncheck);
        }
    }
}
