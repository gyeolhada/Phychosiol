package com.example.phychosiolz.main.mine.questionnaire.Question.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;

import com.example.phychosiolz.R;
import com.example.phychosiolz.utils.ViewUtil;

/**
 * big5 程度条
 * 通过自定义view的方式，使用不同画笔，绘制代表陈鼓的的矩形
 * 通过setLevel方法，更新view
 */
public class QuestionBig5Level extends View {
    private int width;
    private Paint mContainPaint;

    private Paint mLinePaint;
    private Paint mCurrentPaint;

    private int level = 0;
    private final int maxLevel = 5;

    private IActionUpListener iActionUpListener;

    public QuestionBig5Level(Context context) {
        this(context, null);
    }

    public QuestionBig5Level(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public QuestionBig5Level(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mContainPaint = new Paint();
        mContainPaint.setColor(getResources().getColor(R.color.grey));
        mContainPaint.setAntiAlias(true);
        mContainPaint.setStyle(Paint.Style.FILL);

        mCurrentPaint = new Paint();
        mCurrentPaint.setColor(getResources().getColor(R.color.background_green));
        mCurrentPaint.setAntiAlias(true);
        mCurrentPaint.setStyle(Paint.Style.FILL);

        mLinePaint = new Paint();
        mLinePaint.setColor(getResources().getColor(R.color.white));
        mCurrentPaint.setStyle(Paint.Style.FILL);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        width = measureDimension(ViewUtil.INSTANCE.dpToPx(getContext(), 300), widthMeasureSpec);
        int height = measureDimension(ViewUtil.INSTANCE.dpToPx(getContext(), 16), heightMeasureSpec);
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
        int startY = ViewUtil.INSTANCE.dpToPx(getContext(), 4);
        int endY = ViewUtil.INSTANCE.dpToPx(getContext(), 20);
        canvas.drawRect(0, startY, width, endY, mContainPaint);
        int lineStep = width / 5;
        int X = lineStep;
        int lineWidthHalf = ViewUtil.INSTANCE.dpToPx(getContext(), 1);
        for (int i = 0; i < 4; i++) {
            canvas.drawRect(X - lineWidthHalf, startY, X + lineWidthHalf, endY, mLinePaint);
            X += lineStep;
        }
        int startX, endX;
        for (int i = 0; i < level; i++) {
            if (i == 0) {
                startX = 0;
            } else {
                startX = i * lineStep + lineWidthHalf;
            }
            if (i == maxLevel - 1) {
                endX = width;
            } else {
                endX = (i + 1) * lineStep - lineWidthHalf;
            }
            canvas.drawRect(startX, startY, endX, endY, mCurrentPaint);
        }
    }

    private void setLevel(int level) {
        this.level = level;
        invalidate();
    }

    @Override
    public boolean performClick() {
        return super.performClick();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                getParent().requestDisallowInterceptTouchEvent(true);//这句话是告诉父view，我的事件自己处理 不让父view拦截自己的事件
                setLevel(getArea(event.getX()));
            case MotionEvent.ACTION_MOVE:
                setLevel(getArea(event.getX()));
                break;
            case MotionEvent.ACTION_UP:
                getParent().requestDisallowInterceptTouchEvent(false);//这句话是告诉父view可以阻止我拦截我
                iActionUpListener.onActionUp(getArea(event.getX()));
                performClick();
                break;
        }
        return true;
    }

    private int getArea(float rawX) {
        for (int i = 0; i < 5; i++) {
            if (rawX > (i / 5.0) * width && rawX <= width * (i + 1) / 5.0) {
                return i + 1;
            }
        }
        return 0;
    }

    public void setIActionUpListener(IActionUpListener iActionUpListener) {
        this.iActionUpListener = iActionUpListener;
    }

    public interface IActionUpListener {
        void onActionUp(int level);
    }
}
