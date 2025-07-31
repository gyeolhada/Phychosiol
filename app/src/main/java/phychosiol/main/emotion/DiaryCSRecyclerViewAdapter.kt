package com.example.phychosiolz.main.emotion

import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.text.isDigitsOnly
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.phychosiolz.R
import com.example.phychosiolz.data.room.model.Diary
import com.example.phychosiolz.data.room.model.User
import com.example.phychosiolz.databinding.FragmentDiaryCsItemBinding
import com.example.phychosiolz.databinding.FragmentItemExistedUserBinding
import com.example.phychosiolz.utils.GlideUtil
import com.google.gson.Gson


class DiaryCSRecyclerViewAdapter(
    private val onItemClicked: (Diary) -> Unit,
    private val context: Context
) : ListAdapter<Diary, DiaryCSRecyclerViewAdapter.ViewHolder>(UserDiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val viewHolder = ViewHolder(
            FragmentDiaryCsItemBinding.inflate(
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

    inner class ViewHolder(private var binding: FragmentDiaryCsItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SimpleDateFormat")
        fun bind(diary: Diary) {
            binding.text.text =
                if (diary.title!!.length > 5) diary.title!!.substring(0, 3) + "..." else diary.title
            GlideUtil.glideImage(
                context,
                diary.images.let {
                    val toList =
                        Gson().fromJson(it, Array<String>::class.java).toList()
                    //return random image
                    toList[(Math.random() * toList.size).toInt()]
                }, binding.image
            )
            GlideUtil.glideEmotionImage(context, diary.emotion, binding.ivFace)
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