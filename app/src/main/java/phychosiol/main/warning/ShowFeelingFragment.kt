package com.example.phychosiolz.main.warning

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.phychosiolz.R
import com.example.phychosiolz.databinding.FragmentShowFeelingBinding
import com.example.phychosiolz.main.mine.history.FeelingRecyclerViewAdapter
import com.example.phychosiolz.view_model.WarningViewModel

class ShowFeelingFragment : Fragment() {
    private lateinit var bind: FragmentShowFeelingBinding
    private val viewModel: WarningViewModel by viewModels {
       WarningViewModel.Factory
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        requireActivity().window.statusBarColor =
            ResourcesCompat.getColor(resources, R.color.background_green, null)
        bind = FragmentShowFeelingBinding.inflate(layoutInflater)
        return bind.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bind.rvFeeling.adapter = FeelingRecyclerViewAdapter(viewModel.getAllAttacks())
        bind.ivBack.setOnClickListener {
            findNavController().navigateUp()
        }
    }
}