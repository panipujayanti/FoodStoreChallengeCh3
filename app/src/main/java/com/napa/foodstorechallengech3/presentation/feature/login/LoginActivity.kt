package com.napa.foodstorechallengech3.presentation.feature.login

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.view.isVisible
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.napa.foodstorechallengech3.R
import com.napa.foodstorechallengech3.data.network.firebase.auth.FirebaseAuthDataSource
import com.napa.foodstorechallengech3.data.network.firebase.auth.FirebaseAuthDataSourceImpl
import com.napa.foodstorechallengech3.data.repository.UserRepository
import com.napa.foodstorechallengech3.data.repository.UserRepositoryImpl
import com.napa.foodstorechallengech3.databinding.ActivityLoginBinding
import com.napa.foodstorechallengech3.presentation.feature.main.MainActivity
import com.napa.foodstorechallengech3.presentation.feature.register.RegisterActivity
import com.napa.foodstorechallengech3.utils.GenericViewModelFactory
import com.napa.foodstorechallengech3.utils.highLightWord
import com.napa.foodstorechallengech3.utils.proceedWhen

class LoginActivity : AppCompatActivity() {

    private val binding: ActivityLoginBinding by lazy {
        ActivityLoginBinding.inflate(layoutInflater)
    }

    private val viewModel: LoginViewModel by viewModels {
        GenericViewModelFactory.create(createViewModel())
    }

    private fun createViewModel(): LoginViewModel {
        val firebaseAuth = FirebaseAuth.getInstance()
        val dataSource: FirebaseAuthDataSource = FirebaseAuthDataSourceImpl(firebaseAuth)
        val repository: UserRepository = UserRepositoryImpl(dataSource)
        return LoginViewModel(repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setupForm()
        setClickListeners()
        observeResult()
    }

    private fun setupForm() {
        binding.layoutForm.tilEmail.isVisible = true
        binding.layoutForm.tilPassword.isVisible = true
    }

    private fun observeResult() {
        viewModel.loginResult.observe(this) {
            it.proceedWhen (
                doOnSuccess = {
                    binding.pbLoading.isVisible = false
                    binding.btnLogin.isVisible = true
                    binding.btnLogin.isEnabled = false
                    navigateToMain()
                },
                doOnLoading = {
                    binding.pbLoading.isVisible = true
                    binding.btnLogin.isVisible = false
                },
                doOnError = {
                    binding.pbLoading.isVisible = false
                    binding.btnLogin.isVisible = true
                    binding.btnLogin.isEnabled = true
                    Toast.makeText(this,
                        "Login Failed ${it.exception?.message.orEmpty()}",
                        Toast.LENGTH_SHORT)
                        .show()
                }
            )
        }
    }

    private fun navigateToMain() {
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        }
        startActivity(intent)
    }

    private fun setClickListeners() {
        //todo :set click listener
        binding.tvNavToRegister.highLightWord(getString(R.string.text_highlight_register)) {
            navigateToRegister()
        }
        binding.btnLogin.setOnClickListener{
            doLogin()
        }
    }

    private fun navigateToRegister() {
        val intent = Intent(this, RegisterActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        startActivity(intent)
    }

    private fun doLogin() {
        if (isFormValid()) {
            val email = binding.layoutForm.etEmail.text.toString().trim()
            val password = binding.layoutForm.etPassword.text.toString().trim()
            viewModel.doLogin(email, password)
        }
    }

    private fun isFormValid(): Boolean {
        val email = binding.layoutForm.etEmail.text.toString().trim()
        val password = binding.layoutForm.etPassword.text.toString().trim()
        return checkEmailValidation(email)
                && checkPasswordValidation(password,binding.layoutForm.tilPassword)
    }

    private fun checkEmailValidation(email: String): Boolean {
        return if (email.isEmpty()) {
            //email cannot be empty
            binding.layoutForm.tilEmail.isErrorEnabled = true
            binding.layoutForm.tilEmail.error = getString(R.string.text_error_email_empty)
            false
        }else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            //format email is correct
            binding.layoutForm.tilEmail.isErrorEnabled = true
            binding.layoutForm.tilEmail.error = getString(R.string.text_error_email_invalid)
            false
        }else {
            binding.layoutForm.tilEmail.isErrorEnabled = false
            true
        }
    }


    private fun checkPasswordValidation(
        password: String,
        textInputLayout: TextInputLayout
    ): Boolean {
        return if (password.isEmpty()) {
            textInputLayout.isErrorEnabled = true
            textInputLayout.error = getString((R.string.text_error_password_empty))
            false
        } else if (password.length < 8) {
            textInputLayout.isErrorEnabled = true
            textInputLayout.error = getString((R.string.text_error_password_less_than_8_char))
            false
        } else {
            textInputLayout.isErrorEnabled = false
            true
        }
    }
}