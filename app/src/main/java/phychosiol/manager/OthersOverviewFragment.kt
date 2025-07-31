package com.example.phychosiolz.manager

import android.graphics.Color
import android.icu.text.DecimalFormat
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.bumptech.glide.Glide
import com.example.phychosiolz.R
import com.example.phychosiolz.databinding.FragmentOthersOverviewBinding
import com.example.phychosiolz.view_model.OthersOverviewViewModel
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter
import okhttp3.internal.format
import java.util.LinkedList

/***
 * 用于展示用户的生理数据，医生查看时使用
 */
class OthersOverviewFragment : Fragment() {
    private lateinit var bind: FragmentOthersOverviewBinding
    private val viewModel: OthersOverviewViewModel by viewModels { OthersOverviewViewModel.Factory }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        requireActivity().window.statusBarColor =
            ResourcesCompat.getColor(resources, R.color.background_green, null)
        bind = FragmentOthersOverviewBinding.inflate(layoutInflater)

        // Initialize chart
        initHeartBeatChart()
        initSpo2Chart()
        initTempChart()
        initSkinChart()

        viewModel.recGraphPack.observe(viewLifecycleOwner){
            Log.d("OthersOverviewFragment", "onCreateView: $it")
            viewModel.submitHeartData(it.pointHeartRate)
            viewModel.submitSpO2Data(it.pointSpo2)
            viewModel.submitTempData(it.avgTemp)
            viewModel.submitSkinData(it.pointSkin)
            bind.tvAveHeart.text= it.avgHeartRate.toInt().toString()
            bind.tvAveSpO2.text= format("%.1f",it.avgSpo2)
            bind.tvAveTemp.text= format("%.1f",it.maxTemp)
        }
        Glide.with(requireContext()).asGif().load(R.drawable.ani_heartbeat).into(bind.aniHeart)
        Glide.with(requireContext()).asGif().load(R.drawable.ani_ecg).into(bind.aniSkin)
        Glide.with(requireContext()).asGif().load(R.drawable.ani_thermometer).into(bind.aniTemp)
        Glide.with(requireContext()).asGif().load(R.drawable.cholesterol).into(bind.aniSpo2)

