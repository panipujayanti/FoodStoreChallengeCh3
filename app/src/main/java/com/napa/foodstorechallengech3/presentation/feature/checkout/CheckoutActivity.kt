package com.napa.foodstorechallengech3.presentation.feature.checkout

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.view.isVisible
import com.napa.foodstorechallengech3.R
import com.napa.foodstorechallengech3.data.local.database.AppDatabase
import com.napa.foodstorechallengech3.data.local.database.datasource.CartDataSource
import com.napa.foodstorechallengech3.data.local.database.datasource.CartDatabaseDataSource
import com.napa.foodstorechallengech3.data.repository.CartRepository
import com.napa.foodstorechallengech3.data.repository.CartRepositoryImpl
import com.napa.foodstorechallengech3.databinding.ActivityCheckoutBinding
import com.napa.foodstorechallengech3.presentation.common.adapter.CartListAdapter
import com.napa.foodstorechallengech3.presentation.feature.dialogs.SuccessDialogFragment
import com.napa.foodstorechallengech3.utils.GenericViewModelFactory
import com.napa.foodstorechallengech3.utils.proceedWhen
import com.napa.foodstorechallengech3.utils.toCurrencyFormat

class CheckoutActivity : AppCompatActivity() {

    private val viewModel: CheckoutViewModel by viewModels {
        val database = AppDatabase.getInstance(this)
        val cartDao = database.cartDao()
        val cartDataSource: CartDataSource = CartDatabaseDataSource(cartDao)
        val repo: CartRepository = CartRepositoryImpl(cartDataSource)
        GenericViewModelFactory.create(CheckoutViewModel(repo))
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
            val successDialog = SuccessDialogFragment()
            successDialog.show(supportFragmentManager, "SuccessDialog")
        }
    }


    private fun setupList() {
        binding.layoutContent.rvCart.adapter = adapter
    }
    private fun observeData() {
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
                    binding.tvTotalPrice.text = totalPrice.toCurrencyFormat()
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
                    binding.tvTotalPrice.text = totalPrice.toCurrencyFormat()
                }
                binding.layoutContent.root.isVisible = false
                binding.layoutContent.rvCart.isVisible = false
                binding.cvSectionOrder.isVisible = false
            })
        }
    }

}