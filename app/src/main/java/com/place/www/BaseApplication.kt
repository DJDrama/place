package com.place.www

import android.app.Application
import com.google.android.libraries.places.api.Places

class BaseApplication: Application(){
    override fun onCreate() {
        super.onCreate()
        Places.initialize(applicationContext, getString(R.string.google_map_api_key))

        // Create a new PlacesClient instance
        //val placesClient = Places.createClient(this)
    }
}