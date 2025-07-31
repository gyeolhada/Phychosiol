package com.example.phychosiolz.main.mine.questionnaire


import android.app.AlertDialog
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.Navigation
import com.example.phychosiolz.R
import com.example.phychosiolz.databinding.FragmentQuestionnaireBinding
import com.example.phychosiolz.main.mine.questionnaire.Question.manger.QuestionViewFactory
import com.example.phychosiolz.main.mine.questionnaire.Questionnaire.manger.QuestionnaireBase
import com.example.phychosiolz.main.mine.questionnaire.Questionnaire.manger.QuestionnaireFactory
import com.example.phychosiolz.repo.QuestionnaireRepository
import com.example.phychosiolz.view_model.LoginAndRegisterViewModel
import java.sql.Date
import java.sql.Time
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Calendar


class QuestionnaireFragment() : Fragment() {
    private lateinit var bind: FragmentQuestionnaireBinding
    private var questionnaireBase: QuestionnaireBase? = null
    private val viewModel: LoginAndRegisterViewModel by viewModels { LoginAndRegisterViewModel.Factory }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        requireActivity().window.statusBarColor =
            ResourcesCompat.getColor(resources, R.color.white, null)
        val tag = arguments?.getString("tag")
        questionnaireBase = QuestionnaireFactory.getQuestionnaire(tag);
        bind = FragmentQuestionnaireBinding.inflate(layoutInflater)

        bind.tvQustionnaireTitle.text = questionnaireBase!!.title
        bind.tvQuestionnaireContent.text = questionnaireBase!!.content
        if (questionnaireBase!!.isNeedProgress) {
            bind.progressBar.max = questionnaireBase!!.questionList!!.size
            bind.progressBar.progress = 0
        } else {
            bind.progressBar.visibility = View.GONE
        }
        val questionBaseList = questionnaireBase!!.questionList
        for (i in questionBaseList!!.indices) {
            val questionBase = questionBaseList[i]
            val questionBaseView =
                QuestionViewFactory.getQuestion(requireContext(), i, questionBase)
            if (questionBaseView != null) {
                if (questionnaireBase!!.isNeedProgress) {
                    questionBaseView.setIClickListener { isAdd: Boolean ->
                        val nowProgress: Int = bind.progressBar.getProgress()
                        if (isAdd) {
                            bind.progressBar.setProgress(nowProgress + 1)
                        } else {
                            if (nowProgress > 0) {
                                bind.progressBar.setProgress(nowProgress - 1)
                            }
                        }
                    }
                }
                bind.questionContainer.addView(questionBaseView)
            }
        }

        bind.ivBack.setOnClickListener { tipBack() }
        bind.subBtn.setOnClickListener { v ->
            if (questionnaireBase!!.isFinish) {
                //保存问卷
                questionnaireBase!!.saveFile()

                val cur = Calendar.getInstance()
                val res = questionnaireBase!!.getResult() + " " + cur
                    .get(Calendar.YEAR).toString() + "-" + (cur
                    .get(Calendar.MONTH) + 1).toString() + "-" + cur
                    .get(Calendar.DAY_OF_MONTH).toString() + " " + cur.get(Calendar.HOUR_OF_DAY)
                    .toString() + ":" + cur.get(Calendar.MINUTE)
                    .toString() + ":" + cur.get(Calendar.SECOND).toString()
                Log.i("QuestionnaireFragment", "${res}")
                viewModel.subQuestionnaire(viewModel.currentUser.value!!.uid.toString(), tag!!, res)
                Toast.makeText(requireContext(), "提交成功", Toast.LENGTH_SHORT).show()
                Navigation.findNavController(bind.root).navigateUp()
            } else {
                Toast.makeText(requireContext(), "尚未完成问卷哦", Toast.LENGTH_SHORT)
                    .show()
            }
        }
        return bind.root
    }

    fun saveSp() {
//        sharedPreferencesHelper.putString(tag + "_time", System.currentTimeMillis().toString())
//        if (isNeedScore(tag)) {
//            sharedPreferencesHelper.putString(tag + "_score", questionnaireBase.getResult())
//        }
    }

    fun tipBack() {
        val builder: AlertDialog.Builder = AlertDialog.Builder(requireContext())
            .setMessage("您的问卷还未提交，确定退出？")
            .setPositiveButton("确定退出") { dialog, which ->
                Navigation.findNavController(bind.root).navigateUp()
            }
            .setNegativeButton("继续回答", null)
        val alertDialog: AlertDialog = builder.create()
        alertDialog.show()
    }
}