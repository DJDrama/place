package com.place.www.ui.main.fragments

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.view.*
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.PermissionChecker
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.libraries.maps.CameraUpdateFactory
import com.google.android.libraries.maps.GoogleMap
import com.google.android.libraries.maps.OnMapReadyCallback
import com.google.android.libraries.maps.model.LatLng
import com.google.android.libraries.maps.model.Marker
import com.google.android.libraries.maps.model.MarkerOptions
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.AutocompleteActivity
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import com.place.www.R
import com.place.www.databinding.FragmentMapBinding
import com.place.www.model.LocationItem
import com.place.www.ui.showToast


class MapFragment : Fragment(), OnMapReadyCallback, GoogleMap.OnInfoWindowClickListener {
    companion object {
        const val ZOOM_LEVEL = 13f
        const val REQUESTING_LOCATION_UPDATES_KEY = "REQUESTING_LOCATION_UPDATES_KEY"
        const val REQUEST_CODE_PERMISSION = 101
        const val locationPermission = Manifest.permission.ACCESS_FINE_LOCATION
        const val MAX_NUMBER_REQUEST_PERMISSIONS = 2
        const val UPDATE_INTERVAL = 10 * 1000L
        const val FASTEST_INTERVAL = 2000L
        const val REQUEST_CHECK_SETTINGS = 1011
        const val AUTOCOMPLETE_REQUEST_CODE = 1
    }

    /** Places API **/
    // Set the fields to specify which types of place data to
    // return after the user has made a selection.
    private val fields =
        listOf(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG, Place.Field.ADDRESS)

    private var mHasPermission: Boolean = false
    private var mPermissionRequestCount: Int = 0
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var mLocationRequest: LocationRequest
    private var locationCallback: LocationCallback? = null
    private var requestingLocationUpdates = false

    private var _binding: FragmentMapBinding? = null
    private val binding get() = _binding!!

