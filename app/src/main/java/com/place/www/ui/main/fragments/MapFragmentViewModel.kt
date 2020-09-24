package com.place.www.ui.main.fragments

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.place.www.model.LocationItem

class MapFragmentViewModel: ViewModel(){
    private val _locationItem: MutableLiveData<LocationItem> = MutableLiveData()
    val locationItem: MutableLiveData<LocationItem>
        get() = _locationItem

    private val _infoWindowClicked = MutableLiveData<Boolean>()
    val infoWindowClicked: MutableLiveData<Boolean>
    get() = _infoWindowClicked

    init{
        _locationItem.value=null
    }
    fun setCurrentLocation(it: LocationItem) {
        _locationItem.value = it
    }
    fun getLocationItem() = locationItem.value

    fun setInfoWindowClicked(bool: Boolean){
        _infoWindowClicked.value = bool
    }
}