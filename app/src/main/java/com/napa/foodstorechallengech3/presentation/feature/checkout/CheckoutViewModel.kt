package com.napa.foodstorechallengech3.presentation.feature.checkout

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.napa.foodstorechallengech3.data.repository.CartRepository
import kotlinx.coroutines.Dispatchers

class CheckoutViewModel(private val cartRepository: CartRepository) : ViewModel() {
    val cartList = cartRepository.getUserCartData().asLiveData(Dispatchers.IO)

}