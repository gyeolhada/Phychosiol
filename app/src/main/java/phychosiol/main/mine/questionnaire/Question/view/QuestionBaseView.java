package com.example.phychosiolz.main.mine.questionnaire.Question.view;

import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.phychosiolz.main.mine.questionnaire.Question.model.QuestionBase;


/**
 * 问题view 基类
 * 需要setQuestion设置问题数据
 * setIClickListener接口主要统计完成问题数
 */
public abstract class QuestionBaseView extends ConstraintLayout {
    protected IClickListener iClickListener;
    public QuestionBaseView(@NonNull Context context) {
        this(context, null);
    }

    public QuestionBaseView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public QuestionBaseView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public abstract void setQuestion(int index, QuestionBase questionnaireBase);

    public void setIClickListener(IClickListener iClickListener) {
        this.iClickListener = iClickListener;
    }

    public interface IClickListener {
        void onClick(boolean isAdd);
    }
}
