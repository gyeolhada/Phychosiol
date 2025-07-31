package com.example.phychosiolz.main.mine

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.viewModels
import androidx.navigation.Navigation
import com.example.phychosiolz.MainActivity
import com.example.phychosiolz.R
import com.example.phychosiolz.databinding.FragmentMineEditBinding
import com.example.phychosiolz.utils.GlideUtil
import com.example.phychosiolz.view_model.MineEditViewModel
import com.google.android.material.datepicker.MaterialDatePicker
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import com.example.phychosiolz.model.UserLoginInfo

class MineEditFragment : Fragment() {
    private lateinit var bind: FragmentMineEditBinding
    private val viewModel: MineEditViewModel by viewModels { MineEditViewModel.Factory }
    private lateinit var pickMultipleMedia: ActivityResultLauncher<PickVisualMediaRequest>
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        requireActivity().window.statusBarColor =
            ResourcesCompat.getColor(resources, R.color.white, null)
        bind = FragmentMineEditBinding.inflate(layoutInflater)
        return bind.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        pickMultipleMedia =
            registerForActivityResult(ActivityResultContracts.PickMultipleVisualMedia()) {
                if (it.size > 1) {
                    Toast.makeText(requireContext(), "只能选择一张图片", Toast.LENGTH_SHORT).show()
                }
                //申请永久访问权限
                requireActivity().contentResolver.takePersistableUriPermission(
                    it[0], Intent.FLAG_GRANT_READ_URI_PERMISSION
                )
                viewModel.changeAvatar(it[0].toString())
            }
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //隐藏软键盘
        bind.bgLayout.setOnClickListener {
            it.requestFocus()
            val imm =
                it.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(it.windowToken, 0)
            bind.edtUsername.clearFocus()
        }
        bind.ivLogout.setOnClickListener {
            viewModel.logout()
            (requireActivity() as MainActivity).userLogout()//通知MainActivity,将服务停止
            Navigation.findNavController(bind.root)
                .navigate(R.id.action_mineEditFragment_to_LoginFragment)
        }

        bind.btnDelete.setOnClickListener {
            viewModel.deleteUser {
                viewModel.logout()
                Navigation.findNavController(bind.root)
                    .navigate(R.id.action_mineEditFragment_to_LoginFragment)
            }
        }

        bind.ivExchangeTime.setOnClickListener {
            clearFocus()
            //禁用0.5秒
            bind.ivExchangeTime.isEnabled = false
            bind.ivExchangeTime.postDelayed({ bind.ivExchangeTime.isEnabled = true }, 500)
            val datePicker =
                MaterialDatePicker.Builder.datePicker()
                    .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                    .setTitleText("修改生日")
                    .build()
            datePicker.show(childFragmentManager, "datePicker")
            datePicker.addOnPositiveButtonClickListener {
                //translate long to date in format yyyy-MM-dd
                val date = Date(it)
                viewModel.updateUserBirthday(
                    SimpleDateFormat(
                        "yyyy-MM-dd",
                        Locale.getDefault()
                    ).format(date)
                )
            }
        }
        bind.edtUsername.onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                viewModel.changeUname(bind.edtUsername.text.toString())
            }
        }
        bind.edtUserRealName.onFocusChangeListener =
            View.OnFocusChangeListener { _, hasFocus ->
                if (!hasFocus) {
                    viewModel.changePname(bind.edtUserRealName.text.toString())
                }
            }
        bind.edtHeight.onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                viewModel.changeHeight(bind.edtHeight.text.toString())
            }
        }
        bind.edtWeight.onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                viewModel.changeWeight(bind.edtWeight.text.toString())
            }
        }

        bind.ivExchange.setOnClickListener {
            clearFocus()
            viewModel.exchangeSex()
        }

        bind.tvSave.setOnClickListener {
            clearFocus()
            viewModel.updateUserInfo {
                Navigation.findNavController(bind.root)
                    .navigateUp()
            }
        }
        bind.ivAvatar.setOnClickListener {
            clearFocus()
            //禁用0.5秒
            bind.ivAvatar.isEnabled = false
            bind.ivAvatar.postDelayed({ bind.ivAvatar.isEnabled = true }, 500)
            //调用系统相册
            pickMultipleMedia.launch(PickVisualMediaRequest())
        }
        viewModel.currentUser.observe(viewLifecycleOwner) {
            if (it == null) {//删除用户后，currentUser会变为null
                return@observe
            }
            bind.edtUsername.text = Editable.Factory.getInstance().newEditable(it.uname)
            bind.edtUserRealName.text = Editable.Factory.getInstance().newEditable(it.pname ?: "")
            bind.edtHeight.text =
                Editable.Factory.getInstance().newEditable((it.pheight ?: "").toString())
            bind.edtWeight.text =
                Editable.Factory.getInstance().newEditable((it.pweight ?: "").toString())
            bind.tvSex.text = it.usex
            bind.tvBirth.text = it.pbirthday
            GlideUtil.glideAvatar(
                requireContext(),
                it.usex!!,
                it.uavatar!!,
                bind.ivAvatar
            )
        }
    }

    private fun clearFocus() {
        bind.edtUsername.clearFocus()
        bind.edtUserRealName.clearFocus()
        bind.edtHeight.clearFocus()
        bind.edtWeight.clearFocus()
    }
}