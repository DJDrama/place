package com.place.www.ui.main.fragments

import android.location.Location
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MapFragmentViewModel: ViewModel(){
    private val _location: MutableLiveData<Location> = MutableLiveData()
    val location: MutableLiveData<Location>
        get() = _location

    init{
        _location.value=null
    }
    fun setMyLocation(it: Location) {
        _location.value = it
    }

}