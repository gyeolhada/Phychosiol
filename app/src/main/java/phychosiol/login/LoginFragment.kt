package com.example.phychosiolz.login

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.Navigation
import com.example.phychosiolz.R
import com.example.phychosiolz.databinding.FragmentLoginBinding
import com.example.phychosiolz.view_model.LoginAndRegisterViewModel

class LoginFragment : Fragment() {
    private lateinit var bind: FragmentLoginBinding
    private val loginAndRegisterViewModel: LoginAndRegisterViewModel by viewModels { LoginAndRegisterViewModel.Factory }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        loginAndRegisterViewModel.checkLogin { _ ->//如果已经登录，直接跳转到主页
            Navigation.findNavController(bind.root)
                .navigate(R.id.action_loginFragment_to_navigation_main)
        }
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        requireActivity().window.statusBarColor =
            ResourcesCompat.getColor(resources, R.color.background_green, null)
        bind = FragmentLoginBinding.inflate(layoutInflater)
//        loginAndRegisterViewModel=LoginAndRegisterViewModel.Factory.create(LoginAndRegisterViewModel::class.java,)
        return bind.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //隐藏软键盘
        bind.root.setOnClickListener {
            it.requestFocus()
            val imm =
                it.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(it.windowToken, 0)
            bind.edtUsername.clearFocus()
        }
        //切换性别
        bind.switchSex.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                loginAndRegisterViewModel.newUserSex.postValue("女")
            } else {
                loginAndRegisterViewModel.newUserSex.postValue("男")
            }
        }
        bind.btnRegister.setOnClickListener {
            bind.edtUsername.clearFocus()
            loginAndRegisterViewModel.newUsername.value = bind.edtUsername.text.toString()
            loginAndRegisterViewModel.doRegister()
        }
        bind.btnLogin.setOnClickListener {
            Navigation.findNavController(bind.root)
                .navigate(R.id.action_loginFragment_to_existedUserFragment)
        }
        bind.btnLoginAsManager.setOnClickListener {
            Navigation.findNavController(bind.root)
                .navigate(R.id.action_loginFragment_to_managerLoginFragment)
        }
        loginAndRegisterViewModel.newUserSex.observe(viewLifecycleOwner, Observer { sex ->
            bind.tvSex.text = sex
            if (sex == "男") {
                bind.ivSex.setImageDrawable(
                    ContextCompat.getDrawable(
                        requireContext(),
                        R.drawable.man
                    )
                )
            } else {
                bind.ivSex.setImageDrawable(
                    ContextCompat.getDrawable(
                        requireContext(),
                        R.drawable.woman
                    )
                )
            }
        })
        loginAndRegisterViewModel.registerResult.observe(viewLifecycleOwner) { res ->
            when (res) {
                LoginAndRegisterViewModel.LoginResult.FAILURE -> {
                    Toast.makeText(requireContext(), "软件故障", Toast.LENGTH_SHORT).show()
                    bind.tvWarningInform.text="软件故障!"
                    bind.tvWarningInform.visibility=android.view.View.VISIBLE
                    //....text=''
                }

                LoginAndRegisterViewModel.LoginResult.SUCCESS -> {
                    Toast.makeText(requireContext(), "注册成功", Toast.LENGTH_SHORT).show()
                    Navigation.findNavController(bind.root)
                        .navigate(R.id.action_loginFragment_to_navigation_main)
                }

                LoginAndRegisterViewModel.LoginResult.BLANK -> {
//                    Toast.makeText(requireContext(), "用户名不能为空", Toast.LENGTH_SHORT).show()
                    bind.tvWarningInform.text="用户名不能为空!"
                    bind.tvWarningInform.visibility=android.view.View.VISIBLE
                }

                LoginAndRegisterViewModel.LoginResult.TOO_SHORT -> {
//                    Toast.makeText(requireContext(), "用户名太短", Toast.LENGTH_SHORT).show()
                    bind.tvWarningInform.text="用户名太短!"
                    bind.tvWarningInform.visibility=android.view.View.VISIBLE
                }

                LoginAndRegisterViewModel.LoginResult.TOO_LONG -> {
//                    Toast.makeText(requireContext(), "用户名太长", Toast.LENGTH_SHORT).show()
                    bind.tvWarningInform.text="用户名太长!"
                    bind.tvWarningInform.visibility=android.view.View.VISIBLE
                }

                LoginAndRegisterViewModel.LoginResult.EXISTED -> {
//                    Toast.makeText(requireContext(), "用户名已存在", Toast.LENGTH_SHORT).show()
                    bind.tvWarningInform.text="用户名已存在!"
                    bind.tvWarningInform.visibility=android.view.View.VISIBLE
                }

                LoginAndRegisterViewModel.LoginResult.WAITING -> {
                }

                else -> Toast.makeText(requireContext(), "未知错误", Toast.LENGTH_SHORT).show()
            }
        }
    }
}

