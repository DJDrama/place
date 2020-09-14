package com.place.www.ui.launcher.fragments

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.place.www.R
import kotlinx.android.synthetic.main.fragment_login.*

class LoginFragment: Fragment(R.layout.fragment_login), View.OnClickListener {


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //getting activity context as AppcompatActivity in order to access
        //supportActionBar
        val act = requireActivity() as AppCompatActivity
        //Hide ActionBar
        act.supportActionBar?.hide()
        //Set show hide animation to false
        act.supportActionBar?.setShowHideAnimationEnabled(false)

        button_register.setOnClickListener(this)
    }

    override fun onClick(p0: View?) {
        p0?.let{
            when(it.id){
                R.id.button_register->{
                    navigateToRegisterFragment(it)
                }
            }
        }
    }
    private fun navigateToRegisterFragment(view: View){
        view.findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
    }
}