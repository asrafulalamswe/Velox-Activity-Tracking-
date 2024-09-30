package com.dynamicdriller.velox.ui.fragments

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.dynamicdriller.velox.R
import com.dynamicdriller.velox.databinding.FragmentStatisticsBinding
import com.dynamicdriller.velox.other.CustomMarkerView
import com.dynamicdriller.velox.other.TrackingUtility
import com.dynamicdriller.velox.ui.viewmodels.StatisticsViewModel
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import dagger.hilt.android.AndroidEntryPoint
import kotlin.math.round

@AndroidEntryPoint
class StatisticsFragment : Fragment(R.layout.fragment_statistics){
    private val viewModel: StatisticsViewModel by viewModels()
    private lateinit var binding: FragmentStatisticsBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentStatisticsBinding.inflate(layoutInflater)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        subscribeToObservers()
        setUpBarChart()
    }


    private fun setUpBarChart(){
        binding.barChart.xAxis.apply {
            position = XAxis.XAxisPosition.BOTTOM
            setDrawLabels(false)
            axisLineColor = Color.BLUE
            textColor = Color.BLACK
        }
        binding.barChart.axisLeft .apply {
            axisLineColor = Color.BLUE
            textColor = Color.BLACK
            setDrawGridLines(false)
        }
        binding.barChart.axisRight.apply {
            axisLineColor = Color.BLUE
            textColor = Color.BLACK
            setDrawGridLines(false)
        }
        binding.barChart.apply {
            description.text = "All activities here"
            legend.isEnabled = false
        }
    }


    private fun subscribeToObservers(){
        viewModel.totalTimeRun.observe(viewLifecycleOwner, Observer {
            it?.let {
                val totalTimeRun = TrackingUtility.getFormattedStopWatchTime(it)
                binding.tvTotalTime.text = totalTimeRun
            }
        })
        viewModel.totalDistance.observe(viewLifecycleOwner, Observer {
            it?.let {
                val km = it/1000f
                val totalDistance = round(km * 10f) / 10f
                val totalDistanceString = "${totalDistance} Km"
                binding.tvTotalDistance.text = totalDistanceString
            }
        })

        viewModel.totalCaloriesBurned.observe(viewLifecycleOwner, Observer {
            it?.let {
                val totalcalories = "${it} Kcal"
                binding.tvTotalCalories.text = totalcalories
            }
        })

        viewModel.totalAvgSpeed.observe(viewLifecycleOwner, Observer {
            it?.let {
                val avgSpeed = round(it * 10f) / 10f
                val avgSpeedString = "${avgSpeed} Km/h"
                binding.tvAverageSpeed.text = avgSpeedString
            }
        })

        viewModel.runsSortedByDate.observe(viewLifecycleOwner, Observer {
            it?.let {
                val allAvgSpeeds = it.indices.map { i-> BarEntry(i.toFloat(), round((it[i].distanceInMeters/1000f) * 10f)/10f ) }
                val barDataSet = BarDataSet(allAvgSpeeds, "Distance Per Activity").apply {
                    valueTextColor = Color.WHITE
                    color = Color.BLUE
                }
                binding.barChart.data = BarData(barDataSet)
                binding.barChart.marker = CustomMarkerView(it.reversed(), requireContext(), R.layout.marker_view)
                binding.barChart.invalidate()

            }
        })

    }
}