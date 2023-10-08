package com.napa.foodstorechallengech3.presentation.feature.main

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.navigation.Navigation.findNavController
import androidx.navigation.findNavController
import com.napa.foodstorechallengech3.R
import androidx.navigation.ui.setupWithNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.napa.foodstorechallengech3.data.local.datastore.appDataStore
import com.napa.foodstorechallengech3.databinding.ActivityMainBinding
import com.napa.foodstorechallengech3.presentation.feature.home.HomeViewModel
import com.napa.foodstorechallengech3.utils.GenericViewModelFactory


class MainActivity : AppCompatActivity() {
    private val binding: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setupBottomNav()
    }

    private fun setupBottomNav() {
        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        binding.navView.setupWithNavController(navController)
    }


}