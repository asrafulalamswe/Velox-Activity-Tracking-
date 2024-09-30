package com.dynamicdriller.velox.ui.fragments

import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.dynamicdriller.velox.R
import com.dynamicdriller.velox.databinding.FragmentSetupBinding
import com.dynamicdriller.velox.other.Constants.KEY_FIRST_TIME_TOGGLE
import com.dynamicdriller.velox.other.Constants.KEY_NAME
import com.dynamicdriller.velox.other.Constants.KEY_WEIGHT
import com.dynamicdriller.velox.services.TrackingService
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SetupFragment: Fragment(R.layout.fragment_setup) {
    @Inject
    lateinit var sharedpref : SharedPreferences
    @set:Inject
    var isFirstAppOen = true

    private lateinit var binding: FragmentSetupBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSetupBinding.inflate(layoutInflater)
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if(!isFirstAppOen){
            val  navOptions = NavOptions.Builder().setPopUpTo(R.id.setupFragment, true).build()
            if (TrackingService.isTracking.value==true){
                findNavController().navigate(R.id.trackingFragment, savedInstanceState,navOptions)
            }else{
                findNavController().navigate(R.id.action_setupFragment_to_biCycleFragment, savedInstanceState,navOptions)
            }
        }


        binding.tvContinue.setOnClickListener {
            val success = writePersonalDataToSharedPref()
            if (success){
                sharedpref.edit()
                    .putBoolean(KEY_FIRST_TIME_TOGGLE, false)
                    .apply()
                findNavController().navigate(R.id.action_setupFragment_to_biCycleFragment)
            }else{
                Snackbar.make(requireView(), "Please Enter all the inputs", Snackbar.LENGTH_SHORT).show()
            }

        }
    }

    private fun writePersonalDataToSharedPref(): Boolean {
        val name = binding.etName.text.toString()
        val weight = binding.etWeight.text.toString()
        if (name.isEmpty() || weight.isEmpty()){
            return false
        }
        sharedpref.edit()
            .putString(KEY_NAME, name)
            .putFloat(KEY_WEIGHT, weight.toFloat())
            .putBoolean(KEY_FIRST_TIME_TOGGLE, false)
            .apply()
//        val toolbarText = "Let's go, $name !"
//        MainActivity().binding.tvToolbarTitle.text =  toolbarText
        return true
    }
}