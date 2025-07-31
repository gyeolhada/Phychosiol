package com.example.phychosiolz.login

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.phychosiolz.R
import com.example.phychosiolz.data.room.model.User
import com.example.phychosiolz.databinding.FragmentExistedUserBinding
import com.example.phychosiolz.view_model.LoginAndRegisterViewModel

class ExistedUserFragment : Fragment() {
    private lateinit var bind: FragmentExistedUserBinding
    private lateinit var adapter: ExistedUserRecyclerViewAdapter
    private val loginAndRegisterViewModel: LoginAndRegisterViewModel by viewModels { LoginAndRegisterViewModel.Factory }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        requireActivity().window.statusBarColor = Color.parseColor("#FFFFFF")
        bind = FragmentExistedUserBinding.inflate(layoutInflater)
        adapter = ExistedUserRecyclerViewAdapter ({
            Log.d("ExistedUserFragment", "login: $it")
            loginAndRegisterViewModel.login(it.let {
                User(it.uid, it.uname, it.usex, it.uavatar)
            }) { _ ->
                Navigation.findNavController(bind.root)
                    .navigate(R.id.action_existedUserFragment_to_navigation_main)
            }
        },requireContext())
        bind.list.adapter = adapter
        bind.list.layoutManager = LinearLayoutManager(context)
        return bind.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bind.ivBack.setOnClickListener {
            Navigation.findNavController(bind.root).navigateUp()
        }
        loginAndRegisterViewModel.usersFlow.observe(viewLifecycleOwner) { users ->
            adapter.submitList(users)
        }
    }
}