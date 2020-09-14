package com.place.www.ui.launcher.fragments

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.place.www.R

class RegisterFragment: Fragment(R.layout.fragment_register){

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val act = requireActivity() as AppCompatActivity
        //Show ActionBar
        act.supportActionBar?.show()
    }
}