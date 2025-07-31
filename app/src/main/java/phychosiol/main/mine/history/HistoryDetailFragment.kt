package com.example.phychosiolz.main.mine.history

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.phychosiolz.R
import com.example.phychosiolz.databinding.FragmentHistoryDetailBinding

class HistoryDetailFragment : Fragment() {
    private lateinit var bind: FragmentHistoryDetailBinding

    // 声明参数变量
    private var name: String? = null
    private var score: String? = null
    private var testTime: String? = null
    private var content: String? = null

    companion object {
        fun newInstance(name: String, score: String, testTime: String, content: String): HistoryDetailFragment {
            val fragment = HistoryDetailFragment()
            val args = Bundle()
            args.putString("name", name)
            args.putString("score", score)
            args.putString("testTime", testTime)
            args.putString("content", content)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        requireActivity().window.statusBarColor =
            ResourcesCompat.getColor(resources, R.color.background_green, null)
        bind = FragmentHistoryDetailBinding.inflate(layoutInflater)
        return bind.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 从参数中获取值
        arguments?.let {
            name = it.getString("name")
            score = it.getString("score")
            testTime = it.getString("testTime")
            content = it.getString("content")
        }
        // 将数据绑定到视图上
        bind.tvType.text = name

        bind.ivType.setImageResource(when (name) {
            "BDI-II" -> R.drawable.icon_mine_bdi
            "BIG5" -> R.drawable.icon_mine_big5
            "EPQ" -> R.drawable.icon_mine_epq
            "GAD-7" -> R.drawable.icon_mine_gad
            "PANAS-X" -> R.drawable.icon_mine_panas_x
            "PANAS" -> R.drawable.icon_mine_panas
            "PHQ-9" -> R.drawable.icon_mine_phq
            "SDS" -> R.drawable.icon_mine_sds
            "STAI-S" -> R.drawable.icon_mine_stais
            else ->  R.drawable.icon_mine_bdi
        })

        bind.tvRes.text = content

        bind.ivBack.setOnClickListener {
            findNavController().navigateUp()
        }
    }
}
