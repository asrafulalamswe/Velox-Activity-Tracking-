package com.dynamicdriller.velox.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.dynamicdriller.velox.R
import com.dynamicdriller.velox.databinding.ItemRunBinding
import com.dynamicdriller.velox.db.BiCycle
import com.dynamicdriller.velox.other.TrackingUtility
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class RunAdapter: RecyclerView.Adapter<RunAdapter.RunViewHolder>() {

    inner class RunViewHolder( val binding : ItemRunBinding):RecyclerView.ViewHolder(binding.root){

    }

    private val difCallback = object : DiffUtil.ItemCallback<BiCycle>(){
        override fun areItemsTheSame(oldItem: BiCycle, newItem: BiCycle): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: BiCycle, newItem: BiCycle): Boolean {
            return oldItem.hashCode() == newItem.hashCode()
        }
    }

    private val differ = AsyncListDiffer(this, difCallback)

    fun submitList (list:List<BiCycle>) = differ.submitList(list)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RunViewHolder {
        val binding =  RunViewHolder(ItemRunBinding.inflate(LayoutInflater.from(parent.context)))
        return binding
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    override fun onBindViewHolder(holder: RunViewHolder, position: Int) {
        val run = differ.currentList[position]
        holder.itemView.apply {
            Glide.with(this).load(run.image).placeholder(R.drawable.baseline_loading)
                .error(R.drawable.baseline_broken_image_24).into(holder.binding.ivRunImage)

            val calendar = Calendar.getInstance().apply {
                timeInMillis = run.timestamp
            }
            holder.binding.titleTV.text = run.title
            val dateFormat = SimpleDateFormat("MMMM d, yyyy 'at' hh:mm a", Locale.getDefault())
            val currentDate = Date() // Create a Date object for the current date and time
            holder.binding.tvDate.text = dateFormat.format(currentDate)

            Timber.d("Date is ${dateFormat.format(calendar.time)}")

            val avgSpeed = "${run.avgSpeedInKm} km/h"
            holder.binding.tvAvgSpeed.text =  if (run.avgSpeedInKm>0) avgSpeed else "00 km/h"

            val distanceInKm = "${run.distanceInMeters/1000f} km"
            holder.binding.tvDistance.text =  distanceInKm
            holder.binding.tvTime.text =  TrackingUtility.getFormattedStopWatchTime(run.timeInMillis)

            val caloriesBurned =  "${run.caloriesBurn} kcal"
            holder.binding.tvCalories.text =  caloriesBurned


        }
    }
}