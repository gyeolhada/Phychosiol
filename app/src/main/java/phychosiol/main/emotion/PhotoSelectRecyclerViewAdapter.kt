package com.example.phychosiolz.main.emotion

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.phychosiolz.R
import com.example.phychosiolz.databinding.FragmentPhotoSelectItemBinding
import com.example.phychosiolz.utils.GlideUtil
import com.example.phychosiolz.utils.ViewUtil


class PhotoSelectRecyclerViewAdapter(
    private val itemSize: Int,
    private val onItemClicked: (Int) -> Unit,
    private val onDeleteClicked: (Int) -> Unit,
    private val onAddClicked: () -> Unit,
    private val context: Context,
    private val currentList: MutableList<String> = mutableListOf()
) : RecyclerView.Adapter<PhotoSelectRecyclerViewAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val viewHolder = ViewHolder(
            FragmentPhotoSelectItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
        viewHolder.itemView.setOnClickListener {
            val position = viewHolder.adapterPosition
            if (position == currentList.size) {
                onAddClicked()
                return@setOnClickListener
            }
        }
        return viewHolder
    }

    override fun getItemCount(): Int {
        if (currentList.size < 9) {
            Log.d("EmotionEditFragment", "getItemCount: ${currentList.size}+1")
            return currentList.size + 1
        }
        Log.d("EmotionEditFragment", "getItemCount: ${currentList.size}")
        return currentList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        Log.d("EmotionEditFragment", "onBindViewHolder: $position")
        if (position == currentList.size) {
            holder.bind(null)
            return
        }
        holder.bind(currentList[position])
    }

    @SuppressLint("NotifyDataSetChanged")
    fun submitList(list: List<String>?) {
        currentList.clear()
        if (list != null) {
            currentList.addAll(list)
        }
        notifyDataSetChanged()
    }

    inner class ViewHolder(private var binding: FragmentPhotoSelectItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SimpleDateFormat")
        fun bind(uri: String?) {
            GlideUtil.glideImage(context, uri ?: R.drawable.add_image.toString(), binding.image)
            if (uri == null){
                binding.image.visibility = INVISIBLE
                binding.addBtn.visibility = VISIBLE
                binding.deleteBtn.visibility = INVISIBLE
            }else{
                binding.addBtn.visibility = INVISIBLE
                binding.image.visibility = VISIBLE
                binding.deleteBtn.visibility = VISIBLE
            }
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
            binding.deleteBtn.setOnClickListener {
                onDeleteClicked(adapterPosition)
            }
        }
    }

}