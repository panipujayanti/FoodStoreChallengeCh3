package com.napa.foodstorechallengech3.presentation.splashscreen

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.google.firebase.auth.FirebaseAuth
import com.napa.foodstorechallengech3.R
import com.napa.foodstorechallengech3.data.network.firebase.auth.FirebaseAuthDataSource
import com.napa.foodstorechallengech3.data.network.firebase.auth.FirebaseAuthDataSourceImpl
import com.napa.foodstorechallengech3.data.repository.UserRepository
import com.napa.foodstorechallengech3.data.repository.UserRepositoryImpl
import com.napa.foodstorechallengech3.databinding.ActivitySplashBinding
import com.napa.foodstorechallengech3.presentation.feature.login.LoginActivity
import com.napa.foodstorechallengech3.presentation.feature.main.MainActivity
import com.napa.foodstorechallengech3.utils.GenericViewModelFactory
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SplashActivity : AppCompatActivity() {

    private val viewModel: SplashViewModel by viewModels {
        GenericViewModelFactory.create(createViewModel())
    }

    private fun createViewModel(): SplashViewModel {
        val firebaseAuth = FirebaseAuth.getInstance()
        val dataSource: FirebaseAuthDataSource = FirebaseAuthDataSourceImpl(firebaseAuth)
        val repository: UserRepository = UserRepositoryImpl(dataSource)
        return SplashViewModel(repository)
    }

    private val binding: ActivitySplashBinding by lazy {
        ActivitySplashBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        checkIfUserLogin()
    }

    private fun checkIfUserLogin() {
        lifecycleScope.launch {
            delay(2000)
            if (viewModel.isUserLoggedIn()) {
                navigateToMain()
            } else {
                navigateToLogin()
            }
        }
    }

    private fun navigateToLogin() {
        val intent = Intent(this, LoginActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        }
        startActivity(intent)
    }

    private fun navigateToMain() {
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        }
        startActivity(intent)
    }


}