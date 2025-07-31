package com.example.phychosiolz.main.observe

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.bumptech.glide.Glide
import com.example.phychosiolz.MainActivity
import com.example.phychosiolz.MyApplication
import com.example.phychosiolz.R
import com.example.phychosiolz.databinding.FragmentObserveBinding
import com.example.phychosiolz.main.warning.DepressionAttackFineTuneDialog
import com.example.phychosiolz.service.UserService
import com.example.phychosiolz.view_model.DeviceStatus
import okhttp3.internal.format
import java.text.SimpleDateFormat
import java.util.Date

class ObserveFragment : Fragment() {

    private lateinit var bind: FragmentObserveBinding
    private var service: UserService? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        requireActivity().window.statusBarColor =
            ResourcesCompat.getColor(resources, R.color.background_green, null)
        bind = FragmentObserveBinding.inflate(layoutInflater)
        return bind.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (requireActivity() as MainActivity).getUserService()
            .observe(viewLifecycleOwner) { service ->
                this.service = service
                service?.isDepressionAttackRecognitionsOn?.observe(viewLifecycleOwner) {
                    if (bind.materialSwitch.isChecked != it) {
                        bind.materialSwitch.isChecked = it
                    }
                    if (it) {
                        Toast.makeText(context, "发作检测已开启", Toast.LENGTH_SHORT).show()
                    }
                }
                service?.connectedDevice?.observe(viewLifecycleOwner) {
                    if (it == null) {
                        bind.tvDeviceName.text = "未连接设备"
//                        bind.tvDeviceElectric.text = "--%"
                        bind.tvDeviceStatus.text = "未连接"
                        Glide.with(this).load(R.drawable.unconnected).into(bind.myWatch)
                        return@observe
                    }
                    bind.tvDeviceName.text = it.mDeviceName
                    val res1 = it.mElc?.times(100.00)
                    val s1 = res1.toString()
//                    bind.tvDeviceElectric.text = "$s1%"

                    val now = Date()
                    val s2 = SimpleDateFormat("HH:mm")
                    val ss: String = s2.format(now)
                    val res2 = "(截至$ss)"
                    bind.ElectricUpdateTime.text = res2

                    if (it.mDeviceState == DeviceStatus.CONNECTED) {
                        bind.tvDeviceStatus.text = "已连接"
                        Glide.with(this).load(R.drawable.connected).into(bind.myWatch)
                    } else {
                        bind.tvDeviceStatus.text = "未连接"
                        Glide.with(this).load(R.drawable.unconnected).into(bind.myWatch)
                    }
                }
                service?.bloodOxygenValidValue?.observe(viewLifecycleOwner) {
                    bind.tvSpO2Num.text = it.let { s ->
                        if (s == 0.toFloat()) "未检测" else format("%.1f", s)
                    }.toString()
                }
                service?.heartRateValidValue?.observe(viewLifecycleOwner) {
                    bind.tvHeartRateValue.text = it.let { s ->
                        if (s == 0.toFloat()) "未检测" else format("%.1f", s)
                    }.toString()
                }
                service?.temperatureValidValue?.observe(viewLifecycleOwner) {
                    bind.tvTempValue.text = it.let { s ->
                        if (s == 0.toFloat()) "未检测" else format("%.1f", s)
                    }.toString()
                }
                service?.dataTimeValue?.observe(viewLifecycleOwner) { time ->
                    if (time.isNullOrBlank()) return@observe
                    bind.tvTempDate.text = time
                    bind.tvHeartbeatDate.text = time
                    bind.tvSpO2Date.text = time
                }
                service?.elc?.observe(viewLifecycleOwner) {
//                    bind.tvDeviceElectric.text = "$it%"

//                    bind.tvDeviceElectric.text = "69%"
                }
            }

        bind.btnConnectOtherDevice.setOnClickListener {
            Navigation.findNavController(bind.root)
                .navigate(R.id.action_observeFragment_to_connectFragment)
        }

        bind.cvHeartbeat.setOnClickListener {
            Navigation.findNavController(bind.root)
                .navigate(R.id.action_observeFragment_to_heartChartFragment)
        }

        bind.cvSpO2.setOnClickListener {
            Navigation.findNavController(bind.root)
                .navigate(R.id.action_observeFragment_to_spo2ChartFragment)
        }

        bind.cvTemp.setOnClickListener {
            Navigation.findNavController(bind.root)
                .navigate(R.id.action_observeFragment_to_temperatureChartFragment)
        }
        bind.cvOverview.setOnClickListener {
            Navigation.findNavController(bind.root)
                .navigate(R.id.action_observeFragment_to_overviewChartFragment)
        }

        bind.btnHelp.setOnClickListener {
            Navigation.findNavController(bind.root)
                .navigate(R.id.action_observeFragment_to_warningFragment)
        }

        bind.btnSelfCheck.setOnClickListener {
            Toast.makeText(context, "自检功能暂未开放", Toast.LENGTH_SHORT).show()
        }
        bind.ivShowInfo.setOnClickListener {
            Toast.makeText(
                context,
                "发作检测需要进行一次主动的发作标记才可使用。",
                Toast.LENGTH_SHORT
            ).show()
        }
        bind.materialSwitch.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                service?.turnOnDepressionAttackRecognitions(::onFineTuneNeeded)
            } else {
                service?.isDepressionAttackRecognitionsOn?.value = false
            }
        }
        bind.btShowHistory.setOnClickListener {
            Navigation.findNavController(bind.root)
                .navigate(R.id.action_observeFragment_to_showFeelingFragment)
        }
    }

    //展示dialog给用户，收集数据进行微调
    private fun onFineTuneNeeded() {
        val dialog = DepressionAttackFineTuneDialog(service!!)
        dialog.show(childFragmentManager, "DepressionAttackFineTuneDialog")
    }
}