    private val mapFragmentViewModel: MapFragmentViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMapBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.map.onDestroy()
        mapFragmentViewModel.clearGoogleMap()
        if (locationCallback != null) {
            locationCallback = null
        }
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)
        requestPermissionsIfNecessary()
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult?) {
                locationResult ?: return
                for (location in locationResult.locations) {
                    // Update UI with location data

                    mapFragmentViewModel.setCurrentLocation(
                        LocationItem(
                            id = "CURRENT_LOCATION",
                            name = "Current Location",
                            latLng = LatLng(location.latitude, location.longitude),
                            address = ""
                        )
                    )
                    //just do once so break
                    stopLocationUpdates()
                    break
                }
            }
        }
        fusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(requireContext())
        updateValuesFromBundle(savedInstanceState)

        binding.map.let {
            it.onCreate(savedInstanceState)
            it.getMapAsync(this)
        }
        binding.ivSearch.setOnClickListener {
            // Start the autocomplete intent.
            val intent = Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY, fields)
                .build(requireContext())
            startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE)
        }

        subscribeObservers()
    }

    private fun subscribeObservers() {
        mapFragmentViewModel.mapReadyAndLocationMediatorLiveData.observe(viewLifecycleOwner) {
            it?.run {
                if (isMapReady) {
                    this.googleMap?.let { gMap ->
                        this.location?.let { locationItem ->
                            with(gMap) {
                                moveCamera(
                                    CameraUpdateFactory.newLatLngZoom(
                                        locationItem.latLng,
                                        ZOOM_LEVEL
                                    )
                                )
                                clear()
                                val markerOptions =
                                    MarkerOptions().position(locationItem.latLng!!).title(locationItem.name)
                                val marker = addMarker(markerOptions)
                                marker.showInfoWindow()
                                setOnInfoWindowClickListener(this@MapFragment)

                            }
                        } ?: if (requestingLocationUpdates) {
                            startLocationUpdates()
                        } else {
                            setLocationSettings()
                        }
                    }
                }
            }
        }

        mapFragmentViewModel.infoWindowClicked.observe(viewLifecycleOwner){
            it?.let{boolValue->
                if(boolValue){
                    mapFragmentViewModel.getLocationItem()?.let{locationItem->
                        val action = MapFragmentDirections.actionMapFragmentToMapDetailFragment(
                            locationItem
                        )
                        findNavController().navigate(action)
                        mapFragmentViewModel.setInfoWindowClicked(false)
                    }
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == AUTOCOMPLETE_REQUEST_CODE) {
            when (resultCode) {
                Activity.RESULT_OK -> {
                    data?.let {
                        val place = Autocomplete.getPlaceFromIntent(data)
                        Log.i("MapFragment", "Place: ${place.name}, ${place.id} ${place.latLng}")
                        with(place){
                            mapFragmentViewModel.setCurrentLocation(
                                LocationItem(
                                    id ?: "",
                                    name ?: "",
                                    latLng = LatLng(latLng!!.latitude, latLng!!.longitude),
                                    address?:""
                                )
                            )
                        }
                    }
                }
                AutocompleteActivity.RESULT_ERROR -> {
                    data?.let {
                        val status = Autocomplete.getStatusFromIntent(data)
                        Log.e("MapFragment", status.statusMessage ?: " ")
                    }
                }
                Activity.RESULT_CANCELED -> {
                    // The user canceled the operation.
                }
            }
            return
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun requestPermissionsIfNecessary() {
        mHasPermission = checkPermission()
        if (!mHasPermission) {
            if (mPermissionRequestCount < MAX_NUMBER_REQUEST_PERMISSIONS) {
                mPermissionRequestCount++
                requestPermissions(
                    arrayOf(locationPermission),
                    REQUEST_CODE_PERMISSION
                )
            } else {
                requireContext().showToast(
                    getString(R.string.set_permissions_in_settings)
                )
            }
        }
    }

    private fun checkPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            requireContext(),
            locationPermission
        ) == PackageManager.PERMISSION_GRANTED
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        // Check if permissions were granted after a permissions request flow.
        if (requestCode == REQUEST_CODE_PERMISSION) {
            //just check index 0 since we have only 1 permission
            if (grantResults[0] == PermissionChecker.PERMISSION_GRANTED) {
                getLastLocation()
            } else {
                requireContext().showToast(
                    getString(R.string.set_permissions_in_settings)
                )
            }
        }
    }

    private fun getLastLocation() {
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requireContext().showToast(
                getString(R.string.set_permissions_in_settings)
            )
            return
        }
        if (::fusedLocationProviderClient.isInitialized) {
            fusedLocationProviderClient.lastLocation.addOnSuccessListener { location: Location? ->
                location?.let {
                    mapFragmentViewModel.setCurrentLocation(
                        LocationItem(
                            id = "CURRENT_LOCATION",
                            name = "Current Location",
                            latLng = LatLng(location.latitude, location.longitude),
                            address = ""
                        )
                    )
                } ?: setLocationSettings()
            }
        }
    }

    private fun setLocationSettings() {
        // Create the location request to start receiving updates
        // https://developer.android.com/training/location/change-location-settings
        mLocationRequest = LocationRequest.create().apply {
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            interval = UPDATE_INTERVAL
            fastestInterval = FASTEST_INTERVAL
        }

        // Create LocationSettingsRequest object using location request
        val builder = LocationSettingsRequest.Builder().addLocationRequest(mLocationRequest)
        val locationSettingsRequest = builder.build()

        // Check whether location settings are satisfied
        // https://developers.google.com/android/reference/com/google/android/gms/location/SettingsClient
        val settingsClient = LocationServices.getSettingsClient(requireContext())
        val task = settingsClient.checkLocationSettings(locationSettingsRequest)
        task.addOnSuccessListener { locationSettingsResponse ->
            // All location settings are satisfied. The client can initialize
            // location requests here.
            requestingLocationUpdates = true
            startLocationUpdates()
        }
        task.addOnFailureListener { exception ->
            if (exception is ResolvableApiException) {
                // Location settings are not satisfied, but this can be fixed
                // by showing the user a dialog.
                try {
                    // Show the dialog by calling startResolutionForResult(),
                    // and check the result in onActivityResult().
                    exception.startResolutionForResult(
                        requireActivity(),
                        REQUEST_CHECK_SETTINGS
                    )
                } catch (sendEx: IntentSender.SendIntentException) {
                    // Ignore the error.
                    Log.e("Error", "SendIntentException:  $sendEx")

                }
            } else {
                requireContext().showToast("$exception")
            }
        }
    }

    private fun stopLocationUpdates() {
        requestingLocationUpdates = false
        fusedLocationProviderClient.removeLocationUpdates(locationCallback)
    }


    override fun onSaveInstanceState(outState: Bundle) {
        outState.putBoolean(REQUESTING_LOCATION_UPDATES_KEY, requestingLocationUpdates)
        super.onSaveInstanceState(outState)
    }

    private fun updateValuesFromBundle(savedInstanceState: Bundle?) {
        savedInstanceState ?: return
        // Update the value of requestingLocationUpdates from the Bundle.
        if (savedInstanceState.keySet().contains(REQUESTING_LOCATION_UPDATES_KEY)) {
            requestingLocationUpdates = savedInstanceState.getBoolean(
                REQUESTING_LOCATION_UPDATES_KEY
            )
        }
        // ...
        // Update UI to match restored state
        //updateUI()
    }

    private fun startLocationUpdates() {
        if (checkPermission()) {
            fusedLocationProviderClient.requestLocationUpdates(
                mLocationRequest,
                locationCallback,
                Looper.getMainLooper()
            )
        }
    }

    override fun onPause() {
        super.onPause()
        stopLocationUpdates()

    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_profile, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_my_profile -> {
                navigateToMyProfile()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun navigateToMyProfile() {
        findNavController().navigate(R.id.action_mapFragment_to_myProfileFragment)
    }

    override fun onMapReady(p0: GoogleMap?) {
        p0?.let {
            mapFragmentViewModel.setMapReady(true)
            mapFragmentViewModel.setGoogleMap(it)
        }
    }

    override fun onInfoWindowClick(p0: Marker?) {
        p0?.let{
            if(it.title == "Current Location") return
            mapFragmentViewModel.setInfoWindowClicked(true)
        }
    }
}