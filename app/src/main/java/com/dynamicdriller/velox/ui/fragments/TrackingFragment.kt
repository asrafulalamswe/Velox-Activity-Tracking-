package com.dynamicdriller.velox.ui.fragments

import android.annotation.SuppressLint
import android.content.DialogInterface
import android.content.Intent
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.dynamicdriller.velox.R
import com.dynamicdriller.velox.databinding.FragmentTrackingBinding
import com.dynamicdriller.velox.databinding.FragmentTrackingBinding.*
import com.dynamicdriller.velox.db.Activity
import com.dynamicdriller.velox.other.Constants.ACTION_PAUSE_SERVICE
import com.dynamicdriller.velox.other.Constants.ACTION_START_OR_RESUME_SERVICE
import com.dynamicdriller.velox.other.Constants.ACTION_STOP_SERVICE
import com.dynamicdriller.velox.other.Constants.MAP_ZOOM
import com.dynamicdriller.velox.other.Constants.POLYLINE_COLOR
import com.dynamicdriller.velox.other.Constants.POLYLINE_WIDTH
import com.dynamicdriller.velox.other.TrackingUtility
import com.dynamicdriller.velox.services.PolyLine
import com.dynamicdriller.velox.services.TrackingService
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapsInitializer
import com.google.android.gms.maps.OnMapsSdkInitializedCallback
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import java.util.Calendar
import javax.inject.Inject
import kotlin.math.round


const val CANCEL_TRACKING_DIALOG_CHECK = "CANCEL_TRACKING_DIALOG_CHECK"

