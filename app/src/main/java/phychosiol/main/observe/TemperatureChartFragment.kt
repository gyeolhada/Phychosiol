package com.example.phychosiolz.main.observe

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import com.example.phychosiolz.R
import com.example.phychosiolz.databinding.FragmentHeartChartBinding
import com.example.phychosiolz.databinding.FragmentSpo2ChartBinding
import com.example.phychosiolz.databinding.FragmentTemperatureChartBinding
import com.example.phychosiolz.view_model.ChartViewModel
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.listener.OnChartValueSelectedListener
import com.google.android.material.datepicker.MaterialDatePicker
import java.lang.String.format
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import kotlin.math.max
import kotlin.math.min


class TemperatureChartFragment : Fragment() {
    private lateinit var bind: FragmentTemperatureChartBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        requireActivity().window.statusBarColor =
            ResourcesCompat.getColor(resources, R.color.white, null)
        bind = FragmentTemperatureChartBinding.inflate(layoutInflater)

        val chartViewModel = ViewModelProvider(
            requireActivity(), ChartViewModel.Factory
        )[ChartViewModel::class.java]

        initChart()

        bind.ivSelectDate.setOnClickListener {
            val picker = MaterialDatePicker.Builder.datePicker().build()
            picker.addOnPositiveButtonClickListener {
                val calendar = Calendar.getInstance()
                calendar.timeInMillis = it
                chartViewModel.queryTime.value = calendar
            }
            picker.show(requireActivity().supportFragmentManager, picker.toString())
        }

        bind.ivLeft.setOnClickListener {
            val calendar = chartViewModel.queryTime.value
            calendar!!.add(Calendar.DAY_OF_MONTH, -1)
            chartViewModel.queryTime.value = calendar
        }

        bind.ivRight.setOnClickListener {
            val calendar = chartViewModel.queryTime.value
            calendar!!.add(Calendar.DAY_OF_MONTH, 1)
            chartViewModel.queryTime.value = calendar
        }

        bind.ivBack.setOnClickListener {
            Navigation.findNavController(it).navigateUp()
        }
        bind.vCurrent.setOnClickListener {
            findNavController().navigate(R.id.action_temperatureChartFragment_to_overviewChartFragment)
        }
        chartViewModel.queryTime.observe(viewLifecycleOwner) {
            //TODO 设定时间
            bind.tvDate.text = SimpleDateFormat("yyyy年MM月dd日", Locale.CHINA).format(it.time)
            //1为血氧
            if (chartViewModel.currentuser.value!!.uname == "test") {
                // 设置预先设定好的数据
                bind.tvDate.text = SimpleDateFormat("yyyy年MM月dd日", Locale.CHINA).format(it.time)
                val testData = mutableListOf<Float>()
                for (i in 0..47) {
                    // 预先设定好的数据，这里假设为随机生成的数据
                    testData.add((36 + Math.random() * 2).toFloat())
                }
                val barEntries = mutableListOf<BarEntry>()
                var maxRate = 0f
                var minRate = 100f
                for (i in 0..47) {
                    barEntries.add(BarEntry(i.toFloat(), testData[i]))
                    if (testData[i] != 0f) {
                        minRate = min(minRate, testData[i])
                        maxRate = max(maxRate, testData[i])
                    }
                }
                bind.tvHighestBodyNum.text = if (maxRate == 0f) "--" else format("%.2f", maxRate)
                bind.tvLowestBodyNum.text = if (minRate == 100f) "--" else format("%.2f", minRate)
                val barDataSet = BarDataSet(barEntries, "平均体温") // "Label" 是柱状图的标签
                barDataSet.color = Color.parseColor("#8A9CDC") // 柱状图的颜色
                barDataSet.setDrawValues(false) // 不显示数值
                bind.barChart.data = BarData(barDataSet)
                bind.barChart.invalidate() // refresh
            } else {
                chartViewModel.getTestDataListInDay(1).observe(viewLifecycleOwner) {
                    // set data
                    bind.barChart.data = it?.let {
                        val barEntries = mutableListOf<BarEntry>()
                        var maxRate = 0f
                        var minRate = 100f
                        for (i in 0..47) {
                            barEntries.add(BarEntry(i.toFloat(), it[i]))
                            if (it[i] != 0f) {
                                minRate = min(minRate, it[i])
                                maxRate = max(maxRate, it[i])
                            }
                        }
                        bind.tvHighestBodyNum.text =
                            if (maxRate == 0f) "--" else format("%.2f", maxRate)
                        bind.tvLowestBodyNum.text =
                            if (minRate == 100f) "--" else format("%.2f", minRate)
                        val barDataSet = BarDataSet(barEntries, "平均体温") // "Label" 是柱状图的标签
                        barDataSet.color = Color.parseColor("#8A9CDC") // 柱状图的颜色
                        barDataSet.setDrawValues(false) // 不显示数值
                        BarData(barDataSet)
                    }
                    bind.barChart.invalidate() // refresh
                }
            }
        }
        return bind.root
    }

    private fun initChart() {
        //init MPChart's BarChart
        bind.barChart.apply {
            //禁止缩放
            setScaleEnabled(false)
            //禁止拖拽
            isDragEnabled = false
            // 不显示图例
            this.legend.isEnabled = false;
            // 不显示描述
            this.description.isEnabled = false;
            // 左右空出barWidth/2，更美观
            this.setFitBars(true);
            // 不绘制网格
            this.setDrawGridBackground(false);
            // 设置x轴显示在下方
            xAxis.position = XAxis.XAxisPosition.BOTTOM;
            // 设置x轴不画线
            xAxis.setDrawGridLines(false);
            // 设置自定义的ValueFormatter,显示整点
            xAxis.valueFormatter = object : ValueFormatter() {
                override fun getFormattedValue(value: Float): String {
                    return (value / 2).toInt().toString() + ":00"

                }
            }

            // 设置左y轴，设置y-label显示在图表外
//            axisRight.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
            // Y轴从0开始，不然会上移一点
            //y值最大为200
            axisRight.setDrawAxisLine(true)
//            axisRight.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
            // 设置虚线效果
            val dashLength = 10f
            val spaceLength = 3f
            axisRight.enableGridDashedLine(dashLength, spaceLength, 0f)
            xAxis.enableGridDashedLine(dashLength, spaceLength, 0f)
            // 隐藏左y轴数值标签
            axisLeft.setDrawLabels(false)
            axisLeft.setDrawGridLines(false)

            // 设置点击事件监听器
            setOnChartValueSelectedListener(object : OnChartValueSelectedListener {
                @SuppressLint("SetTextI18n")
                override fun onValueSelected(e: Entry?, h: Highlight?) {
                    // 在这里处理柱状图被点击时的逻辑，例如显示相关数值
                    if (e != null) {
                        // e.y 即为柱状图的数值
                        // 在这里可以进行你的显示逻辑
                        // 例如 tv.setText("数值为：" + e.y);
                        val h1=(e.x/2).toInt()
                        val m1=((e.x-h1*2)*30).toInt()
                        val m2=if (m1== 30) 0 else 30
                        val h2=if (m1== 30) h1+1 else h1

                        val startTime = String.format("%02d:%02d", h1, m1)
                        val endTime = String.format("%02d:%02d", h2, m2)

                        bind.tvTime.text = "$startTime-$endTime"
                        bind.tvTempNum.text = if (e.y == 0f) "--" else format("%.2f",e.y)
                    }
                }

                override fun onNothingSelected() {
                    // 不做任何处理
                    bind.tvTime.text = "00:00-00:00"
                    bind.tvTempNum.text = "--"
                }
            })
        }
    }
}