package com.napa.foodstorechallengech3.presentation.feature.cart

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import com.napa.foodstorechallengech3.R
import com.napa.foodstorechallengech3.data.local.database.AppDatabase
import com.napa.foodstorechallengech3.data.local.database.datasource.CartDataSource
import com.napa.foodstorechallengech3.data.local.database.datasource.CartDatabaseDataSource
import com.napa.foodstorechallengech3.data.repository.CartRepository
import com.napa.foodstorechallengech3.data.repository.CartRepositoryImpl
import com.napa.foodstorechallengech3.databinding.FragmentCartBinding
import com.napa.foodstorechallengech3.model.Cart
import com.napa.foodstorechallengech3.model.CartMenu
import com.napa.foodstorechallengech3.presentation.common.adapter.CartListAdapter
import com.napa.foodstorechallengech3.presentation.common.adapter.CartListener
import com.napa.foodstorechallengech3.presentation.feature.checkout.CheckoutActivity
import com.napa.foodstorechallengech3.utils.GenericViewModelFactory
import com.napa.foodstorechallengech3.utils.hideKeyboard
import com.napa.foodstorechallengech3.utils.proceedWhen
import com.napa.foodstorechallengech3.utils.toCurrencyFormat

class CartFragment : Fragment() {

    private lateinit var binding: FragmentCartBinding

    private val viewModel: CartViewModel by viewModels {
        val database = AppDatabase.getInstance(requireContext())
        val cartDao = database.cartDao()
        val cartDataSource: CartDataSource = CartDatabaseDataSource(cartDao)
        val repo: CartRepository = CartRepositoryImpl(cartDataSource)
        GenericViewModelFactory.create(CartViewModel(repo))
    }

    private val adapter: CartListAdapter by lazy {
        CartListAdapter(object : CartListener {
            override fun onPlusTotalItemCartClicked(cart: Cart) {
                viewModel.increaseCart(cart)
            }

            override fun onMinusTotalItemCartClicked(cart: Cart) {
                viewModel.decreaseCart(cart)
            }

            override fun onRemoveCartClicked(cart: Cart) {
                viewModel.removeCart(cart)
            }

            override fun onUserDoneEditingNotes(cart: Cart) {
                viewModel.setCartNotes(cart)
                hideKeyboard()
            }
        })
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCartBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupList()
        observeData()
        setClickListener()
    }

    private fun setClickListener() {
        binding.btnCheckout.setOnClickListener {
            context?.startActivity(Intent(requireContext(), CheckoutActivity::class.java))
        }
    }

    private fun setupList() {
        binding.rvCart.itemAnimator = null
        binding.rvCart.adapter = adapter
    }

    private fun observeData()  {
        viewModel.cartList.observe(viewLifecycleOwner) { result ->
            result.proceedWhen(
                doOnSuccess = {
                    binding.rvCart.isVisible = true
                    binding.layoutState.root.isVisible= false
                    binding.layoutState.pbLoading.isVisible = false
                    binding.layoutState.tvError.isVisible = false
                    result.payload?.let{ (carts, totalPrice) ->
                        adapter.submitData(carts)
                        binding.tvTotalPrice.text = totalPrice.toCurrencyFormat()
                    }
                },
                doOnError = { err ->
                    binding.layoutState.root.isVisible = true
                    binding.layoutState.tvError.isVisible = true
                    binding.layoutState.tvError.text = err.exception?.message.orEmpty()
                    binding.layoutState.pbLoading.isVisible = false
                },
                doOnLoading = {
                    binding.layoutState.root.isVisible = true
                    binding.layoutState.tvError.isVisible = false
                    binding.layoutState.pbLoading.isVisible = true
                    binding.rvCart.isVisible = false
                },
                doOnEmpty = { data ->
                    binding.layoutState.root.isVisible = true
                    binding.layoutState.pbLoading.isVisible = false
                    binding.layoutState.tvError.isVisible = true
                    binding.layoutState.tvError.text = getString(R.string.text_cart_is_empty)
                    data.payload?.let { (_, totalPrice) ->
                        binding.tvTotalPrice.text = totalPrice.toCurrencyFormat()
                    }
                    binding.rvCart.isVisible = false
                })
        }
    }
}