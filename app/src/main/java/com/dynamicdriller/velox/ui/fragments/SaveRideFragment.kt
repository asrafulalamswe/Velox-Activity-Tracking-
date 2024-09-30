package com.dynamicdriller.velox.ui.fragments

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.dynamicdriller.velox.R
import com.dynamicdriller.velox.databinding.FragmentSaveRideBinding
import com.dynamicdriller.velox.db.BiCycle
import com.dynamicdriller.velox.other.Constants
import com.dynamicdriller.velox.services.TrackingService
import com.dynamicdriller.velox.ui.viewmodels.MainViewModel
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import java.time.LocalTime


@AndroidEntryPoint
class SaveRideFragment : Fragment() {
    private lateinit var binding: FragmentSaveRideBinding
    private val args:SaveRideFragmentArgs  by navArgs()
    private val viewModel: MainViewModel by viewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentSaveRideBinding.inflate(layoutInflater)
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        Timber.d("Image from savefragment: ${args.bicycle.image.toString()}")
        binding.mapFragment.setImageBitmap(args.bicycle.image)
        binding.saveButton.setOnClickListener {
            var title = binding.rideName.editText?.text.toString()
            if (binding.rideName.editText?.text.isNullOrEmpty()) {
                val now = LocalTime.now() // get the current time
                title = if (now.isBefore(LocalTime.of(12, 0))) {
                    "Morning Ride"
                } else if (now.isBefore(LocalTime.of(18, 0))) {
                    "Afternoon Ride"
                } else if (now.isAfter(LocalTime.of(18, 0))) {
                    "Evening Ride"
                } else { // default case
                    "Night Ride"
                }
            }
            val biCycle = BiCycle(
                title = title,
                image = args.bicycle.image,
                timestamp = args.bicycle.timestamp,
                timeInMillis = args.bicycle.timeInMillis,
                avgSpeedInKm =  args.bicycle.avgSpeedInKm,
                distanceInMeters =  args.bicycle.distanceInMeters,
                caloriesBurn =  args.bicycle.caloriesBurn
            )
            viewModel.insertBiCycle(biCycle)
            stopRun()
            Snackbar.make(requireActivity().findViewById(R.id.rootView), "Ride Saved Successfully", Snackbar.LENGTH_SHORT).show()
        }
    }

    private fun stopRun(){
        sendCommandToService(Constants.ACTION_STOP_SERVICE)
        if (findNavController().currentDestination?.id==R.id.saveRideFragment){
            findNavController().navigate(R.id.action_saveRideFragment_to_biCycleFragment)
        }
    }

    private fun sendCommandToService(action:String){
        Intent(requireContext(), TrackingService::class.java).also {
            it.action = action
            requireContext().startService(it)
        }
    }
}