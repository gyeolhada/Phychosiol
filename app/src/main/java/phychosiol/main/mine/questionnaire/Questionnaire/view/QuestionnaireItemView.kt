package com.example.phychosiolz.main.mine.questionnaire.Questionnaire.view

import android.content.Context
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.RippleDrawable
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import com.example.phychosiolz.R
import com.example.phychosiolz.main.mine.questionnaire.Questionnaire.model.questionnarieItem.QuestionnaireItem
import com.example.phychosiolz.utils.ViewUtil.dpToPx

/**
 * 问卷子项view
 * 判断是否需要分数
 * 通过sp记录上次测试时间
 */
@Deprecated(
    "这是一个自定义view的例子，但是因为当时时间紧，使用了更简陋的方法重写。--zyd"
)
class QuestionnaireItemView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : ConstraintLayout(context, attrs, 0) {
    private var mQuestionIg: ImageView? = null
    private var mQuestionTitle: TextView? = null
    private var mQuestionScore: TextView? = null
    private var mQuestionStatus: TextView? = null
    private var mContainer: ConstraintLayout? = null
    private var questionnaireItem: QuestionnaireItem? = null

    init {
        initView()
    }

    private fun initView() {
        LayoutInflater.from(context).inflate(R.layout.mine_questionnaire_item, this, true)
        mQuestionIg = findViewById(R.id.question_image)
        mQuestionTitle = findViewById(R.id.question_name)
        mQuestionScore = findViewById(R.id.question_score)
        mQuestionStatus = findViewById(R.id.question_status)
        mContainer = findViewById(R.id.container)
    }

    fun setQuestionItem(item: QuestionnaireItem?) {
        questionnaireItem = item
        val mGroupDrawable =
            ContextCompat.getDrawable(context, R.drawable.shape_bg_question_item) as RippleDrawable?
        val gradientDrawable = mGroupDrawable!!.findDrawableByLayerId(R.id.mask) as GradientDrawable
        val px = dpToPx(context, 3f)
        mQuestionIg!!.setImageResource(questionnaireItem!!.resId)
        gradientDrawable.setStroke(px, resources.getColor(questionnaireItem!!.color))
        mContainer!!.background = mGroupDrawable
        mQuestionTitle!!.text = questionnaireItem!!.title
        //updateStatus()
    }

    //待定 暂时用sp
    fun updateSource() {
//        String questionnaireItemTag = questionnaireItem.getTag();
//        if (!QuestionnaireItemManger.isNeedScore(questionnaireItemTag)) {
//            mQuestionScore.setVisibility(INVISIBLE);
//        } else {
//            SharedPreferencesHelper sharedPreferencesHelper = SharedPreferencesHelper.getInstance(MainApplication.getInstance());
//            String scoreKey = questionnaireItemTag + "_score";
//            String source = sharedPreferencesHelper.getString(scoreKey, "0") + "分";
//
//            Resources resources = getResources();
//            SpannableString spanSource = new SpannableString(source);
//            spanSource.setSpan(new AbsoluteSizeSpan(30, true), 0, source.length() - 1, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
//            spanSource.setSpan(new ForegroundColorSpan(resources.getColor(R.color.color_71CCD4)), 0, source.length() - 1, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
//            spanSource.setSpan(new AbsoluteSizeSpan(16, true), source.length() - 1, source.length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
//            spanSource.setSpan(new ForegroundColorSpan(resources.getColor(R.color.black)), source.length() - 1, source.length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
//            mQuestionScore.setText(spanSource);
//        }
    }

    fun updateStatus() {
//        SharedPreferencesHelper sharedPreferencesHelper = SharedPreferencesHelper.getInstance(getContext());
//        Resources resources = getResources();
//        String statusKey = questionnaireItem.getTag() + "_time";
//        String defaultStatus = "no test record";
//        String status = sharedPreferencesHelper.getString(statusKey, defaultStatus);
//        if (status.equals(defaultStatus)) {
//            mQuestionStatus.setText("开始测评");
//        } else {
//            Long timeGap = System.currentTimeMillis() - Long.parseLong(status);
//            String timeGapText;
//            String timeGapType = "none";
//            if (timeGap > 24 * 60 * 60 * 1000) {
//                timeGapText = "距离上次测评时间：" + timeGap / (24 * 60 * 60 * 1000) + "天";
//                timeGapType = "day";
//            } else if (timeGap > 60 * 60 * 1000) {
//                timeGapText = "距离上次测评时间：" + timeGap / (60 * 60 * 1000) + "小时";
//                timeGapType = "hour";
//            } else if (timeGap > 60 * 1000) {
//                timeGapText = "距离上次测评时间：" + timeGap / (60 * 1000) + "分钟";
//                timeGapType = "minute";
//            } else {
//                timeGapText = "刚完成测试哦！";
//            }
//            SpannableString spanStatus = new SpannableString(timeGapText);
//            spanStatus.setSpan(new AbsoluteCornerSize(16), 0, timeGapText.length() - 1, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
//            spanStatus.setSpan(new ForegroundColorSpan(resources.getColor(R.color.warning_red)), 0, timeGapText.length() - 1, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
//            switch (timeGapType) {
//                case "day":
//                    spanStatus.setSpan(new ForegroundColorSpan(resources.getColor(R.color.warning_red)), 9 ,timeGapText.length() - 1,Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
//                    break;
//                case "hour":
//                case "minute":
//                    spanStatus.setSpan(new ForegroundColorSpan(resources.getColor(R.color.warning_red)), 9 ,timeGapText.length() - 2,Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
//                    break;
//                default:
//                    break;
//            }
//            mQuestionStatus.setText(spanStatus);
//        }
    }
}