        return bind.root
    }


    private fun initHeartBeatChart() {
        var values = LinkedList<Entry>()
        var set1 = LineDataSet(values, "")

        viewModel.pointHeartData.observe(viewLifecycleOwner) {
            val lineData = LineData(set1) // 创建LineData对象并将set1添加到其中
            bind.heartbeatLineHart.data = lineData // 将LineData设置给图表

//            viewModel.pointHeartData.observe(viewLifecycleOwner) { data ->
            val entries = it.mapIndexed { index, fl -> Entry(index.toFloat(), fl) }
            set1.values = entries // 更新LineDataSet的数值
            lineData.notifyDataChanged() // 通知LineData数据已改变
            bind.heartbeatLineHart.notifyDataSetChanged() // 刷新图表
            bind.heartbeatLineHart.invalidate()
            Log.i("OverviewChartFragment", viewModel.pointHeartData.value.toString())
//            }
        }

// x轴

        val xAxis = bind.heartbeatLineHart.xAxis
        xAxis.position = XAxis.XAxisPosition.BOTTOM // x轴底部显示
        xAxis.isEnabled = false
        xAxis.setDrawGridLines(false)   // 隐藏x轴线条
        xAxis.textColor = Color.parseColor("#8FC7CC")   // x轴字体颜色
        xAxis.axisLineColor = Color.TRANSPARENT // 底部x轴透明色
        xAxis.labelCount = 100    // x轴显示100段
// 左边y轴
        val leftAxis = bind.heartbeatLineHart.axisLeft
        leftAxis.enableGridDashedLine(5f, 5f, 0f) // y轴虚线
        leftAxis.textColor = Color.parseColor("#8FC7CC")    // y轴字体颜色
        leftAxis.axisLineColor = Color.TRANSPARENT  // y轴边线颜色透明
        leftAxis.gridColor = Color.parseColor("#335566")    // y轴颜色
        leftAxis.labelCount = 6    // y轴显示6段
        leftAxis.axisMinimum = 1000f     // y轴最小值
        leftAxis.valueFormatter = object : ValueFormatter() {
            override fun getAxisLabel(value: Float, axis: AxisBase?): String {
                return DecimalFormat("0.00").format(value.toDouble())   // 数值保留2位小数
            }
        }
        bind.heartbeatLineHart.axisRight.isEnabled = false
        bind.heartbeatLineHart.axisLeft.isEnabled = false  // 隐藏右边y轴
        bind.heartbeatLineHart.setVisibleXRangeMaximum(5F);
        bind.heartbeatLineHart.data = LineData(set1)
        bind.heartbeatLineHart.legend.isEnabled = false // 隐藏左下角说明
        bind.heartbeatLineHart.setTouchEnabled(false)   // 设置图表不可点击
        bind.heartbeatLineHart.description.isEnabled = false   // 隐藏右下角描述

        // 在这里设置线
        set1.color = Color.parseColor("#8FC7CC")    // 线条颜色
        set1.lineWidth = 2f // 线条宽度
        set1.setCircleColor(Color.parseColor("#8FC7CC"))    // 圆点颜色
        set1.circleRadius = 0f // 圆点大小
        set1.setDrawCircleHole(false)   // 设置没有圆孔
        set1.setDrawFilled(true)    // 显示线条下部分颜色
        set1.valueTextColor = Color.parseColor("#8FC7CC")   // 数值颜色
        set1.mode = LineDataSet.Mode.CUBIC_BEZIER  // 显示曲线
        set1.setDrawValues(false)  // 不显示数值
        set1.setDrawCircles(false)  // 不显示圆点
        set1.valueFormatter = object : ValueFormatter() {
            override fun getFormattedValue(value: Float): String {
                return DecimalFormat("0.00").format(value.toDouble())   // 数值保留2位小数
            }
        }

    }

    private fun initSpo2Chart() {
        var values = LinkedList<Entry>()
        var set1 = LineDataSet(values, "")

        viewModel.pointSpO2Data.observe(viewLifecycleOwner) {
            val lineData = LineData(set1) // 创建LineData对象并将set1添加到其中
            bind.spO2LineChart.data = lineData // 将LineData设置给图表

//            viewModel.pointSpO2Data.observe(viewLifecycleOwner) { data ->
            val entries = it.mapIndexed { index, fl -> Entry(index.toFloat(), fl) }
            set1.values = entries // 更新LineDataSet的数值
            lineData.notifyDataChanged() // 通知LineData数据已改变
            bind.spO2LineChart.notifyDataSetChanged() // 刷新图表
            bind.spO2LineChart.invalidate()
            Log.i("OverviewChartFragment", viewModel.pointHeartData.value.toString())
//            }
        }
// x轴
        val xAxis = bind.spO2LineChart.xAxis
        xAxis.position = XAxis.XAxisPosition.BOTTOM // x轴底部显示
        xAxis.isEnabled = false
        xAxis.setDrawGridLines(false)   // 隐藏x轴线条
        xAxis.textColor = Color.parseColor("#8FC7CC")   // x轴字体颜色
        xAxis.axisLineColor = Color.TRANSPARENT // 底部x轴透明色
        xAxis.labelCount = 100    // x轴显示100段
// 左边y轴
        val leftAxis = bind.spO2LineChart.axisLeft
        leftAxis.enableGridDashedLine(5f, 5f, 0f) // y轴虚线
        leftAxis.textColor = Color.parseColor("#8FC7CC")    // y轴字体颜色
        leftAxis.axisLineColor = Color.TRANSPARENT  // y轴边线颜色透明
        leftAxis.gridColor = Color.parseColor("#335566")    // y轴颜色
        leftAxis.axisMinimum = 30f     // y轴最小值
        leftAxis.mAxisMaximum = 100f    // y轴最大值
        leftAxis.labelCount = 6    // y轴显示6段
        leftAxis.valueFormatter = object : ValueFormatter() {
            override fun getAxisLabel(value: Float, axis: AxisBase?): String {
                return DecimalFormat("0.00").format(value.toDouble())   // 数值保留2位小数
            }
        }
        bind.spO2LineChart.axisRight.isEnabled = false// 隐藏右边y轴
        bind.spO2LineChart.setVisibleXRangeMaximum(5F);
        bind.spO2LineChart.data = LineData(set1)
        bind.spO2LineChart.legend.isEnabled = false // 隐藏左下角说明
        bind.spO2LineChart.setTouchEnabled(false)   // 设置图表不可点击
        bind.spO2LineChart.description.isEnabled = false   // 隐藏右下角描述
        // 在这里设置线
        set1.color = Color.parseColor("#8FC7CC")    // 线条颜色
        set1.lineWidth = 2f // 线条宽度
        set1.setCircleColor(Color.parseColor("#8FC7CC"))    // 圆点颜色
        set1.circleRadius = 0f // 圆点大小
        set1.setDrawCircleHole(false)   // 设置没有圆孔
        set1.setDrawFilled(true)    // 显示线条下部分颜色
        set1.valueTextColor = Color.parseColor("#8FC7CC")   // 数值颜色
        set1.mode = LineDataSet.Mode.CUBIC_BEZIER  // 显示曲线
        set1.setDrawValues(false)  // 不显示数值
        set1.setDrawCircles(false)  // 不显示圆点
        set1.valueFormatter = object : ValueFormatter() {
            override fun getFormattedValue(value: Float): String {
                return DecimalFormat("0.00").format(value.toDouble())   // 数值保留2位小数
            }
        }
    }

    private fun initTempChart() {
        var values = LinkedList<Entry>()
        var set1 = LineDataSet(values, "")

        viewModel.pointTempData.observe(viewLifecycleOwner) {
            val lineData = LineData(set1) // 创建LineData对象并将set1添加到其中
            bind.tempLineHart.data = lineData // 将LineData设置给图表

//            viewModel.pointTempData.observe(viewLifecycleOwner) { data ->
            val entries = it.mapIndexed { index, fl -> Entry(index.toFloat(), fl) }
            set1.values = entries // 更新LineDataSet的数值
            lineData.notifyDataChanged() // 通知LineData数据已改变
            bind.tempLineHart.notifyDataSetChanged() // 刷新图表
            bind.tempLineHart.invalidate()
            Log.i("OverviewChartFragment", viewModel.pointHeartData.value.toString())
        }
//        }
// x轴
        val xAxis = bind.tempLineHart.xAxis
        xAxis.position = XAxis.XAxisPosition.BOTTOM // x轴底部显示
        xAxis.isEnabled = false
        xAxis.setDrawGridLines(false)   // 隐藏x轴线条
        xAxis.textColor = Color.parseColor("#8FC7CC")   // x轴字体颜色
        xAxis.axisLineColor = Color.TRANSPARENT // 底部x轴透明色
        xAxis.labelCount = 1000    // x轴显示1000段
// 左边y轴
        val leftAxis = bind.tempLineHart.axisLeft
//        leftAxis.axisMinimum = 34f     // y轴最小值
        leftAxis.axisMaximum = 50f    // y轴最大值
        leftAxis.enableGridDashedLine(5f, 5f, 0f) // y轴虚线
        leftAxis.textColor = Color.parseColor("#8FC7CC")    // y轴字体颜色
        leftAxis.axisLineColor = Color.TRANSPARENT  // y轴边线颜色透明
        leftAxis.gridColor = Color.parseColor("#335566")    // y轴颜色
        leftAxis.axisMinimum = 0f     // y轴最小值
        leftAxis.labelCount = 6    // y轴显示6段
        leftAxis.valueFormatter = object : ValueFormatter() {
            override fun getAxisLabel(value: Float, axis: AxisBase?): String {
                return DecimalFormat("0.00").format(value.toDouble())   // 数值保留2位小数
            }
        }
        bind.tempLineHart.axisRight.isEnabled = false // 隐藏右边y轴
        bind.tempLineHart.setVisibleXRangeMaximum(5F);
        bind.tempLineHart.data = LineData(set1)
        bind.tempLineHart.legend.isEnabled = false // 隐藏左下角说明
        bind.tempLineHart.setTouchEnabled(false)   // 设置图表不可点击
        bind.tempLineHart.description.isEnabled = false   // 隐藏右下角描述

// 在这里设置线
        set1.color = Color.parseColor("#8FC7CC")    // 线条颜色
        set1.lineWidth = 2f // 线条宽度
        set1.setCircleColor(Color.parseColor("#8FC7CC"))    // 圆点颜色
        set1.circleRadius = 3f // 圆点大小
        set1.setDrawCircleHole(false)   // 设置没有圆孔
        set1.setDrawFilled(true)    // 显示线条下部分颜色
        set1.valueTextColor = Color.parseColor("#8FC7CC")   // 数值颜色
        set1.mode = LineDataSet.Mode.CUBIC_BEZIER  // 显示曲线
        set1.setDrawValues(false)  // 不显示数值
        set1.setDrawCircles(false)  // 不显示圆点
        set1.valueFormatter = object : ValueFormatter() {
            override fun getFormattedValue(value: Float): String {
                return DecimalFormat("0.00").format(value.toDouble())   // 数值保留2位小数
            }
        }
    }

    private fun initSkinChart() {
        var values = LinkedList<Entry>()
        var set1 = LineDataSet(values, "")


        viewModel.pointSkinData.observe(viewLifecycleOwner) {
            val lineData = LineData(set1) // 创建LineData对象并将set1添加到其中
            bind.skinLineHart.data = lineData // 将LineData设置给图表

            viewModel.pointSkinData.observe(viewLifecycleOwner) { data ->
                val entries = data.mapIndexed { index, fl -> Entry(index.toFloat(), fl) }
                set1.values = entries // 更新LineDataSet的数值
                lineData.notifyDataChanged() // 通知LineData数据已改变
                bind.skinLineHart.notifyDataSetChanged() // 刷新图表
                bind.skinLineHart.invalidate()
                Log.i("OverviewChartFragment", viewModel.pointHeartData.value.toString())
            }
        }

// x轴
        val xAxis = bind.skinLineHart.xAxis
        xAxis.position = XAxis.XAxisPosition.BOTTOM // x轴底部显示
        xAxis.isEnabled = true
        xAxis.setDrawGridLines(false)   // 隐藏x轴线条
        xAxis.textColor = Color.parseColor("#8FC7CC")   // x轴字体颜色
        xAxis.axisLineColor = Color.TRANSPARENT // 底部x轴透明色
        xAxis.labelCount = 1000    // x轴显示1000段
// 左边y轴
        val leftAxis = bind.skinLineHart.axisLeft
        leftAxis.axisMinimum = 0f     // y轴最小值
        leftAxis.axisMaximum = 3080f    // y轴最大值
        leftAxis.enableGridDashedLine(5f, 5f, 0f) // y轴虚线
        leftAxis.textColor = Color.parseColor("#8FC7CC")    // y轴字体颜色
        leftAxis.axisLineColor = Color.TRANSPARENT  // y轴边线颜色透明
        leftAxis.gridColor = Color.parseColor("#335566")    // y轴颜色
        leftAxis.labelCount = 6    // y轴显示6段
        leftAxis.valueFormatter = object : ValueFormatter() {
            override fun getAxisLabel(value: Float, axis: AxisBase?): String {
                return DecimalFormat("0.00").format(value.toDouble())   // 数值保留2位小数
            }
        }
        bind.skinLineHart.axisRight.isEnabled = false  // 隐藏右边y轴
        bind.skinLineHart.setVisibleXRangeMaximum(5F);
        bind.skinLineHart.data = LineData(set1)
        bind.skinLineHart.legend.isEnabled = false // 隐藏左下角说明
        bind.skinLineHart.setTouchEnabled(false)   // 设置图表不可点击
        bind.skinLineHart.description.isEnabled = false   // 隐藏右下角描述

// 在这里设置线
        set1.color = Color.parseColor("#8FC7CC")    // 线条颜色
        set1.lineWidth = 2f // 线条宽度
        set1.setCircleColor(Color.parseColor("#8FC7CC"))    // 圆点颜色
        set1.circleRadius = 3f // 圆点大小
        set1.setDrawCircleHole(false)   // 设置没有圆孔
        set1.setDrawFilled(true)    // 显示线条下部分颜色
        set1.valueTextColor = Color.parseColor("#8FC7CC")   // 数值颜色
        set1.mode = LineDataSet.Mode.CUBIC_BEZIER  // 显示曲线
        set1.setDrawValues(false)  // 不显示数值
        set1.setDrawCircles(false)  // 不显示圆点
        set1.valueFormatter = object : ValueFormatter() {
            override fun getFormattedValue(value: Float): String {
                return DecimalFormat("0.00").format(value.toDouble())   // 数值保留2位小数
            }
        }


    }
}