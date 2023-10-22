package com.napa.foodstorechallengech3.presentation.feature.profile

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import coil.load
import coil.transform.CircleCropTransformation
import com.google.firebase.auth.FirebaseAuth
import com.napa.foodstorechallengech3.R
import com.napa.foodstorechallengech3.data.network.firebase.auth.FirebaseAuthDataSource
import com.napa.foodstorechallengech3.data.network.firebase.auth.FirebaseAuthDataSourceImpl
import com.napa.foodstorechallengech3.data.repository.UserRepository
import com.napa.foodstorechallengech3.data.repository.UserRepositoryImpl
import com.napa.foodstorechallengech3.databinding.FragmentProfileBinding
import com.napa.foodstorechallengech3.presentation.feature.login.LoginActivity
import com.napa.foodstorechallengech3.presentation.feature.register.RegisterViewModel
import com.napa.foodstorechallengech3.utils.GenericViewModelFactory
import com.napa.foodstorechallengech3.utils.proceedWhen

class ProfileFragment : Fragment() {
    private val binding: FragmentProfileBinding by lazy {
        FragmentProfileBinding.inflate(layoutInflater)
    }

    private fun createViewModel(): ProfileViewModel {
        val firebaseAuth = FirebaseAuth.getInstance()
        val dataSource: FirebaseAuthDataSource = FirebaseAuthDataSourceImpl(firebaseAuth)
        val repository = UserRepositoryImpl(dataSource)
        return ProfileViewModel(repository)
    }

    private val viewModel: ProfileViewModel by viewModels {
        GenericViewModelFactory.create(createViewModel())
    }
    private val pickMedia =
        registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            if (uri != null) {
                changePhotoProfile(uri)
            }
        }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupForm()
        setClickListeners()
        showUserData()
        observeData()
    }

    private fun observeData() {
        viewModel.changePhotoResult.observe(viewLifecycleOwner){
            it.proceedWhen(
                doOnSuccess = {
                    Toast.makeText(requireContext(), "Change Photo Profile Success !", Toast.LENGTH_SHORT).show()
                    showUserData()
                },
                doOnError = {
                    Toast.makeText(requireContext(), "Change Photo Profile Failed !", Toast.LENGTH_SHORT).show()
                    showUserData()
                }
            )
        }
        viewModel.changeProfileResult.observe(viewLifecycleOwner){
            it.proceedWhen(
                doOnSuccess = {
                    binding.pbLoading.isVisible = false
                    binding.btnChangeProfile.isVisible = true
                    Toast.makeText(requireContext(), "Change Profile Data Success !", Toast.LENGTH_SHORT).show()
                },
                doOnError = {
                    binding.pbLoading.isVisible = false
                    binding.btnChangeProfile.isVisible = true
                    Toast.makeText(requireContext(), "Change Profile Data Failed !", Toast.LENGTH_SHORT).show()
                },
                doOnLoading = {
                    binding.pbLoading.isVisible = true
                    binding.btnChangeProfile.isVisible = false
                }
            )
        }
    }

    private fun showUserData() {
        viewModel.getCurrentUser()?.let {
            binding.layoutForm.etName.setText(it.fullName)
            binding.layoutForm.etEmail.setText(it.email)
            binding.ivProfilePict.load(it.photoUrl) {
                crossfade(true)
                placeholder(R.drawable.img_oppa)
                error(R.drawable.img_oppa)
                transformations(CircleCropTransformation())
            }
        }
    }


    private fun setClickListeners() {
        binding.btnChangeProfile.setOnClickListener {
            if (checkNameValidation()) {
                changeProfileData()
            }
        }
        binding.ivEditPhoto.setOnClickListener {
            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }
        binding.tvChangePwd.setOnClickListener {
            requestChangePassword()
        }
        binding.llLogout.setOnClickListener {
            doLogout()
        }
    }

    private fun setupForm() {
        binding.layoutForm.tilName.isVisible = true
        binding.layoutForm.tilEmail.isVisible = true
        binding.layoutForm.etEmail.isEnabled = false
    }

    private fun changeProfileData() {
        val fullName = binding.layoutForm.etName.text.toString().trim()
        viewModel.updateFullName(fullName)
    }

    private fun changePhotoProfile(uri: Uri) {
        viewModel.updateProfilePicture(uri)
    }

    private fun checkNameValidation(): Boolean {
        val fullName = binding.layoutForm.etName.text.toString().trim()
        return if (fullName.isEmpty()) {
            binding.layoutForm.tilName.isErrorEnabled = true
            binding.layoutForm.tilName.error = getString(R.string.text_error_name_cannot_empty)
            false
        } else {
            binding.layoutForm.tilName.isErrorEnabled = false
            true
        }
    }

    private fun requestChangePassword() {
        viewModel.createChangePwdRequest()
        val dialog = AlertDialog.Builder(requireContext())
            .setMessage("Change password request sended to your email : ${viewModel.getCurrentUser()?.email} Please check to your inbox or spam")
            .setPositiveButton(
                "Okay"
            ) { dialog, id ->

            }.create()
        dialog.show()
    }

    private fun doLogout() {
        val dialog = AlertDialog.Builder(requireContext()).setMessage("Do you want to logout ?")
            .setPositiveButton(
                "Yes"
            ) { dialog, id ->
                viewModel.doLogout()
                navigateToLogin()
            }
            .setNegativeButton(
                "No"
            ) { dialog, id ->
                //no-op , do nothing
            }.create()
        dialog.show()
    }

    private fun navigateToLogin() {
        context?.startActivity(Intent(requireContext(), LoginActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        })
    }

}