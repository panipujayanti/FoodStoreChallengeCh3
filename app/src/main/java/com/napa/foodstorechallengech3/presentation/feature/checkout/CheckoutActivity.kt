package com.napa.foodstorechallengech3.presentation.feature.checkout

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import com.chuckerteam.chucker.api.ChuckerInterceptor
import com.google.firebase.auth.FirebaseAuth
import com.napa.foodstorechallengech3.R
import com.napa.foodstorechallengech3.data.local.database.AppDatabase
import com.napa.foodstorechallengech3.data.local.database.datasource.CartDataSource
import com.napa.foodstorechallengech3.data.local.database.datasource.CartDatabaseDataSource
import com.napa.foodstorechallengech3.data.network.api.datasource.FoodStoreApiDataSource
import com.napa.foodstorechallengech3.data.network.api.service.FoodStoreApiService
import com.napa.foodstorechallengech3.data.network.firebase.auth.FirebaseAuthDataSourceImpl
import com.napa.foodstorechallengech3.data.repository.CartRepository
import com.napa.foodstorechallengech3.data.repository.CartRepositoryImpl
import com.napa.foodstorechallengech3.data.repository.UserRepository
import com.napa.foodstorechallengech3.data.repository.UserRepositoryImpl
import com.napa.foodstorechallengech3.databinding.ActivityCheckoutBinding
import com.napa.foodstorechallengech3.presentation.common.adapter.CartListAdapter
import com.napa.foodstorechallengech3.utils.GenericViewModelFactory
import com.napa.foodstorechallengech3.utils.proceedWhen
import com.napa.foodstorechallengech3.utils.toCurrencyFormat

class CheckoutActivity : AppCompatActivity() {

    private val viewModel: CheckoutViewModel by viewModels {
        val database = AppDatabase.getInstance(this)
        val cartDao = database.cartDao()
        val cartDataSource: CartDataSource = CartDatabaseDataSource(cartDao)
        val chuckerInterceptor = ChuckerInterceptor(this.applicationContext)
        val service = FoodStoreApiService.invoke(chuckerInterceptor)
        val apiDataSource = FoodStoreApiDataSource(service)
        val firebaseAuth = FirebaseAuth.getInstance()
        val firebaseDataSource = FirebaseAuthDataSourceImpl(firebaseAuth)
        val cartRepo: CartRepository = CartRepositoryImpl(cartDataSource, apiDataSource)
        val userRepo : UserRepository = UserRepositoryImpl(firebaseDataSource)
        GenericViewModelFactory.create(CheckoutViewModel(cartRepo, userRepo))
    }

    private val binding : ActivityCheckoutBinding by lazy {
        ActivityCheckoutBinding.inflate(layoutInflater)
    }

    private val adapter : CartListAdapter by lazy {
        CartListAdapter()
    }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setupList()
        observeData()
        setOnClickListener()

    }

    private fun setOnClickListener() {
        binding.ivBack.setOnClickListener {
            onBackPressed()
        }
        binding.btnCheckout.setOnClickListener {
            viewModel.createOrder()
        }
    }


    private fun setupList() {
        binding.layoutContent.rvCart.adapter = adapter
    }

    private fun observeData(){
        observeCartData()
        observeCheckoutResult()
    }
    private fun observeCartData() {
        viewModel.cartList.observe(this) {
            it.proceedWhen(doOnSuccess = { result ->
                binding.layoutState.root.isVisible = false
                binding.layoutState.pbLoading.isVisible = false
                binding.layoutState.tvError.isVisible = false
                binding.layoutContent.root.isVisible = true
                binding.layoutContent.rvCart.isVisible = true
                binding.cvSectionOrder.isVisible = true
                result.payload?.let { (carts, totalPrice) ->
                    adapter.submitData(carts)
                    binding.layoutContent.tvTotalPrice.text = totalPrice.toCurrencyFormat()
                }
            }, doOnLoading = {
                binding.layoutState.root.isVisible = true
                binding.layoutState.pbLoading.isVisible = true
                binding.layoutState.tvError.isVisible = false
                binding.layoutContent.root.isVisible = false
                binding.layoutContent.rvCart.isVisible = false
                binding.cvSectionOrder.isVisible = false
            }, doOnError = { err ->
                binding.layoutState.root.isVisible = true
                binding.layoutState.pbLoading.isVisible = false
                binding.layoutState.tvError.isVisible = true
                binding.layoutState.tvError.text = err.exception?.message.orEmpty()
                binding.layoutContent.root.isVisible = false
                binding.layoutContent.rvCart.isVisible = false
                binding.cvSectionOrder.isVisible = false
                Toast.makeText(this, it.exception?.message.orEmpty(), Toast.LENGTH_SHORT).show()
            }, doOnEmpty = { data ->
                binding.layoutState.root.isVisible = true
                binding.layoutState.pbLoading.isVisible = false
                binding.layoutState.tvError.isVisible = true
                binding.layoutState.tvError.text = getString(R.string.text_cart_is_empty)
                data.payload?.let { (_, totalPrice) ->
                    binding.layoutContent.tvTotalPrice.text = totalPrice.toCurrencyFormat()
                }
                binding.layoutContent.root.isVisible = false
                binding.layoutContent.rvCart.isVisible = false
                binding.cvSectionOrder.isVisible = false
            })
        }
    }
    private fun observeCheckoutResult() {
        viewModel.checkoutResult.observe(this) {
            it.proceedWhen(
                doOnSuccess = {
                    binding.layoutState.root.isVisible = false
                    binding.layoutState.pbLoading.isVisible = false
                    showDialogCheckoutSuccess()
                },
                doOnError = {
                    binding.layoutState.root.isVisible = false
                    binding.layoutState.pbLoading.isVisible = false
                    Toast.makeText(this, "Checkout Error", Toast.LENGTH_SHORT).show()
                },
                doOnLoading = {
                    binding.layoutState.root.isVisible = true
                    binding.layoutState.pbLoading.isVisible = true
                }
            )
        }
    }
    private fun showDialogCheckoutSuccess() {
        AlertDialog.Builder(this)
            .setTitle("Pesanan Berhasil")
            .setMessage("Terima kasih! Pesanan Anda Sedang Kami Proses.")
            .setPositiveButton(getString(R.string.text_ok)) { _, _ ->
                viewModel.clearCart()
                finish()
            }.create().show()
    }

}