package com.place.www.ui.main.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.place.www.R
import kotlinx.android.synthetic.main.fragment_my_profile.*
import java.text.SimpleDateFormat
import java.util.*

class MyProfileFragment: Fragment(R.layout.fragment_my_profile){
    private lateinit var firebaseAuth: FirebaseAuth
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        firebaseAuth = FirebaseAuth.getInstance()

        tv_email_address.text = firebaseAuth.currentUser?.email
        tv_joined_date.text = SimpleDateFormat("yyyy/MM/dd hh:mm:ss a", Locale.KOREA)
            .format(Date(firebaseAuth.currentUser?.metadata?.creationTimestamp!!))

    }
}