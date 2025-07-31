package com.example.phychosiolz.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.Navigation
import com.example.phychosiolz.R
import com.example.phychosiolz.databinding.FragmentManagerLoginBinding
import com.example.phychosiolz.view_model.LoginAndRegisterViewModel
import com.example.phychosiolz.view_model.ManagerLoginViewModel

class ManagerLoginFragment : Fragment() {
    private lateinit var bind: FragmentManagerLoginBinding
    private val viewModel: ManagerLoginViewModel by viewModels { ManagerLoginViewModel.Factory }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requireActivity().window.statusBarColor =
            ResourcesCompat.getColor(resources, R.color.theme_orange, null)
        viewModel.checkLogin { _ ->//如果已经登录，直接跳转到主页
            Navigation.findNavController(bind.root)
                .navigate(R.id.action_managerLoginFragment_to_managerFragment)
        }
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        bind = FragmentManagerLoginBinding.inflate(layoutInflater)
        return bind.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bind.loginButton.setOnClickListener {
            if (bind.etName.text.isNullOrBlank() || bind.etNumber.text.isNullOrBlank()) {
                bind.etName.error = "用户名不能为空"
                bind.etNumber.error = "密码不能为空"
                return@setOnClickListener
            }
            viewModel.login(bind.etName.text.toString(),bind.etNumber.text.toString()) {
                Navigation.findNavController(bind.root)
                    .navigate(R.id.action_managerLoginFragment_to_managerFragment)
            }
        }
    }
}

