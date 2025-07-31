package com.example.phychosiolz.main.warning

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.MutableLiveData
import androidx.navigation.Navigation
import com.example.phychosiolz.MainActivity
import com.example.phychosiolz.R
import com.example.phychosiolz.databinding.FragmentWarningBinding
import com.example.phychosiolz.service.UserService
import com.example.phychosiolz.view_model.WarningViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.Random


class WarningFragment : Fragment() {

    private lateinit var bind: FragmentWarningBinding
    private val viewModel by viewModels<WarningViewModel> { WarningViewModel.Factory }
    private val service by lazy { (requireActivity() as MainActivity).getUserService() }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        requireActivity().window.statusBarColor =
            ResourcesCompat.getColor(resources, R.color.white, null)
        bind = FragmentWarningBinding.inflate(layoutInflater)
        return bind.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // 获取当前时间的字符串表示作为开始时间
        val formatter = SimpleDateFormat("yyyy.MM.dd\nHH:mm", Locale.getDefault())
        val formatter1 = SimpleDateFormat("yyyy.MM.dd  HH:mm:ss", Locale.getDefault())
        var startTime = formatter.format(Calendar.getInstance().time)
        var st1 = Calendar.getInstance().time
        var st2 = st1
        //显示dialog
        service.observe(viewLifecycleOwner) {
            if (it?.responseDoctor?.value == null) {
                val dialog = WarningDialogFragment()
                dialog.dialogCallback = object : WarningDialogFragment.DialogCallback {
                    override fun onEnsure() {
                        viewModel.startCounting()
                        //发送请求，通知服务器，病发开始
                        service.value?.askForHelp()
                    }

                    override fun onCancel() {
                        //返回上一个fragment
                        Navigation.findNavController(bind.root).navigateUp()
                    }
                }
                dialog.show(childFragmentManager, "warningDialog")
            }
            it?.responseDoctor?.observe(viewLifecycleOwner) { doc ->
                if (doc != null) {
                    bind.tvDoctorName.text = doc.name
                    bind.btnCall.setOnClickListener {
                        val intent = Intent(Intent.ACTION_DIAL)
                        intent.data = Uri.parse("tel:${doc.phone}")
                        startActivity(intent)
                    }
                    //current time is response time
                    val formatter = SimpleDateFormat("yyyy.MM.dd HH:mm", Locale.getDefault())
                    bind.tvRespondTime.text = formatter.format(Calendar.getInstance().time)
                }
            }
        }

        bind.tvIllTime.text = startTime

        bind.tvIllEnd.setOnClickListener {
            Navigation.findNavController(bind.root).navigateUp()
        }
        bind.ivRefresh.setOnClickListener {
            viewModel.refresh()
        }

        viewModel.url.observe(viewLifecycleOwner) {
            bind.webView.loadUrl(it)
        }

        viewModel.lastTime.observe(viewLifecycleOwner) {
            val st2Calendar = Calendar.getInstance()
            st2Calendar.time = st1
            st2Calendar.add(Calendar.SECOND, it?.toInt()!!)
            st2 = st2Calendar.time

            val hour = it / 3600
            val minute = (it - hour * 3600) / 60
            val second = (it - hour * 3600 - minute * 60)
            var res = ""
            if (hour < 10) res = res + "0" + hour.toString() + ":"
            else res = "$res$hour:"
            if (minute < 10) res = res + "0" + minute.toString() + ":"
            else res = "$res$minute:"
            if (second < 10) res = res + "0" + second.toString()
            else res += second.toString()
            bind.tvLastTime.text = res
        }

        // 记录发作病情
        bind.ivTakeNote.setOnClickListener {
            FeelingEditDialog(requireContext(), "") { content ->
                viewModel.saveContent(formatter1.format(st1),formatter1.format(st2),content)
            }.show()
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        service.value?.attackDone()//通知服务器，病发结束
    }

}
