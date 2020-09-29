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
) : Parcelable {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as PlaceItem

        if (id != other.id) return false
        if (name != other.name) return false
        if (address != other.address) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + name.hashCode()
        result = 31 * result + address.hashCode()
        return result
    }
}

