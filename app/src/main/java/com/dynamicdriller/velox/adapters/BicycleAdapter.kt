package com.dynamicdriller.velox.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.dynamicdriller.velox.R
import com.dynamicdriller.velox.databinding.ItemRunBinding
import com.dynamicdriller.velox.db.Activity
import com.dynamicdriller.velox.other.TrackingUtility
import com.dynamicdriller.velox.ui.fragments.BiCycleFragmentDirections
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class BicycleAdapter: RecyclerView.Adapter<BicycleAdapter.RunViewHolder>() {

    inner class RunViewHolder( val binding : ItemRunBinding):RecyclerView.ViewHolder(binding.root){
        fun bind(activity:Activity){
            binding.itemCardView.setOnClickListener {
                val action = BiCycleFragmentDirections.actionBiCycleFragmentToActivityDetailsFragment(activity)
                it.findNavController().navigate(action)
            }
        }
    }

    private val difCallback = object : DiffUtil.ItemCallback<Activity>(){
        override fun areItemsTheSame(oldItem: Activity, newItem: Activity): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Activity, newItem: Activity): Boolean {
            return oldItem.hashCode() == newItem.hashCode()
        }
    }

    private val differ = AsyncListDiffer(this, difCallback)

    fun submitList (list:List<Activity>) = differ.submitList(list)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RunViewHolder {
        val binding =  RunViewHolder(ItemRunBinding.inflate(LayoutInflater.from(parent.context)))
        return binding
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    override fun onBindViewHolder(holder: RunViewHolder, position: Int) {
        val activity = differ.currentList[position]
        holder.bind(activity)
        holder.itemView.setOnClickListener {
            val action = BiCycleFragmentDirections.actionBiCycleFragmentToActivityDetailsFragment(activity)
            it.findNavController().navigate(action)
        }
        holder.itemView.apply {
            Glide.with(this).load(activity.image).placeholder(R.drawable.baseline_loading)
                .error(R.drawable.baseline_broken_image_24).into(holder.binding.ivRunImage)

            val calendar = Calendar.getInstance().apply {
                timeInMillis = activity.timestamp
            }
            holder.binding.titleTV.text = activity.title
            val dateFormat = SimpleDateFormat("MMMM d, yyyy 'at' hh:mm a", Locale.getDefault())
            val currentDate = Date() // Create a Date object for the current date and time
            holder.binding.tvDate.text = dateFormat.format(currentDate)

            Timber.d("Date is ${dateFormat.format(calendar.time)}")

            val avgSpeed = "${activity.avgSpeedInKm} km/h"
            holder.binding.tvAvgSpeed.text =  if (activity.avgSpeedInKm>0) avgSpeed else "00 km/h"

            val distanceInKm = "${activity.distanceInMeters/1000f} km"
            holder.binding.tvDistance.text =  distanceInKm
            holder.binding.tvTime.text =  TrackingUtility.getFormattedStopWatchTime(activity.timeInMillis)

            val caloriesBurned =  "${activity.caloriesBurn} kcal"
            holder.binding.tvCalories.text =  caloriesBurned
        }
    }
}