package com.dynamicdriller.velox.ui.fragments

import android.Manifest
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.dynamicdriller.velox.R
import com.dynamicdriller.velox.adapters.BicycleAdapter
import com.dynamicdriller.velox.databinding.FragmentBicycleBinding
import com.dynamicdriller.velox.other.Constants.BACKGROUND_LOCATION_PERMISSION_CODE
import com.dynamicdriller.velox.other.Constants.REQUEST_CODE_LOCATION_PERMISSION
import com.dynamicdriller.velox.other.SortType
import com.dynamicdriller.velox.other.TrackingUtility
import com.dynamicdriller.velox.ui.viewmodels.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions
import timber.log.Timber

@AndroidEntryPoint
class BiCycleFragment : Fragment(R.layout.fragment_bicycle), EasyPermissions.PermissionCallbacks {
    private lateinit var binding: FragmentBicycleBinding
    private val viewModel:MainViewModel by viewModels()
    private lateinit var bicycleAdapter: BicycleAdapter
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentBicycleBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requestPermissions()
        setUpRecyclerView()
        viewModel.activities.observe(viewLifecycleOwner) { newList ->
            setUpRecyclerView()
            bicycleAdapter.submitList(newList)
        }
        binding.fab.setOnClickListener {
            findNavController().navigate(R.id.action_biCycleFragment_to_trackingFragment)
        }
        when(viewModel.sortType){
            SortType.DATE -> binding.spFilter.setSelection(0)
            SortType.RUNNING_TIME -> binding.spFilter.setSelection(1)
            SortType.DISTANCE -> binding.spFilter.setSelection(2)
            SortType.AVG_SPEED -> binding.spFilter.setSelection(3)
            SortType.CALORIES_BURNED -> binding.spFilter.setSelection(4)
        }
        binding.spFilter.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(adapterView: AdapterView<*>?, view: View?, position: Int, id: Long) {
                when(position){
                    0-> viewModel.sortRuns(SortType.DATE)
                    1-> viewModel.sortRuns(SortType.RUNNING_TIME)
                    2-> viewModel.sortRuns(SortType.DISTANCE)
                    3-> viewModel.sortRuns(SortType.AVG_SPEED)
                    4-> viewModel.sortRuns(SortType.CALORIES_BURNED)
                }
                Timber.d("updated filter: size = ${viewModel.activities.value?.size}")
            }
            override fun onNothingSelected(p0: AdapterView<*>?) {}

        }

    }



    private fun setUpRecyclerView() {
        binding.rvRuns.apply {
            layoutManager = LinearLayoutManager(requireContext())
            bicycleAdapter = BicycleAdapter()
            adapter = bicycleAdapter
        }
    }

    private fun requestPermissions() {
        if(TrackingUtility.hasLocationPermissions(requireContext())) {
            return
        }
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            EasyPermissions.requestPermissions(
                this,
                "You need to accept location permissions to use this app.",
                REQUEST_CODE_LOCATION_PERMISSION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
        } else {
            EasyPermissions.requestPermissions(
                this,
                "You need to accept location permissions to use this app.",
                REQUEST_CODE_LOCATION_PERMISSION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION,
            )
            EasyPermissions.requestPermissions(
                this,
                "You need to accept background location permissions to use this app. Click ok and In Location Settings, select Allow All the time.",
                BACKGROUND_LOCATION_PERMISSION_CODE,
                Manifest.permission.ACCESS_BACKGROUND_LOCATION
            )
        }
    }

    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
        if(EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            AppSettingsDialog.Builder(this).build().show()
        } else {
            requestPermissions()
        }
    }

    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {}

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }


}