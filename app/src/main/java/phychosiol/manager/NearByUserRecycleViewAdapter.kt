package com.example.phychosiolz.manager

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.phychosiolz.R
import com.example.phychosiolz.databinding.FragmentNearNyItemBinding
import com.example.phychosiolz.model.UserTransInfo

class NearByUserRecycleViewAdapter (
    private val onItemClicked: (UserTransInfo) -> Unit,
    ) : ListAdapter<UserTransInfo, NearByUserRecycleViewAdapter.ViewHolder>(DeviceDiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NearByUserRecycleViewAdapter.ViewHolder {
        return ViewHolder(
            FragmentNearNyItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.bind(getItem(position))
            holder.itemView.setOnClickListener {
                onItemClicked(getItem(position))
            }
        }

        inner class ViewHolder(private var binding: FragmentNearNyItemBinding) :
            RecyclerView.ViewHolder(binding.root) {
            fun bind(user: UserTransInfo) {
                binding.tvUserName.text = user.userName
                binding.tvUserBirthdate.text = user.userBirth
                binding.tvUserSex.text = user.userSex
            }
        }

        companion object {
            private val DeviceDiffCallback = object : DiffUtil.ItemCallback<UserTransInfo>() {
                override fun areItemsTheSame(oldItem: UserTransInfo, newItem: UserTransInfo): Boolean {
                    return oldItem == newItem
                }

                override fun areContentsTheSame(oldItem: UserTransInfo, newItem: UserTransInfo): Boolean {
                    return oldItem == newItem
                }
            }
        }
    }
