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
import com.example.phychosiolz.data.room.model.Attack
import com.example.phychosiolz.databinding.FragmentItemFeelingBinding
import com.example.phychosiolz.databinding.FragmentItemHistoryBinding
import com.example.phychosiolz.utils.FileUtil
import java.io.File

class FeelingRecyclerViewAdapter(private val itemList: List<Attack>) : RecyclerView.Adapter<FeelingRecyclerViewAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = FragmentItemFeelingBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = itemList[position]
        holder.bind(item)
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    inner class ViewHolder(private val binding: FragmentItemFeelingBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Attack) {
            binding.tvStartTime.text = item.startTime.toString()
            binding.tvEndTime.text = item.endTime.toString()
            binding.tvContent.text = item.feelings.toString()
        }
    }
}


