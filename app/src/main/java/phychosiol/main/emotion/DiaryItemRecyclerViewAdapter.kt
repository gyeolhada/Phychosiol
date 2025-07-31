package com.example.phychosiolz.main.emotion

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.phychosiolz.databinding.FragmentItemItemDiaryBinding
import com.example.phychosiolz.utils.GlideUtil
import com.example.phychosiolz.utils.ViewUtil


class DiaryItemRecyclerViewAdapter(
    private val onItemClicked: (String) -> Unit,
    private val context: Context,
    private val itemSize: Int
) : ListAdapter<String, DiaryItemRecyclerViewAdapter.ViewHolder>(UserDiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val viewHolder = ViewHolder(
            FragmentItemItemDiaryBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

        viewHolder.itemView.setOnClickListener {
            val position = viewHolder.adapterPosition
            onItemClicked(getItem(position))
        }
        return viewHolder
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ViewHolder(private var binding: FragmentItemItemDiaryBinding) :
        RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SimpleDateFormat")
        fun bind(uri: String) {
            val layoutParams = binding.root.layoutParams
            layoutParams.width = itemSize
            layoutParams.height = itemSize
            binding.root.layoutParams = layoutParams
            // 计算单位是px
            val dpToPx = ViewUtil.dpToPx(context, 4f)
            binding.image.layoutParams = layoutParams.let {
                it.width = itemSize-dpToPx
                it.height = itemSize-dpToPx
                it
            }
            GlideUtil.glideImage(context,uri,binding.image)
        }
    }

    companion object {
        private val UserDiffCallback = object : DiffUtil.ItemCallback<String>() {
            override fun areItemsTheSame(oldItem: String, newItem: String): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: String, newItem: String): Boolean {
                return oldItem == newItem
            }
        }
    }

}