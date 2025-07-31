package com.example.phychosiolz.main.observe

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.phychosiolz.MainActivity
import com.example.phychosiolz.R
import com.example.phychosiolz.data.Graph
import com.example.phychosiolz.databinding.FragmentConnectBinding
import com.example.phychosiolz.service.UserService
import com.example.phychosiolz.view_model.DeviceStatus
import java.text.SimpleDateFormat
import java.util.Date

class ConnectFragment : Fragment() {

    private lateinit var bind: FragmentConnectBinding
    private lateinit var adapter: ConnectRecycleViewAdapter
    private var service: UserService? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        bind = FragmentConnectBinding.inflate(layoutInflater)
        return bind.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (requireActivity() as MainActivity).getUserService()
            .observe(viewLifecycleOwner) { nService ->
                service = nService
                nService!!.setDao(
                    Graph.db.testdataDao()
                )
                nService.scannedDevices.observe(viewLifecycleOwner) { list ->
                    //加载列表
                    Log.i("ble", list.toString())
                    adapter = ConnectRecycleViewAdapter(
                        onItemClicked = { device ->
                            service?.connectDevice(requireContext(), device.mAddress!!)
                        }, requireContext()
                    )
                    adapter.submitList(list)
                    bind.rvFoundDevices.adapter = adapter
                    bind.rvFoundDevices.layoutManager = LinearLayoutManager(context)
                }

                Glide.with(requireContext()).asGif().load(R.drawable.radar).into(bind.ivBluetooth)
                bind.switchBluetooth.setOnClickListener { service?.switchScanState() }
                service?.isScanning?.observe(viewLifecycleOwner) {
                    if (it) {
                        bind.switchBluetooth.text = "停止扫描"
                        bind.ivBluetooth.visibility = View.VISIBLE
                        bind.tvScanState.visibility = View.VISIBLE
                        service?.startScan()
                        bind.switchBluetooth.isChecked = true
                    } else {
                        bind.switchBluetooth.text = "开始扫描"
                        bind.ivBluetooth.visibility = View.INVISIBLE
                        bind.tvScanState.visibility = View.INVISIBLE
                        service?.stopScan()
                        bind.switchBluetooth.isChecked = false
                    }
                }

                service?.connectedDevice?.observe(viewLifecycleOwner) {
                    if(it==null){
                        bind.tvDeviceName.text = "未连接设备"
//                        bind.tvDeviceElectric.text = "--%"
                        bind.tvDeviceStatus.text = "未连接"
                        Glide.with(this).load(R.drawable.unconnected).into(bind.myWatch)
                        return@observe
                    }
                    bind.tvDeviceName.text = it.mDeviceName
//                    val res1 = it?.mElc?.times(100.00)
//                    val s1 = res1.toString()
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
            }
    }
}