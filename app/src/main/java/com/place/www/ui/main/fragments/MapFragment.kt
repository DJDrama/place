package com.place.www.ui.main.fragments

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.place.www.R

class MapFragment: Fragment(R.layout.fragment_map){

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)

    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_profile, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_my_profile -> {
                navigateToMyProfile()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
    private fun navigateToMyProfile(){
        findNavController().navigate(R.id.action_mapFragment_to_myProfileFragment)
    }
}