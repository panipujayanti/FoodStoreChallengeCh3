package com.napa.foodstorechallengech3.presentation.splashscreen

import androidx.lifecycle.ViewModel
import com.napa.foodstorechallengech3.data.repository.UserRepository

class SplashViewModel(private val repository: UserRepository) : ViewModel() {

    fun isUserLoggedIn() = repository.isLoggedIn()
}
