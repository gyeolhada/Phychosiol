package com.example.phychosiolz.main.mine.questionnaire.Question.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;


import com.example.phychosiolz.R;
import com.example.phychosiolz.utils.ViewUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * 程度题刻度条
 * 通过重写onDraw，绘制刻度
 */
public class QuestionLevelTextView extends View {

    private Paint mLevelTextPaint;

    private int width;

    private List<String> mList;

    public QuestionLevelTextView(Context context) {
        this(context, null);
    }

    public QuestionLevelTextView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public QuestionLevelTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {

        //画笔 绘制文字
        mLevelTextPaint = new Paint();
        mLevelTextPaint.setColor(getResources().getColor(R.color.background_green));
        mLevelTextPaint.setAntiAlias(true);
        mLevelTextPaint.setStyle(Paint.Style.FILL);
        mLevelTextPaint.setTextSize(ViewUtil.INSTANCE.sp2px(getContext(), 8));

        mList = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            int tmp = (i + 1) * 10;
            mList.add(String.valueOf(tmp));
        }
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        width = measureDimension(ViewUtil.INSTANCE.dpToPx(getContext(),361), widthMeasureSpec);
        int height = measureDimension(ViewUtil.INSTANCE.dpToPx(getContext(), 20), heightMeasureSpec);
        setMeasuredDimension(width, height);
    }

    public int measureDimension(int defaultSize, int measureSpec) {
        int result;

        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);

        if (specMode == MeasureSpec.EXACTLY) {
            result = specSize;
        } else {
            result = defaultSize;   //UNSPECIFIED
            if (specMode == MeasureSpec.AT_MOST) {
                result = Math.min(result, specSize);
            }
        }
        return result;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.save();
        float indexWidth;
        float tmp = ViewUtil.INSTANCE.dpToPx(getContext(), 10);
        indexWidth = (float) ((width - ViewUtil.INSTANCE.dpToPx(getContext(), 20)) / (mList.size() - 1));

        //根据下标数均匀分布
        for (int i = 0; i < mList.size(); i++) {
            String text = mList.get(i);
            if (i == 0) {
                canvas.drawText(text, tmp, ViewUtil.INSTANCE.dpToPx(getContext(), 6.5f), mLevelTextPaint);
            } else {
                canvas.drawText(text, tmp - ViewUtil.INSTANCE.dpToPx(getContext(), 5), ViewUtil.INSTANCE.dpToPx(getContext(), 8), mLevelTextPaint);

            }
            tmp += indexWidth;
        }

    }

    public void setStringList(List<String> list) {
        mList = list;
        invalidate();
    }
}
