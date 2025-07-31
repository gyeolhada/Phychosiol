package com.example.phychosiolz.main.mine.history

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.phychosiolz.R
import com.example.phychosiolz.databinding.FragmentMineHistoryBinding
import com.example.phychosiolz.utils.FileUtil
import com.example.phychosiolz.view_model.LoginAndRegisterViewModel

class MineHistoryFragment : Fragment() {
    private lateinit var bind: FragmentMineHistoryBinding
    private lateinit var adapter: HistoryRecyclerViewAdapter
    private val viewModel: LoginAndRegisterViewModel by viewModels {
        LoginAndRegisterViewModel.Factory
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        requireActivity().window.statusBarColor =
            ResourcesCompat.getColor(resources, R.color.background_green, null)
        bind = FragmentMineHistoryBinding.inflate(layoutInflater)
        return bind.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bind.ivBack.setOnClickListener {
            findNavController().navigateUp()
        }
        val currentUserId = viewModel.getCurrentUserId()
//        val days = FileUtil.getAllQuestionnairesByUserId(currentUserId)
//        val itemList = mutableListOf<Pair<String, String>>() // Pair<问卷路径, 问卷日期>
//        for (questionnairePath in questionnaires) {
//            val dateIndex = questionnairePath.lastIndexOf('/') + 1
//            val date = questionnairePath.substring(dateIndex)
//            itemList.add(Pair(currentUserId, date))
//        }
        // 打印questionnaires列表的所有内容
//        Log.d("MineHistoryFragment", "Questionnaires List:")
//        for (questionnairePath in questionnaires) {
//            Log.d("MineHistoryFragment", "Questionnaire Path: $questionnairePath")
//        }
        adapter = HistoryRecyclerViewAdapter(currentUserId)
        bind.rvUserHistory.layoutManager = LinearLayoutManager(requireContext())
        bind.rvUserHistory.adapter = adapter
    }
}