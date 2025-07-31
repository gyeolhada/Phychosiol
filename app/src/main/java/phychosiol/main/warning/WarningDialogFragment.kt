package com.example.phychosiolz.main.warning

import android.app.Dialog
import android.os.Bundle
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.fragment.app.DialogFragment
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import com.example.phychosiolz.MainActivity
import com.example.phychosiolz.R
import com.example.phychosiolz.databinding.FragmentWarningDialogBinding

class WarningDialogFragment : DialogFragment() {
    private lateinit var bind: FragmentWarningDialogBinding
    private var countDownTimer: CountDownTimer? = null
    private val initialTime = 6000L//毫秒，初始6秒
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        bind = FragmentWarningDialogBinding.inflate(inflater, container, false)
        bind.tvMistake.setOnClickListener {
            dialogCallback?.onCancel()
            dismiss()
        }
        bind.tvNeedHelp.setOnClickListener {
            dialogCallback?.onEnsure()
            dismiss()
        }
        startCountDownTimer()
        return bind.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        return dialog
    }

    private fun startCountDownTimer() {//1000毫秒=1秒
        countDownTimer = object : CountDownTimer(initialTime, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val secondsLeft = millisUntilFinished / 1000
                bind.tvSecondLeft.text = secondsLeft.toString()
            }

            override fun onFinish() {
                dialogCallback?.onEnsure()
                dismiss()
            }
        }
        countDownTimer?.start()
    }

    override fun onDestroy() {
        super.onDestroy()
        countDownTimer?.cancel()
    }

    var dialogCallback:DialogCallback?=null

    interface DialogCallback{
        fun onEnsure()
        fun onCancel()
    }
}