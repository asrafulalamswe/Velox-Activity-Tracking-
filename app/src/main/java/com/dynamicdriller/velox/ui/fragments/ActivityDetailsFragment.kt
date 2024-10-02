package com.dynamicdriller.velox.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.dynamicdriller.velox.databinding.FragmentActivityDetailsBinding
import com.dynamicdriller.velox.db.Activity
import com.dynamicdriller.velox.ui.viewmodels.MainViewModel


class ActivityDetailsFragment : Fragment() {
    private lateinit var binding: FragmentActivityDetailsBinding
    private val viewModel: MainViewModel by viewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentActivityDetailsBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val activity = arguments?.getSerializable("activity") as Activity
        binding.activity = activity

    }
}