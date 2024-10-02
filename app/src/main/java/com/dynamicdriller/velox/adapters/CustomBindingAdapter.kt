package com.dynamicdriller.velox.adapters

import android.graphics.Bitmap
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.dynamicdriller.velox.other.TrackingUtility

@BindingAdapter("app:setImageResources")
fun setImageResources(imageView: ImageView, imgBitmap: Bitmap?) {
    imgBitmap.let {
        imageView.setImageBitmap(it)
    }
}

@BindingAdapter("app:setDuration")
fun setDuration(duration:TextView, timeInMillis:Long) {
    duration.text = TrackingUtility.getFormattedStopWatchTime(timeInMillis)
}

@BindingAdapter("app:setDistance")
fun setDistance(distance:TextView, distanceInMeter:Float) {
    val distanceInKm = distanceInMeter/1000f
    distance.text = buildString {
        append(distanceInKm)
        append(" KM")
    }
}

@BindingAdapter("app:setAvgSpeed")
fun setAvgSpeed(avgSpeed:TextView, avgspeedinkm:Float) {
    avgSpeed.text = buildString {
        append(avgspeedinkm.toString())
        append(" KM/H")
    }
}