@AndroidEntryPoint
class TrackingFragment : Fragment(R.layout.fragment_tracking), OnMapsSdkInitializedCallback {
    private var isPaused = false
    private var savedBicycle=Activity()
    private lateinit var binding: FragmentTrackingBinding
    private var map: GoogleMap? = null
    private var isTracking = false
    private var pathPoints = mutableListOf<PolyLine>()
    private var menu:Menu? = null
    @set:Inject
    var weight = 80f
    private var currentTimeInMillis = 0L
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        MapsInitializer.initialize(requireContext(), MapsInitializer.Renderer.LATEST, this)
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        setHasOptionsMenu(true)
        binding = inflate(layoutInflater)
        return binding.root
    }

    @SuppressLint("MissingPermission")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())
        binding.mapView.onCreate(savedInstanceState)

        if (!isLocationEnabled()) {
            showLocationDialog()
        }

        binding.btnToggleRun.setOnClickListener {
            toggleRun()
            if (isTracking){
                isPaused =!isPaused
            }
        }

        if (savedInstanceState!=null){
            val cancelTrackingDialog = parentFragmentManager.findFragmentByTag(
                CANCEL_TRACKING_DIALOG_CHECK) as CancelTrackingDialog?
            cancelTrackingDialog?.setYesListener {
                stopRun()
                isTracking = false
                isPaused = false
            }
        }

        binding.btnFinishRun.setOnClickListener {
            try {
                zoomToSeeWholeTrack()
            }catch (e:Exception){
                Timber.d("Error: ${e.localizedMessage?.toString()}")
            }
            endRunAndSaveToDb()
            binding.btnFinishRun.visibility = View.GONE
            binding.btnToggleRun.text = "Start"
        }
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location: Location? ->
                if (location != null) {
                    val cameraPosition = CameraPosition.Builder()
                        .target(LatLng(location.latitude, location.longitude))
                        .zoom(15f) // Zoom level
                        .build()
                    map?.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))
                    map?.uiSettings?.isZoomControlsEnabled = true
                    map?.isMyLocationEnabled = true
                    map?.isTrafficEnabled = true
                    map?.uiSettings?.setAllGesturesEnabled(true)
                    map?.uiSettings?.isIndoorLevelPickerEnabled = true
                    map?.uiSettings?.isCompassEnabled = true
                    map?.mapType  = GoogleMap.MAP_TYPE_NORMAL
                    map?.uiSettings?.isMapToolbarEnabled = true
                    val customMarkerIcon = BitmapDescriptorFactory.fromResource(R.drawable.img)
                    val markerOptions = MarkerOptions()
                        .position(LatLng(location.latitude, location.longitude))
                        .icon(customMarkerIcon)
                    map?.isBuildingsEnabled = true
                    map?.addMarker(markerOptions)
                }
            }

        binding.mapView.getMapAsync {
            map = it
            addAllPolyLines()
        }
        subscribeToObserver()


    }

    private fun subscribeToObserver(){
        TrackingService.isTracking.observe(viewLifecycleOwner) {
            updateTracking(it)
        }
        TrackingService.pathPoints.observe(viewLifecycleOwner) {
            pathPoints = it
            addLatestPolyline()
            moveCameraToUser()
        }
        TrackingService.timeRunInMillis.observe(viewLifecycleOwner) {
            currentTimeInMillis = it
            val formattedTime = TrackingUtility.getFormattedStopWatchTime(currentTimeInMillis, true)
            binding.tvTimer.text = formattedTime
        }
    }

    private fun toggleRun(){
        if (isTracking){
            menu?.getItem(0)?.isVisible = true
            sendCommandToService(ACTION_PAUSE_SERVICE)
        }else{
            sendCommandToService(ACTION_START_OR_RESUME_SERVICE)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.toolbar_tracking_menu,menu)
        this.menu = menu
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)
        if (currentTimeInMillis>0){
            this.menu?.getItem(0)?.isVisible = true
        }
    }

    private fun showCancelTrackingDialog(){
        CancelTrackingDialog().apply {
            setYesListener {
                stopRun()
            }
        }.show(parentFragmentManager, CANCEL_TRACKING_DIALOG_CHECK)
    }

    private fun stopRun(){
        binding.tvTimer.text = "00:00:00:00"
        sendCommandToService(ACTION_STOP_SERVICE)
        if (findNavController().currentDestination?.id==R.id.trackingFragment){
            findNavController().navigate(R.id.action_trackingFragment_to_biCycleFragment)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.miCancelTracking->{
                showCancelTrackingDialog()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun updateTracking(isTracking:Boolean){
        this.isTracking = isTracking
        if (!isTracking && currentTimeInMillis >0L){
            if (isPaused){
                binding.btnToggleRun.text = "Resume"
                this.isPaused = !this.isPaused
            }else{
                binding.btnToggleRun.text = "Start"
            }
            binding.btnFinishRun.visibility = View.VISIBLE
        }else{
            if (isTracking){
                binding.btnToggleRun.text = "Pause"
                menu?.getItem(0)?.isVisible = true
                binding.btnFinishRun.visibility = View.GONE
            }else{
                binding.btnToggleRun.text = "Start"
            }

        }
    }

    private fun moveCameraToUser(){
        if (pathPoints.isNotEmpty() && pathPoints.last().isNotEmpty()){
            map?.animateCamera(
                CameraUpdateFactory.newLatLngZoom(pathPoints.last().last(), MAP_ZOOM)
            )
        }
    }
    private fun zoomToSeeWholeTrack() {
        val boundsBuilder = LatLngBounds.builder()
        // Add all polyline points to the boundsBuilder
        for (polyline in pathPoints) {
            for (position in polyline) {
                boundsBuilder.include(position)
            }
        }
        val bounds = boundsBuilder.build()
        val paddingStartEnd = resources.getDimensionPixelSize(R.dimen.map_padding_horizontal) // Adjust padding as needed
        val paddingTop = resources.getDimensionPixelSize(R.dimen.map_padding_top) // Adjust padding as needed
        val cameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds, paddingStartEnd)
        // Move the map camera to the specified bounds with padding
        map?.moveCamera(cameraUpdate)
    }

    private fun endRunAndSaveToDb(){
        try {
            map?.snapshot {bmp->
                var distanceInMeters = 0
                for (polyline in pathPoints){
                    distanceInMeters += TrackingUtility.calculatePolyLineLength(polyline).toInt()
                }
                val avgSpeed = if (distanceInMeters>0) round((distanceInMeters / 1000f) / (currentTimeInMillis / 1000f / 60 / 60) * 10) / 10f else 0f
                val dateTimeStamp = Calendar.getInstance().timeInMillis
                val caloriesBurned = ((distanceInMeters/1000f)* weight).toInt()
                savedBicycle = Activity(image = bmp, timestamp = dateTimeStamp, avgSpeedInKm =  avgSpeed, distanceInMeters =  distanceInMeters, timeInMillis =  currentTimeInMillis, caloriesBurn =  caloriesBurned)
                val action = TrackingFragmentDirections.actionTrackingFragmentToSaveRideFragment(savedBicycle)
                findNavController().navigate(action)
                stopRun()
            }
        }catch (e:Exception){
            Timber.d("Exception: ${e.localizedMessage?.toString()}")
        }
    }


    private fun addAllPolyLines(){
        for (polyLine in pathPoints){
            val polylineOptions = PolylineOptions()
                .color(POLYLINE_COLOR)
                .width(POLYLINE_WIDTH)
                .addAll(polyLine)
            map?.addPolyline(polylineOptions)
        }
    }

    private fun addLatestPolyline(){
        if (pathPoints.isNotEmpty() && pathPoints.last().size>1){
            val preLastLatLang = pathPoints.last()[pathPoints.last().size-2]
            val lastLatLang = pathPoints.last().last()
            val polylineOptions = PolylineOptions()
                .color(POLYLINE_COLOR)
                .width(POLYLINE_WIDTH)
                .add(preLastLatLang,lastLatLang)
            map?.addPolyline(polylineOptions)
        }
    }

    private fun sendCommandToService(action:String){
        Intent(requireContext(), TrackingService::class.java).also {
            it.action = action
            requireContext().startService(it)
        }
    }


    private fun isLocationEnabled(): Boolean {
        // Use requireContext() to access the context inside a fragment
        val locationManager = ContextCompat.getSystemService(requireContext(), LocationManager::class.java)
        return locationManager?.isProviderEnabled(LocationManager.GPS_PROVIDER) == true ||
                locationManager?.isProviderEnabled(LocationManager.NETWORK_PROVIDER) == true
    }

    private fun showLocationDialog() {
        val builder = AlertDialog.Builder(requireContext())  // Use requireContext() here too
        builder.setTitle("Enable Location")
        builder.setMessage("Location services are required to use this app. Please enable location services.")
        builder.setPositiveButton("Open Settings") { dialog: DialogInterface, _: Int ->
            // Open the location settings
            val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
            startActivity(intent)
        }
        builder.setNegativeButton("Cancel") { dialog: DialogInterface, _: Int ->
            dialog.dismiss()
        }
        builder.create().show()
    }

    override fun onResume() {
        super.onResume()
        binding.mapView.onStart()

        // Clear old polylines to avoid duplication
        map?.clear()

        // Add all the polylines back to the map
        if (pathPoints.isNotEmpty()) {
            addAllPolyLines()
        }
        // Move the camera to the user's current location
        moveCameraToUser()
    }

    override fun onStart() {
        super.onStart()
        binding.mapView.onStart()

        // Clear old polylines to avoid duplication
        map?.clear()

        // Add all the polylines back to the map
        if (pathPoints.isNotEmpty()) {
            addAllPolyLines()
        }

        // Move the camera to the user's current location
        moveCameraToUser()

        // Re-subscribe to the tracking service observers
        subscribeToObserver()
    }

    override fun onStop() {
        super.onStop()
        binding.mapView.onStop()
        if (pathPoints.isNotEmpty()){
            addAllPolyLines()
        }
    }

    override fun onPause() {
        super.onPause()
        binding.mapView.onPause()
        if (pathPoints.isNotEmpty()){
            addAllPolyLines()
        }
    }

    override fun onLowMemory() {
        super.onLowMemory()
        binding.mapView.onLowMemory()
    }

    override fun onDestroy() {
        super.onDestroy()
        binding.mapView.onDestroy()
        if (pathPoints.isNotEmpty()){
            addAllPolyLines()
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        binding.mapView.onSaveInstanceState(outState)
    }

    override fun onMapsSdkInitialized(renderer: MapsInitializer.Renderer) {
        when (renderer) {
            MapsInitializer.Renderer.LATEST -> Timber.d("MapsDemo", "The latest version of the renderer is used.")
            MapsInitializer.Renderer.LEGACY -> Timber.d("MapsDemo", "The legacy version of the renderer is used.")
        }
    }
}