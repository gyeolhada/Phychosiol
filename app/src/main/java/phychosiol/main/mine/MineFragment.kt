package com.example.phychosiolz.main.mine

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.Navigation
import com.example.phychosiolz.MainActivity
import com.example.phychosiolz.R
import com.example.phychosiolz.databinding.FragmentMineBinding
import com.example.phychosiolz.utils.GlideUtil
import com.example.phychosiolz.view_model.LoginAndRegisterViewModel

class MineFragment : Fragment() {
    private lateinit var bind: FragmentMineBinding
    private val viewModel: LoginAndRegisterViewModel by viewModels { LoginAndRegisterViewModel.Factory }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        requireActivity().window.statusBarColor =
            ResourcesCompat.getColor(resources, R.color.background_green, null)
        bind = FragmentMineBinding.inflate(layoutInflater)
        return bind.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bind.ivHistory.setOnClickListener{
            Navigation.findNavController(bind.root)
                .navigate(R.id.action_mineFragment_to_mineHistoryFragment)
        }

        bind.ivEnterEdit.setOnClickListener {
            Navigation.findNavController(bind.root)
                .navigate(R.id.action_mineFragment_to_mineEditFragment)
        }
        bind.cvPhq.setOnClickListener {
            val bundle = Bundle()
            bundle.putString("tag", "PHQ-9")
            Log.i("MineFragment", "bundle")
            Navigation.findNavController(bind.root)
                .navigate(R.id.action_mineFragment_to_questionnaireFragment, bundle)
        }
        bind.cvBdi.setOnClickListener {
            val bundle = Bundle()
            bundle.putString("tag", "BDI-II")
            Navigation.findNavController(bind.root)
                .navigate(R.id.action_mineFragment_to_questionnaireFragment, bundle)
        }
        bind.cvGad.setOnClickListener {
            val bundle = Bundle()
            bundle.putString("tag", "GAD-7")
            Navigation.findNavController(bind.root)
                .navigate(R.id.action_mineFragment_to_questionnaireFragment, bundle)
        }
        bind.cvPanas.setOnClickListener {
            val bundle = Bundle()
            bundle.putString("tag", "PANAS")
            Navigation.findNavController(bind.root)
                .navigate(R.id.action_mineFragment_to_questionnaireFragment, bundle)
        }
        bind.cvSds.setOnClickListener {
            val bundle = Bundle()
            bundle.putString("tag", "SDS")
            Navigation.findNavController(bind.root)
                .navigate(R.id.action_mineFragment_to_questionnaireFragment, bundle)
        }
        bind.cvBig5.setOnClickListener {
            val bundle = Bundle()
            bundle.putString("tag", "BIG5")
            Navigation.findNavController(bind.root)
                .navigate(R.id.action_mineFragment_to_questionnaireFragment, bundle)
        }
        bind.cvPanasX.setOnClickListener {
            val bundle = Bundle()
            bundle.putString("tag", "PANAX")
            Navigation.findNavController(bind.root)
                .navigate(R.id.action_mineFragment_to_questionnaireFragment, bundle)
        }
        bind.cvEpq.setOnClickListener {
            val bundle = Bundle()
            bundle.putString("tag", "EPQ")
            Navigation.findNavController(bind.root)
                .navigate(R.id.action_mineFragment_to_questionnaireFragment, bundle)
        }
        bind.cvStais.setOnClickListener {
            val bundle = Bundle()
            bundle.putString("tag", "STAIS")
            Navigation.findNavController(bind.root)
                .navigate(R.id.action_mineFragment_to_questionnaireFragment, bundle)
        }

        bind.tvBdiScore.text = viewModel.bdiScore.value.toString()
        bind.tvPhqScore.text = viewModel.phqScore.value.toString()
        bind.tvGadScore.text = viewModel.gadScore.value.toString()
        bind.tvSdsScore.text = viewModel.sdsScore.value.toString()

        bind.tvBdiTime.text = viewModel.bdiTime.value.toString()
        bind.tvBigTime.text = viewModel.bigTime.value.toString()
        bind.tvEpqTime.text = viewModel.epqTime.value.toString()
        bind.tvGadTime.text = viewModel.gadTime.value.toString()
        bind.tvPanasTime.text = viewModel.panasTime.value.toString()
        bind.tvPanasXTime.text = viewModel.panasxTime.value.toString()
        bind.tvPhqTime.text = viewModel.panasxTime.value.toString()
        bind.tvSdsTime.text = viewModel.sdsTime.value.toString()
        bind.tvStaisTime.text = viewModel.staisTime.value.toString()



        Log.d("MineFragment", "onViewCreated: " + viewModel.currentUser.value)
        viewModel.currentUser.observe(viewLifecycleOwner, Observer { user ->
            GlideUtil.glideAvatar(
                requireContext(),
                user!!.usex!!,
                user!!.uavatar!!,
                bind.ivUserAvatar
            )

            user.pbirthday?.let {
                bind.tvUserAge.text = "${viewModel.getAge(it)}岁"
            }

            bind.tvUserName.text = user.uname
            bind.tvUserSex.text = user.usex
        })

        bind.ivLogout.setOnClickListener {
            viewModel.logout {
                (requireActivity() as MainActivity).userLogout()//通知MainActivity,将服务停止
                Navigation.findNavController(bind.root)
                    .navigate(R.id.action_mineFragment_to_LoginFragment)
            }
        }
    }
}