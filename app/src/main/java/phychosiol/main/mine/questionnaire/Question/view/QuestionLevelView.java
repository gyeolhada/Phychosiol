package com.example.phychosiolz.main.mine.questionnaire.Question.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.phychosiolz.R;
import com.example.phychosiolz.main.mine.questionnaire.Question.model.QuestionBase;
import com.example.phychosiolz.main.mine.questionnaire.Question.model.QuestionLevel;
import com.example.phychosiolz.utils.ViewUtil;


/**
 * 程度题
 * 通过进度条、刻度条以及TextView组成，需要先设置setQuestion
 */
public class QuestionLevelView extends QuestionBaseView {

    private TextView tvContent;

    private QuestionLevelTextView questionLevelTextView;

    private TextView indicator;

    private QuestionLevel questionLevel;

    public QuestionLevelView(@NonNull Context context) {
        this(context, null);
    }

    public QuestionLevelView(@NonNull Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public QuestionLevelView(@NonNull  Context context, @Nullable  AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    @Override
    public void setQuestion(int index, QuestionBase questionnaireBase) {
        questionLevel = (QuestionLevel) questionnaireBase;
        tvContent.setText(questionLevel.content);
        questionLevelTextView.setStringList(questionLevel.getLevelList());
    }



    private void initView() {
        LayoutInflater.from(getContext()).inflate(R.layout.question_level_view, this, true);
        tvContent = findViewById(R.id.question_content);
        SeekBar seekBar = findViewById(R.id.seekBar);

        questionLevelTextView = findViewById(R.id.level_text_view);

        indicator = findViewById(R.id.tv_indicator);

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                int nowProgress = seekBar.getProgress();
                int maxProgress = seekBar.getMax();
                double ratio = nowProgress / (maxProgress * 1.0);
                LayoutParams layoutParams = (LayoutParams) indicator.getLayoutParams();
                layoutParams.setMarginStart((int) (seekBar.getWidth() * ratio - ViewUtil.INSTANCE.dpToPx(getContext(), 10)));
                indicator.setLayoutParams(layoutParams);
                indicator.setText(String.valueOf(nowProgress));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                int nowProgress = seekBar.getProgress();
                int maxProgress = seekBar.getMax();
                double ratio = nowProgress / (maxProgress * 1.0);
                LayoutParams layoutParams = (LayoutParams) indicator.getLayoutParams();
                layoutParams.setMarginStart((int) (seekBar.getWidth() * ratio - ViewUtil.INSTANCE.dpToPx(getContext(), 10)));
                indicator.setLayoutParams(layoutParams);
                indicator.setText(String.valueOf(nowProgress));
                indicator.setVisibility(View.VISIBLE);
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                int nowProgress = seekBar.getProgress();
                questionLevel.setAnswer(nowProgress);
                indicator.setText(String.valueOf(seekBar.getProgress()));
                indicator.setVisibility(View.INVISIBLE);
            }
        });
    }
}
