package com.example.phychosiolz.main.warning

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.example.phychosiolz.R
import com.example.phychosiolz.service.UserService


class DepressionAttackFineTuneDialog(private val service: UserService) : DialogFragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val inflate = inflater.inflate(R.layout.dialog_dep_attack_fine_tune, container, false)
        return inflate
    }

    private lateinit var tvPeace: TextView
    private lateinit var tvAttack: TextView
    private lateinit var btnPeace: Button
    private lateinit var btnAttack: Button
    private lateinit var btModelAdjust: Button
    private fun setUp() {
        tvPeace = requireView().findViewById(R.id.tv_peace)
        tvAttack = requireView().findViewById(R.id.tv_attack)
        btnPeace = requireView().findViewById(R.id.btn_collect_peace)
        btnAttack = requireView().findViewById(R.id.btn_collect_attack)
        btModelAdjust = requireView().findViewById(R.id.bt_model_adjust)
        refresh()

        btnPeace.setOnClickListener {
            val isStart = service.startToCollectFineTuneData(false)
            if (isStart) Toast.makeText(context, "平静数据正在采集！", Toast.LENGTH_SHORT).show()
            else Toast.makeText(context, "未连接设备或正在采集中", Toast.LENGTH_SHORT).show()
            dismiss()
        }
        btnAttack.setOnClickListener {
            val isStart = service.startToCollectFineTuneData(true)
            if (isStart) Toast.makeText(context, "平静数据正在采集！", Toast.LENGTH_SHORT).show()
            else Toast.makeText(context, "未连接设备或正在采集中", Toast.LENGTH_SHORT).show()
            dismiss()
        }
        btModelAdjust.setOnClickListener {
            if (!service.checkDepressionAttackFineTunePeaceDataExist())
                Toast.makeText(context, "请先采集平静数据", Toast.LENGTH_SHORT).show()
            else if (!service.checkDepressionAttackFineTuneAttackDataExist())
                Toast.makeText(context, "请先采集发作数据", Toast.LENGTH_SHORT).show()
            else {
                if (service.doDepressionRecognitionMigration()) {
                    Toast.makeText(context, "微调完成后，检测自动开启，请勿重复微调", Toast.LENGTH_SHORT).show()
                    dismiss()
                } else Toast.makeText(context, "请重新采集数据", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun refresh() {
        tvPeace.text = service.checkDepressionAttackFineTunePeaceDataExist().let {
            if (it) tvPeace.setTextColor(resources.getColor(R.color.background_green))
            else tvPeace.setTextColor(resources.getColor(R.color.red))
            if (it) "已采集" else "未采集"
        }
        tvAttack.text = service.checkDepressionAttackFineTuneAttackDataExist().let {
            if (it) tvAttack.setTextColor(resources.getColor(R.color.background_green))
            else tvAttack.setTextColor(resources.getColor(R.color.red))
            if (it) "已采集" else "未采集"
        }
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUp()
        refresh()
    }
}