package com.place.www.ui

import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast

fun Context.showToast(msg: String){
    Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
}

fun Context.hideKeyBoard(view: View){
    val inputMethodManager = getSystemService(
        Context.INPUT_METHOD_SERVICE) as InputMethodManager
    inputMethodManager
        .hideSoftInputFromWindow(view.windowToken, 0)
}