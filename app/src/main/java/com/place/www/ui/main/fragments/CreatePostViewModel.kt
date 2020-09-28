package com.place.www.ui.main.fragments

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class CreatePostViewModel : ViewModel() {

    private val _resultUri = MutableLiveData<Uri>()
    val resultUri: LiveData<Uri>
        get() = _resultUri


    fun setResultUri(uri: Uri) {
        _resultUri.value = uri
    }

    fun getResultUri() = resultUri.value


}