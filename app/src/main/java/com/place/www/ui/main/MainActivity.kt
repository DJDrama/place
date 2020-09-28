package com.place.www.ui.main

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupActionBarWithNavController
import com.place.www.R

class MainActivity : AppCompatActivity() {
    private val navController by lazy{
        (supportFragmentManager.findFragmentById(R.id.fragmentContainerView)
                as NavHostFragment).navController
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setupActionBarWithNavController(navController = navController)
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp()
    }
}