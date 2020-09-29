package com.place.www.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class PlaceItem(
    val id: String="",
    val name: String="",
    val address: String="",
    val content: String="",
    val date:String="",
    val image:String="",
    val email: String="",
    val uid: String="",
    val lat: Double=0.0,
    val lon: Double=0.0,
    var documentId: String=""
) : Parcelable