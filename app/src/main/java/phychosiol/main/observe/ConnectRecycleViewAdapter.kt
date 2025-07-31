package com.example.phychosiolz.main.observe

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.phychosiolz.databinding.FragmentConnectBinding
import com.example.phychosiolz.databinding.FragmentItemConnectBinding
import com.example.phychosiolz.databinding.FragmentItemItemDiaryBinding
import com.example.phychosiolz.model.BLEDeviceInfo
import com.example.phychosiolz.utils.GlideUtil


class ConnectRecycleViewAdapter(
    private val onItemClicked: (BLEDeviceInfo) -> Unit,
    private val context: Context
) : ListAdapter<BLEDeviceInfo, ConnectRecycleViewAdapter.ViewHolder>(DeviceDiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val viewHolder = ViewHolder(
            FragmentItemConnectBinding.inflate(
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

    inner class ViewHolder(private var binding: FragmentItemConnectBinding) :
        RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SimpleDateFormat")
        fun bind(device: BLEDeviceInfo) {
            //tp
//            GlideUtil.glideImage(context,uri,binding.image)
            val deviceName = device.mDeviceName
            binding.tvDeviceName.text=deviceName

        }
    }

    companion object {
        private val DeviceDiffCallback = object : DiffUtil.ItemCallback<BLEDeviceInfo>() {
            override fun areItemsTheSame(oldItem: BLEDeviceInfo, newItem: BLEDeviceInfo): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: BLEDeviceInfo, newItem: BLEDeviceInfo): Boolean {
                return oldItem == newItem
            }
        }
    }

}