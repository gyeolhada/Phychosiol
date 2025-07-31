package com.example.phychosiolz.main.emotion

import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.text.isDigitsOnly
import androidx.navigation.Navigation
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.phychosiolz.R
import com.example.phychosiolz.data.room.model.Diary
import com.example.phychosiolz.data.room.model.User
import com.example.phychosiolz.databinding.FragmentDiaryCsItemBinding
import com.example.phychosiolz.databinding.FragmentItemDiaryListBinding
import com.example.phychosiolz.databinding.FragmentItemExistedUserBinding
import com.example.phychosiolz.utils.GlideUtil
import com.google.gson.Gson


class DiaryRecyclerViewAdapter(
    private val onItemClicked: (Diary) -> Unit,
    private val context: Context
) : ListAdapter<Diary, DiaryRecyclerViewAdapter.ViewHolder>(UserDiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val viewHolder = ViewHolder(
            FragmentItemDiaryListBinding.inflate(
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

    inner class ViewHolder(private var binding: FragmentItemDiaryListBinding) :
        RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SimpleDateFormat")
        fun bind(diary: Diary) {
            binding.tvTitle.text =diary.title
            binding.tvContent.text=diary.content
            binding.tvTime.text=diary.time
            val images=Gson().fromJson(diary.images,Array<String>::class.java).toList()
            val adapter=DiaryItemRecyclerViewAdapter({},context,context.resources.displayMetrics.widthPixels/4)
            binding.rvImages.adapter=adapter
            adapter.submitList(images)

        }
    }

    companion object {
        private val UserDiffCallback = object : DiffUtil.ItemCallback<Diary>() {
            override fun areItemsTheSame(oldItem: Diary, newItem: Diary): Boolean {
                return oldItem.uid == newItem.uid
            }

            override fun areContentsTheSame(oldItem: Diary, newItem: Diary): Boolean {
                return oldItem == newItem
            }
        }
    }

}