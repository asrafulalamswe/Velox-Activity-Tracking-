package com.dynamicdriller.velox.ui.fragments

import android.annotation.SuppressLint
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.dynamicdriller.velox.R
import com.dynamicdriller.velox.databinding.FragmentSettingsBinding
import com.dynamicdriller.velox.other.Constants.KEY_NAME
import com.dynamicdriller.velox.other.Constants.KEY_WEIGHT
import com.dynamicdriller.velox.ui.MainActivity
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SettingsFragment : Fragment(R.layout.fragment_settings){

    @Inject
    lateinit var sharedpref : SharedPreferences

    private lateinit var binding: FragmentSettingsBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSettingsBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loadFieldsFromSharedPref()
        binding.btnApplyChanges.setOnClickListener{
            val success = applyChangesToSharedPref()
            if (success){
                Snackbar.make(requireView(), "Changes Saved!", Snackbar.LENGTH_SHORT).show()
            }else{
                Snackbar.make(requireView(), "Please fill out all the fields", Snackbar.LENGTH_SHORT).show()
            }
        }
    }

    private fun loadFieldsFromSharedPref(){
        val name = sharedpref.getString(KEY_NAME, "") ?: ""
        val weight = sharedpref.getFloat(KEY_WEIGHT, 80f)
        binding.etName.setText(name)
        binding.etWeight.setText(weight.toString())
    }

    @SuppressLint("SetTextI18n")
    private fun applyChangesToSharedPref(): Boolean {
        val nameText = binding.etName.text.toString()
        val weightText = binding.etWeight.text.toString()
        if (nameText.isEmpty() || weightText.isEmpty()){
            return false
        }
        sharedpref.edit()
            .putString(KEY_NAME, nameText)
            .putFloat(KEY_WEIGHT, weightText.toFloat())
            .apply()
//        MainActivity().binding.tvToolbarTitle.text =  "Let's Go $nameText"
        return true
    }

}
