package com.example.phychosiolz.main.emotion

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.Navigation
import com.example.phychosiolz.R
import com.example.phychosiolz.data.enums.EmotionType
import com.example.phychosiolz.databinding.FragmentEmotionBinding
import com.example.phychosiolz.view_model.EmotionViewModel
import com.github.mikephil.charting.components.Description
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.listener.OnChartValueSelectedListener
import com.google.android.material.carousel.CarouselLayoutManager
import com.google.android.material.datepicker.MaterialDatePicker
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class EmotionFragment : Fragment() {
    private lateinit var bind: FragmentEmotionBinding
    private val viewModel: EmotionViewModel by viewModels { EmotionViewModel.Factory }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        requireActivity().window.statusBarColor =
            ResourcesCompat.getColor(resources, R.color.background_green, null)
        bind = FragmentEmotionBinding.inflate(layoutInflater)
        bind.carouselRv.layoutManager = CarouselLayoutManager()
        bind.carouselRv.adapter = DiaryCSRecyclerViewAdapter({}, requireContext())

        bind.rvUserDairy.adapter = DiaryRecyclerViewAdapter({
            viewModel.submitDiary(it) {
                Navigation.findNavController(bind.root)
                    .navigate(R.id.action_emotionFragment_to_diaryEditFragment)
            }
        }, requireContext())

        bind.rvSelectedDayDairy.adapter = DiaryRecyclerViewAdapter({
            viewModel.submitDiary(it) {
                Navigation.findNavController(bind.root)
                    .navigate(R.id.action_emotionFragment_to_diaryEditFragment)
            }
        }, requireContext())
        return bind.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bind.btnEmotionEdit.setOnClickListener {
            Navigation.findNavController(bind.root)
                .navigate(R.id.action_emotionFragment_to_emotionEditFragment)
        }
        bind.btnList.setOnClickListener {
            viewModel.viewMode.value = EmotionViewModel.ViewMode.LIST
        }
        bind.btnGraph.setOnClickListener {
            viewModel.viewMode.value = EmotionViewModel.ViewMode.GRAPH
        }

        viewModel.localDiaryList.observe(viewLifecycleOwner) {
            (bind.rvUserDairy.adapter as DiaryRecyclerViewAdapter).submitList(
                it ?: listOf()
            )
        }

        viewModel.emotionPieChartData.observe(viewLifecycleOwner) { pieEntries ->
            bind.pieChart.clear()
            if (pieEntries.isNullOrEmpty()) {//没有，让饼图消失
                return@observe
            }
            val pieDataSet = PieDataSet(pieEntries, "Emotion Distribution")
            pieDataSet.colors =
                pieEntries.map { viewModel.getColorForEmotionType(it.data as EmotionType,requireActivity()) }
            val pieData = PieData(pieDataSet)
//            pieData.setValueTextSize(8f)
//            pieData.setValueTextColor(Color.WHITE)
            pieData.setDrawValues(false)
            pieData.isHighlightEnabled = true
            val description = Description()
            description.text = ""
            bind.pieChart.description = description
            bind.pieChart.data = pieData
            bind.pieChart.legend.isEnabled = false
            //逐一显示
            bind.pieChart.animateY(500)
            bind.pieChart.invalidate()
        }
        bind.pieChart.setOnChartValueSelectedListener(object : OnChartValueSelectedListener {
            override fun onValueSelected(e: Entry?, h: Highlight?) {
                // 在这里处理点击事件
                if (e != null) {
                    viewModel.selectedEmotionEntry.value = e
                }
            }

            override fun onNothingSelected() {
                // 在没有选择任何值时执行的操作
            }
        })
        viewModel.viewMode.observe(viewLifecycleOwner) {
            when (it) {
                EmotionViewModel.ViewMode.LIST -> {
                    bind.llDiaryList.visibility = View.VISIBLE
                    bind.llDiaryGraph.visibility = View.GONE
                    bind.pieChart.visibility = View.GONE
                    bind.btnList.backgroundTintList =
                        ResourcesCompat.getColorStateList(resources, R.color.primary_green, null)
                    bind.btnGraph.backgroundTintList =
                        ResourcesCompat.getColorStateList(resources, R.color.white, null)
                    bind.btnEmotionEdit.visibility = View.VISIBLE
                }

                EmotionViewModel.ViewMode.GRAPH -> {
                    bind.llDiaryList.visibility = View.GONE
                    bind.llDiaryGraph.visibility = View.VISIBLE
                    bind.pieChart.visibility = View.VISIBLE
                    bind.btnList.backgroundTintList =
                        ResourcesCompat.getColorStateList(resources, R.color.white, null)
                    bind.btnGraph.backgroundTintList =
                        ResourcesCompat.getColorStateList(resources, R.color.primary_green, null)
                    bind.btnEmotionEdit.visibility = View.GONE
                    bind.pieChart.invalidate()
                }
            }
        }
        viewModel.localDiaryWithImagesList.observe(viewLifecycleOwner) {
            Log.d("EmotionFragment", "onViewCreated: $it")
            (bind.carouselRv.adapter as DiaryCSRecyclerViewAdapter).submitList(it ?: listOf())
        }


        bind.ivSelectDate.setOnClickListener {
            val datePicker =
                MaterialDatePicker.Builder.datePicker()
                    .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                    .setTitleText("修改时间")
                    .build()
            datePicker.show(childFragmentManager, "datePicker")
            datePicker.addOnPositiveButtonClickListener {
                val selectedCalendar = Calendar.getInstance()
                selectedCalendar.timeInMillis = it
                viewModel.queryTime.value = selectedCalendar
            }
        }

        bind.ivLeft.setOnClickListener {
            viewModel.queryTime.value?.add(
                Calendar.DAY_OF_MONTH,
                -1
            )//这里只是对calendar这个对象内的值进行了修改，不会触发observe
            //需要触发observe的话，需要调用setValue
            viewModel.queryTime.value = viewModel.queryTime.value
        }

        bind.ivRight.setOnClickListener {
            viewModel.queryTime.value?.add(Calendar.DAY_OF_MONTH, 1)
            viewModel.queryTime.value = viewModel.queryTime.value
        }

//        //TODO 绘图时不用，这是使用时这些代码要放开
        viewModel.queryTime.observe(viewLifecycleOwner) {
            val dateFormat = SimpleDateFormat("yyyy年MM月dd日", Locale.getDefault())
            val formattedDate = dateFormat.format(it.time)
            bind.tvDate.text = formattedDate
            viewModel.selectedEmotionEntry.value = null
            //重新查询图表的数据
            viewModel.updateEmotionPieChartData(it)
        }

        viewModel.selectedEmotionEntry.observe(viewLifecycleOwner) {
            if (it == null) {
                bind.clEmo.visibility = View.GONE
            } else {
                bind.clEmo.visibility = View.VISIBLE
                val label = it.data as EmotionType // 这里假设你在 PieEntry 的构造函数中传递了 String 数据
                bind.tvPer.text = String.format("%.1f", it.y * 100)
                bind.tvEmoType.text = label.type
                when (label) {
                    EmotionType.HAPPY -> { bind.ivEmoType.setImageResource(R.drawable.happy) }
                    EmotionType.SAD -> { bind.ivEmoType.setImageResource(R.drawable.sad) }
                    EmotionType.SCARED -> { bind.ivEmoType.setImageResource(R.drawable.scared) }
                    EmotionType.SICK -> { bind.ivEmoType.setImageResource(R.drawable.sick) }
                    EmotionType.ANGRY -> { bind.ivEmoType.setImageResource(R.drawable.angry) }
                    EmotionType.NEUTRAL -> { bind.ivEmoType.setImageResource(R.drawable.neutral) }
                }
            }
        }

        viewModel.diaryInSelectedDay.observe(viewLifecycleOwner) {
            Log.d("EmotionFragment", "lsit: $it")
            (bind.rvSelectedDayDairy.adapter as DiaryRecyclerViewAdapter).submitList(it ?: listOf())
        }

        bind.tvOthersDiary.setOnClickListener {
            Toast.makeText(requireContext(), "其他人的日记，功能还在开发中", Toast.LENGTH_SHORT).show()
        }
    }
}