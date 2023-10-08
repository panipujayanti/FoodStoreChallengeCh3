package com.napa.foodstorechallengech3.presentation.feature.cart

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.napa.foodstorechallengech3.data.repository.CartRepository
import com.napa.foodstorechallengech3.model.Cart
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class CartViewModel(private val repo: CartRepository) : ViewModel() {

    val cartList = repo.getUserCartData().asLiveData(Dispatchers.IO)

    fun increaseCart(item: Cart) {
        viewModelScope.launch { repo.increaseCart(item).collect() }
    }

    fun decreaseCart(item: Cart) {
        viewModelScope.launch {repo.decreaseCart(item).collect()}
    }

    fun removeCart(item: Cart) {
        viewModelScope.launch { repo.deleteCart(item).collect() }
    }

    fun setCartNotes(item: Cart)
    {
        viewModelScope.launch { repo.setCartNotes(item).collect() }
    }


}