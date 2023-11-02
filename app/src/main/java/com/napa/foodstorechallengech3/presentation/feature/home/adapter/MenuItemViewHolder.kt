package com.napa.foodstorechallengech3.presentation.feature.home.adapter

import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.napa.foodstorechallengech3.core.ViewHolderBinder
import com.napa.foodstorechallengech3.databinding.ItemGridMenuBinding
import com.napa.foodstorechallengech3.databinding.ItemLinearMenuBinding
import com.napa.foodstorechallengech3.model.Menu
import com.napa.foodstorechallengech3.utils.toCurrencyFormat

private fun Double.formatCurrency(currencySymbol: String): String {
    val formattedAmount = String.format("%, .0f", this).replace(",", ".")
    return "$currencySymbol $formattedAmount"
}

class LinearMenuItemViewHolder(
    private val binding: ItemLinearMenuBinding,
    private val onClickListener: (Menu) -> Unit
) : RecyclerView.ViewHolder(binding.root), ViewHolderBinder<Menu> {
    override fun bind(item: Menu) {
        binding.ivMenu.load(item.menuImgUrl) {
            crossfade(true)
        }
        binding.tvMenuName.text = item.name
        binding.tvPriceMenu.text = item.price.toCurrencyFormat()
        binding.root.setOnClickListener {
            onClickListener.invoke(item)
        }
    }
}

class GridMenuItemViewHolder(
    private val binding: ItemGridMenuBinding,
    private val onClickListener: (Menu) -> Unit
) : RecyclerView.ViewHolder(binding.root), ViewHolderBinder<Menu> {
    override fun bind(item: Menu) {
        binding.ivMenu.load(item.menuImgUrl) {
            crossfade(true)
        }
        binding.tvMenuName.text = item.name
        binding.tvPriceMenu.text = item.price.toCurrencyFormat()
        binding.root.setOnClickListener {
            onClickListener.invoke(item)
        }
    }
}
