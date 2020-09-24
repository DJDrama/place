package com.place.www.ui.main.fragments

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.libraries.maps.GoogleMap
import com.place.www.model.LocationItem

class MapFragmentViewModel : ViewModel() {
    private val _locationItem: MutableLiveData<LocationItem> = MutableLiveData()
    val locationItem: LiveData<LocationItem>
        get() = _locationItem

    private val _infoWindowClicked = MutableLiveData<Boolean>()
    val infoWindowClicked: LiveData<Boolean>
        get() = _infoWindowClicked

    /** TODO **/
    private val _googleMap = MutableLiveData<GoogleMap>()
    val googleMap: LiveData<GoogleMap>
        get() = _googleMap


    init {
        _locationItem.value = null
    }

    fun setCurrentLocation(it: LocationItem) {
        _locationItem.value = it
    }

    fun getLocationItem() = locationItem.value

    fun setInfoWindowClicked(bool: Boolean) {
        _infoWindowClicked.value = bool
    }
}