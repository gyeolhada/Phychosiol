package com.example.phychosiolz.main.emotion

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.phychosiolz.R
import com.example.phychosiolz.databinding.FragmentDiaryDetailBinding
import com.example.phychosiolz.utils.GlideUtil
import com.example.phychosiolz.view_model.EmotionViewModel
import com.google.gson.Gson


class DiaryDetailFragment : Fragment() {
    private lateinit var bind: FragmentDiaryDetailBinding
    private lateinit var adapter: DiaryItemRecyclerViewAdapter
    private val emotionViewModel: EmotionViewModel by viewModels { EmotionViewModel.Factory }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        requireActivity().window.statusBarColor =
            ResourcesCompat.getColor(resources, R.color.white, null)
        bind = FragmentDiaryDetailBinding.inflate(layoutInflater)
        bind.tvTitle.text = emotionViewModel.currentDairy.value!!.title
        bind.tvContent.text = emotionViewModel.currentDairy.value!!.content
        bind.tvTime.text = emotionViewModel.currentDairy.value!!.time
        GlideUtil.glideEmotionImage(
            requireContext(),
            emotionViewModel.currentDairy.value!!.emotion,
            bind.ivEmotion
        )

        adapter = DiaryItemRecyclerViewAdapter({}, requireContext(),
            //屏幕宽度
            (resources.displayMetrics.widthPixels / 3 *1.2).toInt()
            )
        val str =
            Gson().fromJson(emotionViewModel.currentDairy.value!!.images, Array<String>::class.java)
                .toMutableList()
        adapter.submitList(str)
        bind.rvImages.adapter = adapter

        bind.ivBack.setOnClickListener {
            findNavController().navigateUp()
        }

        bind.btnDelete.setOnClickListener {
            emotionViewModel.deleteById { findNavController().navigateUp() }
        }

        GlideUtil.glideAvatar(
            requireContext(),
            "男",
            emotionViewModel.getUserAvatar(),
            bind.ivAvatar
        )

        return bind.root
    }
}
