package com.example.phychosiolz.main.observe

import android.annotation.SuppressLint
import android.graphics.Canvas
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.SeekBar
import androidx.annotation.RequiresApi
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.phychosiolz.R
import com.example.phychosiolz.databinding.FragmentHeartChartBinding
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
import com.github.mikephil.charting.renderer.XAxisRenderer
import com.github.mikephil.charting.utils.MPPointF
import com.github.mikephil.charting.utils.Transformer
import com.github.mikephil.charting.utils.ViewPortHandler
import com.google.android.material.datepicker.MaterialDatePicker
import okhttp3.internal.format
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import kotlin.math.max
import kotlin.math.min

/*** 修改总结：基于M V VM的思想，事件改变数据，数据改变视图 ***/
class HeartChartFragment : Fragment() {
    private lateinit var bind: FragmentHeartChartBinding
    private val chartViewModel:ChartViewModel by viewModels { ChartViewModel.Factory }
    @SuppressLint("SetTextI18n")
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        requireActivity().window.statusBarColor =
            ResourcesCompat.getColor(resources, R.color.white, null)
        bind = FragmentHeartChartBinding.inflate(layoutInflater)
        initChart()

        /*** 4、这里也不需要，在view model层，已经对数据进行了初始化 ***/
//        val s2 = SimpleDateFormat("yyyy年MM月dd日")
//        bind.tvHeartbeatDate.text= s2.format(Date())
        bind.ivSelectDate.setOnClickListener{
            val datePicker =
                MaterialDatePicker.Builder.datePicker()
                    .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                    .setTitleText("修改时间")
                    .build()
            datePicker.show(childFragmentManager,"datePicker")
            datePicker.addOnPositiveButtonClickListener {
                val selectedCalendar = Calendar.getInstance()
                selectedCalendar.timeInMillis = it
                chartViewModel.queryTime.value = selectedCalendar
                /** 2、这里不需要手动改变视图，只需要改变数据，因为下面做了数据的绑定,下同 **/
//                val dateFormat = SimpleDateFormat("yyyy年MM月dd日", Locale.getDefault())
//                val formattedDate = dateFormat.format(selectedCalendar.time)
//                bind.tvHeartbeatDate.text = formattedDate
            }
        }

        bind.ivLeft.setOnClickListener{
//            val currentDate = bind.tvHeartbeatDate.text.toString()
//            val currentDateCalendar = Calendar.getInstance()
//            currentDateCalendar.time = SimpleDateFormat("yyyy年MM月dd日", Locale.getDefault()).parse(currentDate) ?: Date()
//            currentDateCalendar.add(Calendar.DAY_OF_MONTH, -1)
//            val formattedDate = SimpleDateFormat("yyyy年MM月dd日", Locale.getDefault()).format(currentDateCalendar.time)
//            bind.tvHeartbeatDate.text = formattedDate
            /*** 3、修改数据即可 ***/
            chartViewModel.queryTime.value?.add(Calendar.DAY_OF_MONTH,-1)//这里只是对calendar这个对象内的值进行了修改，不会触发observe
            //需要触发observe的话，需要调用setValue
            chartViewModel.queryTime.value=chartViewModel.queryTime.value
        }

        bind.ivRight.setOnClickListener {
//            val currentDate = bind.tvHeartbeatDate.text.toString()
//            val currentDateCalendar = Calendar.getInstance()
//            currentDateCalendar.time = SimpleDateFormat("yyyy年MM月dd日", Locale.getDefault()).parse(currentDate) ?: Date()
//            currentDateCalendar.add(Calendar.DAY_OF_MONTH, 1)
//            val formattedDate = SimpleDateFormat("yyyy年MM月dd日", Locale.getDefault()).format(currentDateCalendar.time)
//            bind.tvHeartbeatDate.text = formattedDate
            chartViewModel.queryTime.value?.add(Calendar.DAY_OF_MONTH,1)
            chartViewModel.queryTime.value=chartViewModel.queryTime.value
        }


        bind.ivBack.setOnClickListener {
            findNavController().popBackStack()
        }
        bind.vCurrent.setOnClickListener {
            findNavController().navigate(R.id.action_heartChartFragment_to_overviewChartFragment)
        }


        chartViewModel.queryTime.observe(viewLifecycleOwner) {
            //TODO 设定时间
            bind.tvDate.text = SimpleDateFormat("yyyy年MM月dd日", Locale.CHINA).format(it.time)
            //1为血氧
            if (chartViewModel.currentuser.value!!.uname == "test") {
                // 设置预先设定好的数据
                val testData = mutableListOf<Float>()
                for (i in 0..47) {
                    // 预先设定好的数据，这里假设为随机生成的数据
                    testData.add((70 + Math.random() * 30).toFloat())
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
                bind.tvHighestRateNum.text = if (maxRate == 0f) "--" else format("%.0f", maxRate)
                bind.tvLowestRateNum.text = if (minRate == 100f) "--" else format("%.0f", minRate)
                val barDataSet = BarDataSet(barEntries, "平均心跳频率") // "Label" 是柱状图的标签
                barDataSet.color = Color.parseColor("#FF5252") // 柱状图的颜色
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
                        bind.tvHighestRateNum.text =
                            if (maxRate == 0f) "--" else format("%.0f", maxRate)
                        bind.tvLowestRateNum.text =
                            if (minRate == 100f) "--" else format("%.0f", minRate)
                        val barDataSet = BarDataSet(barEntries, "平均心跳频率") // "Label" 是柱状图的标签
                        barDataSet.color = Color.parseColor("#FF5252") // 柱状图的颜色
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
            //y最小为0
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
            axisRight.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
            // Y轴从0开始，不然会上移一点
            //y值最大为200
            axisRight.setDrawAxisLine(true)
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
                override fun onValueSelected(e: Entry?, h: Highlight?) {
                    // 在这里处理柱状图被点击时的逻辑，例如显示相关数值
                    if (e != null) {
                        // e.y 即为柱状图的数值
                        // 在这里可以进行你的显示逻辑
                        // 例如 tv.setText("数值为：" + e.y);
                        val heartRate = e.y.toInt()
                        val timePeriod = xAxis.valueFormatter.getFormattedValue(e.x)
                        val nextPeriod=xAxis.valueFormatter.getFormattedValue(e.x+1)
                        var s=timePeriod.toString()
                        s = if (timePeriod==nextPeriod) {
                            "$s-${s.substring(0, s.length - 2)}30"
                        } else {
                            "${s.substring(0, s.length - 2)}30-$nextPeriod"
                        }
                        bind.tvHeartbeatPeriod.text= s
                        bind.tvHeartbeatRate.text= if (heartRate==0) "--" else format("%.0f", e.y)
                    }
                }

                override fun onNothingSelected() {
                    // 不做任何处理
                    bind.tvHeartbeatPeriod.text= "00:00-00:00"
                    bind.tvHeartbeatRate.text= "--"
                }
            })
        }
    }
}


