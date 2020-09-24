package com.place.www.ui.main.fragments

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.place.www.model.LocationItem

class MapDetailFragmentViewModel : ViewModel() {
    private val _locationItem = MutableLiveData<LocationItem>()
    val locationItem: LiveData<LocationItem>
        get() = _locationItem

    fun setLocationItem(locationItem: LocationItem){
        _locationItem.value = locationItem
    }
}