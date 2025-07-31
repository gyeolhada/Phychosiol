package com.example.phychosiolz.main.mine.questionnaire.Question.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.phychosiolz.R;
import com.example.phychosiolz.main.mine.questionnaire.Question.model.QuestionBase;
import com.example.phychosiolz.main.mine.questionnaire.Question.model.QuestionSelect;

import java.util.ArrayList;
import java.util.List;

/**
 * 选择题
 * 通过addView方向，将选项加入到LinearLayout中
 */
public class QuestionSelectView extends QuestionBaseView {

    private TextView mQuestionContent;
    private LinearLayout mItemContainer;

    private QuestionSelect mQuestion;


    private List<QuestionSelectItemView> itemViewList;

    public QuestionSelectView(@NonNull Context context) {
        this(context, null);
    }

    public QuestionSelectView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public QuestionSelectView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    @Override
    public void setQuestion(int index, QuestionBase questionnaireBase) {
        mQuestion = (QuestionSelect) questionnaireBase;
        String content = (index + 1) + "、" + mQuestion.content;
        mQuestionContent.setText(content);
        addSelectItem();
    }

    private void initView() {
        LayoutInflater.from(getContext()).inflate(R.layout.question_selector_view, this, true);
        mQuestionContent = findViewById(R.id.question_content);
        mItemContainer = findViewById(R.id.answer_container);

        itemViewList = new ArrayList<>();
    }

    //增加选项
    private void addSelectItem() {
        List<String> answerList = mQuestion.getAnswerList();
        for (int i = 0; i < answerList.size(); i++) {
            QuestionSelectItemView itemView = new QuestionSelectItemView(getContext());
            itemView.setContent(answerList.get(i));
            mItemContainer.addView(itemView);
            itemViewList.add(itemView);

            int finalI = i;
            itemView.setOnClickListener(v -> {
                int lastIndex = mQuestion.getIndex();
                //单选
                if (lastIndex == -1) {
                    // 没选 -> 已选
                    lastIndex = finalI;
                    if (iClickListener != null) {
                        iClickListener.onClick(true);
                    }
                } else if (lastIndex == finalI) {
                    // 已选 -> 取消
                    lastIndex = -1;
                    if (iClickListener != null) {
                        iClickListener.onClick(false);
                    }
                } else {
                    // 更换选项
                    QuestionSelectItemView oldSelectItem = itemViewList.get(lastIndex);
                    oldSelectItem.updateView();
                    lastIndex = finalI;
                }
                itemView.updateView();
                mQuestion.selectAnswer(lastIndex);

            });
        }
    }



}
