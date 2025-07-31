package com.example.phychosiolz.manager

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.VibrationEffect
import android.os.Vibrator
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.viewModels
import androidx.navigation.Navigation
import com.example.phychosiolz.R
import com.example.phychosiolz.databinding.FragmentManagerBinding
import com.example.phychosiolz.model.UserTransInfo
import com.example.phychosiolz.view_model.ManagerLoginViewModel
import com.example.phychosiolz.view_model.ManagerViewModel
import com.example.phychosiolz.view_model.MineEditViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Calendar

enum class ManaMsg(val code: Int) {
    RECEIVE_HELP(0),
    RECEIVE_SUCCESS(1),
    RECEIVE_FAIL(2)
}

class ManagerFragment : Fragment() {
    private lateinit var bind: FragmentManagerBinding
    private val viewModel: ManagerViewModel by viewModels { ManagerViewModel.Factory }
    private lateinit var handler: Handler
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        bind = FragmentManagerBinding.inflate(layoutInflater)
        return bind.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        bind.helpedUser.visibility = View.GONE

        handler = Handler(Handler.Callback {
            when (it.what) {
                ManaMsg.RECEIVE_HELP.code -> {
                    //make a phone shake
                    shakePhone()
                    val userInfo = it.obj as UserTransInfo
                    //显示一个对话框，展示信息并询问是否响应，不响应则什么都不做，响应了就使用以下代码。
                    MaterialAlertDialogBuilder(requireContext())
                        .setTitle("求助")
                        .setMessage("是否救助此病人？")
                        .setIcon(ResourcesCompat.getDrawable(resources, R.drawable.help, null))
                        .setNegativeButton(resources.getString(R.string.decline)) { dialog, which ->
                            //不响应什么都不做
                        }
                        .setPositiveButton(resources.getString(R.string.accept)) { dialog, which ->
                            //接受请求的代码如下
                            viewModel.doResponse(userInfo.userIp, {
                                handler.obtainMessage(ManaMsg.RECEIVE_SUCCESS.code,userInfo).sendToTarget()
                            }, { acceptor ->
                                handler.obtainMessage(ManaMsg.RECEIVE_FAIL.code, acceptor).sendToTarget()
                            })
                        }
                        .show()
                }

                ManaMsg.RECEIVE_SUCCESS.code -> {
                    bind.helpedUser.visibility = View.VISIBLE
                    // in YYYY-MM-DD HH:MM:SS format
                    bind.tvTime.text = SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Calendar.getInstance().time)

                    val userInfo = it.obj as UserTransInfo
                    bind.tvUserName.text = userInfo.userName
                    bind.tvUserBirth.text = userInfo.userBirth
                    bind.tvUserSex.text = userInfo.userSex
                    bind.tvUserIp.text = userInfo.userIp
                    if (userInfo.userSex == "女")bind.ivUserSex.setImageResource(R.drawable.woman)
                    else bind.ivUserSex.setImageResource(R.drawable.man)

                    Toast.makeText(requireContext(), "响应成功，请尽快前往", Toast.LENGTH_SHORT)
                        .show()
                }

                ManaMsg.RECEIVE_FAIL.code -> {
                    Toast.makeText(requireContext(), "该请求已经有人响应,身份是${it.obj as String}", Toast.LENGTH_SHORT)
                        .show()
                }
            }
            true
        })
        bind.cardScanUser.setOnClickListener {
            Navigation.findNavController(view)
                .navigate(R.id.action_managerFragment_to_nearByUserFragment)
        }
        bind.tvLogout.setOnClickListener {
            viewModel.logout()
            Navigation.findNavController(bind.root)
                .popBackStack(R.id.loginFragment,false)
        }

        bind.ivBack.setOnClickListener {
            Navigation.findNavController(bind.root)
                .popBackStack(R.id.loginFragment,false)
        }

        bind.ivClose.setOnClickListener {
            bind.helpedUser.visibility = View.GONE
        }
        viewModel.currentManagerName.observe(viewLifecycleOwner) {
            bind.tvName.text = it
            bind.tvTime.text = Calendar.getInstance().time.toString()
        }
        viewModel.currentManagerNumber.observe(viewLifecycleOwner) {
            bind.tvNumber.text = it
        }
        bind.switch1.setOnClickListener {
            viewModel.switchListeningState()
        }
        viewModel.isMangerListeningOn.observe(viewLifecycleOwner) {
            bind.switch1.isChecked = it
            viewModel.setListening(handler, it)
        }
    }

    private fun shakePhone() {
        val vibrator = requireActivity().getSystemService(Context.VIBRATOR_SERVICE) as Vibrator

        // Check if the device has a vibrator
        if (vibrator.hasVibrator()) {
            // Use VibrationEffect for API level 26 and above
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE))
            } else {
                // For devices with API level below 26
                @Suppress("DEPRECATION")
                vibrator.vibrate(200)
            }
        }
    }
}