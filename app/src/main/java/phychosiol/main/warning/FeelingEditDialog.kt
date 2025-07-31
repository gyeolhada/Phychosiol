package com.example.phychosiolz.main.warning

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.WindowManager
import android.widget.Toast
import com.example.phychosiolz.R
import com.example.phychosiolz.databinding.DialogFeelingEditBinding
import com.example.phychosiolz.utils.ViewUtil

/***
 * 感受编辑对话框, 这种dialog功能更丰富
 *  调出用 FeelingEditDialog(context, content, callback).show()
 *  消失用 dismiss()
 */
class FeelingEditDialog(
    val preContext: Context,
    private var content: String, //内容
    private val callback: (String) -> Unit//回调
) :
    Dialog(preContext, R.style.myDialog) {
    private lateinit var bind: DialogFeelingEditBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bind = DialogFeelingEditBinding.inflate(layoutInflater)
        setContentView(bind.root)
        initView()
        bind.etDescription.setText(content)
        //TODO 有一个文本框编辑内容，点击提交按钮，则回调保存内容，关闭对话框，点击取消按钮则关闭对话框
        bind.tvCancel.setOnClickListener {
            dismiss()
        }
        bind.tvSubmit.setOnClickListener{
            content = bind.etDescription.text.toString()
            callback(content)
            dismiss()
            Toast.makeText(preContext, "提交成功", Toast.LENGTH_SHORT).show()
        }
    }

    private fun initView() {
        window?.attributes = window?.attributes?.apply {
            height = WindowManager.LayoutParams.WRAP_CONTENT
            width = ViewUtil.getScreenWidth(context) - ViewUtil.dpToPx(context, 40f)
        }

    }
}