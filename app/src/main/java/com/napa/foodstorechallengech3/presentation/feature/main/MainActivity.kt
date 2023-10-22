package com.napa.foodstorechallengech3.presentation.feature.main

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.navigation.Navigation.findNavController
import androidx.navigation.findNavController
import com.napa.foodstorechallengech3.R
import androidx.navigation.ui.setupWithNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.gson.Gson
import com.napa.foodstorechallengech3.data.dummy.DummyCategoriesDataSourceImpl
import com.napa.foodstorechallengech3.data.dummy.DummyMenuDataSourceImpl
import com.napa.foodstorechallengech3.data.local.datastore.appDataStore
import com.napa.foodstorechallengech3.data.network.firebase.auth.FirebaseAuthDataSourceImpl
import com.napa.foodstorechallengech3.data.repository.UserRepositoryImpl
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
        val json = Gson().toJson(DummyMenuDataSourceImpl().getMenuList())
        val jsonca = Gson().toJson(DummyCategoriesDataSourceImpl().getMenuCategories())
        Log.d("Main", json)
    }




    private fun setupBottomNav() {
        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        binding.navView.setupWithNavController(navController)
    }


}