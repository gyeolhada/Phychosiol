package com.example.phychosiolz.main.mine.history


import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.phychosiolz.databinding.FragmentItemHistoryListBinding
import com.example.phychosiolz.utils.FileUtil

class HistoryRecyclerViewAdapter(private val userId: String) : RecyclerView.Adapter<HistoryRecyclerViewAdapter.ViewHolder>() {

    private val historyPerDayList : List<String> = FileUtil.getAllQuestionnairesByUserId(userId)//得到用户所有天数，每一天一个文件夹
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = FragmentItemHistoryListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = historyPerDayList[position]
        holder.bind(item)
    }

    override fun getItemCount(): Int {
        return historyPerDayList.size
    }

    inner class ViewHolder(private val binding: FragmentItemHistoryListBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: String) {
            val dateIndex = item.lastIndexOf('/') + 1
            val date = item.substring(dateIndex)
            val questionnaires = FileUtil.getFilePaths(item)
            //为每一个questionaaire建一个HistoryItemRecyclerView
            binding.tvNum.text = questionnaires.size.toString()
            binding.tvTime.text=date;

            val recyclerView = binding.rvHistory
            recyclerView.layoutManager = LinearLayoutManager(binding.root.context)
            recyclerView.adapter = HistoryItemRecyclerViewAdapter(questionnaires)
        }
    }
}


