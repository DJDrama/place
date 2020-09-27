package com.place.www.ui.main.fragments

import com.google.android.libraries.maps.GoogleMap
import com.place.www.model.LocationItem

data class GoogleMapSettings(
    val isMapReady: Boolean,
    val location: LocationItem?,
    val googleMap: GoogleMap?
)