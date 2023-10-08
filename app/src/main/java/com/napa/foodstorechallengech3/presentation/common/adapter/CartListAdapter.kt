package com.napa.foodstorechallengech3.presentation.common.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.napa.foodstorechallengech3.core.ViewHolderBinder
import com.napa.foodstorechallengech3.databinding.ItemCartMenuBinding
import com.napa.foodstorechallengech3.model.Cart
import com.napa.foodstorechallengech3.model.CartMenu
import com.napa.foodstorechallengech3.utils.doneEditing
import com.napa.foodstorechallengech3.utils.toCurrencyFormat

class CartListAdapter(private val cartListener: CartListener? = null) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val dataDiffer =
        AsyncListDiffer(this, object : DiffUtil.ItemCallback<CartMenu>() {
            override fun areItemsTheSame(
                oldItem: CartMenu,
                newItem: CartMenu
            ): Boolean {
                return oldItem.cart.id == newItem.cart.id && oldItem.menu.id == newItem.menu.id
            }

            override fun areContentsTheSame(
                oldItem: CartMenu,
                newItem: CartMenu
            ): Boolean {
                return oldItem.hashCode() == newItem.hashCode()
            }
        })

    fun submitData(data: List<CartMenu>) {
        dataDiffer.submitList(data)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        TODO("Not yet implemented")
    }

    override fun getItemCount(): Int = dataDiffer.currentList.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as ViewHolderBinder<CartMenu>).bind(dataDiffer.currentList[position])
    }

}

class CartViewHolder(
    private val binding: ItemCartMenuBinding,
    private val cartListener: CartListener?
) : RecyclerView.ViewHolder(binding.root), ViewHolderBinder<CartMenu> {
    override fun bind(item: CartMenu) {
        setCartData(item)
        setCartNotes(item)
        setClickListeners(item)
    }

    private fun setCartData(item: CartMenu) {
        with(binding) {
            binding.ivMenuImage.load(item.menu.menuImgUrl) {
                crossfade(true)
            }
            tvProductCalculate.text = item.cart.itemQuantity.toString()
            tvMenuName.text = item.menu.name
            tvPriceMenu.text = (item.cart.itemQuantity * item.menu.price).toCurrencyFormat()
        }
    }

    private fun setCartNotes(item: CartMenu) {
        binding.etNotesItem.setText(item.cart.itemNotes)
        binding.etNotesItem.doneEditing {
            binding.etNotesItem.clearFocus()
            val newItem = item.cart.copy().apply {
                itemNotes = binding.etNotesItem.text.toString().trim()
            }
            cartListener?.onUserDoneEditingNotes(newItem)
        }
    }

    private fun setClickListeners(item: CartMenu) {
        with(binding) {
            ivMinus.setOnClickListener { cartListener?.onMinusTotalItemCartClicked(item.cart) }
            ivPlus.setOnClickListener { cartListener?.onPlusTotalItemCartClicked(item.cart) }
            ivDeleteMenu.setOnClickListener { cartListener?.onRemoveCartClicked(item.cart) }
        }
    }
}




interface CartListener {
    fun onPlusTotalItemCartClicked(cart: Cart)
    fun onMinusTotalItemCartClicked(cart: Cart)
    fun onRemoveCartClicked(cart: Cart)
    fun onUserDoneEditingNotes(cart: Cart)
}