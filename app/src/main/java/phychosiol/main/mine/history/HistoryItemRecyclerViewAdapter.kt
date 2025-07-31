package com.example.phychosiolz.main.mine.history


import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.phychosiolz.R
import com.example.phychosiolz.databinding.FragmentItemHistoryBinding
import com.example.phychosiolz.utils.FileUtil
import java.io.File

class HistoryItemRecyclerViewAdapter(private val itemList: List<String>) : RecyclerView.Adapter<HistoryItemRecyclerViewAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = FragmentItemHistoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = itemList[position]
        holder.bind(item)
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    inner class ViewHolder(private val binding: FragmentItemHistoryBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: String) {
            val content = FileUtil.readTextFile(item);
//            Log.d("1:",content)

            val scoreRegex = """the total score: (\d+)""".toRegex()
            val scoreMatch = scoreRegex.find(content)
            val score = scoreMatch?.groupValues?.getOrNull(1) ?: ""

            val nameRegex = """(\d+-\d+)(.+)\.txt""".toRegex()
            val fileName = File(item).name
            val nameMatch = nameRegex.find(fileName)
            val name = nameMatch?.groupValues?.getOrNull(2) ?: ""

            val timeRegex = """Current Time: (.+)""".toRegex()
            val timeMatch = timeRegex.find(content)
            val testTime = timeMatch?.groupValues?.getOrNull(1) ?: ""

            binding.questionName.text = name
            binding.questionScore.text = if (score == "") "" else "得分：$score"
            binding.questionTestTime.text = "测试时间：$testTime"

            binding.questionImage.setImageResource(when (name) {
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

//            binding.ivDetail.setOnClickListener {
            binding.root.setOnClickListener {//点击整个框跳转
                val bundle = bundleOf(
                    "name" to name,
                    "score" to score,
                    "testTime" to testTime,
                    "content" to content
                )
                Navigation.findNavController(binding.root)
                    .navigate(R.id.action_mineHistoryFragment_to_HistoryDetailFragment, bundle)
            }

        }
    }
}


