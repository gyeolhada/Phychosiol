package com.example.phychosiolz.login

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
import com.example.phychosiolz.data.room.model.User
import com.example.phychosiolz.databinding.FragmentItemExistedUserBinding
import com.example.phychosiolz.model.UserExistInfo
import com.example.phychosiolz.utils.GlideUtil


class ExistedUserRecyclerViewAdapter(
    private val onItemClicked: (UserExistInfo) -> Unit,
    private val context: Context
) : ListAdapter<UserExistInfo, ExistedUserRecyclerViewAdapter.ViewHolder>(UserDiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val viewHolder = ViewHolder(
            FragmentItemExistedUserBinding.inflate(
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

    inner class ViewHolder(private var binding: FragmentItemExistedUserBinding) :
        RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SimpleDateFormat")
        fun bind(user: UserExistInfo) {
            binding.name.text = user.uname
            binding.lastLogin.text = user.lastLogin
            GlideUtil.glideAvatar(context,user.usex!!,user.uavatar!!,binding.avatar)
        }
    }
    companion object {
        private val UserDiffCallback = object : DiffUtil.ItemCallback<UserExistInfo>() {
            override fun areItemsTheSame(oldItem: UserExistInfo, newItem: UserExistInfo): Boolean {
                return oldItem.uid == newItem.uid
            }
            override fun areContentsTheSame(oldItem: UserExistInfo, newItem: UserExistInfo): Boolean {
                return oldItem == newItem
            }
        }
    }
}