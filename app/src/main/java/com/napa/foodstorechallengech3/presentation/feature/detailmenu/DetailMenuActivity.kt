package com.napa.foodstorechallengech3.presentation.feature.detailmenu

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import coil.load
import com.chuckerteam.chucker.api.ChuckerInterceptor
import com.napa.foodstorechallengech3.R
import com.napa.foodstorechallengech3.data.local.database.AppDatabase
import com.napa.foodstorechallengech3.data.local.database.datasource.CartDataSource
import com.napa.foodstorechallengech3.data.local.database.datasource.CartDatabaseDataSource
import com.napa.foodstorechallengech3.data.network.api.datasource.FoodStoreApiDataSource
import com.napa.foodstorechallengech3.data.network.api.service.FoodStoreApiService
import com.napa.foodstorechallengech3.data.repository.CartRepository
import com.napa.foodstorechallengech3.data.repository.CartRepositoryImpl
import com.napa.foodstorechallengech3.databinding.ActivityDetailMenuBinding
import com.napa.foodstorechallengech3.model.Menu
import com.napa.foodstorechallengech3.utils.GenericViewModelFactory
import com.napa.foodstorechallengech3.utils.proceedWhen
import com.napa.foodstorechallengech3.utils.toCurrencyFormat

class DetailMenuActivity : AppCompatActivity() {

    private val binding: ActivityDetailMenuBinding by lazy {
        ActivityDetailMenuBinding.inflate(layoutInflater)
    }

    private val viewModel: DetailMenuViewModel by viewModels {
        val database = AppDatabase.getInstance(this)
        val cartDao = database.cartDao()
        val cartDataSource: CartDataSource = CartDatabaseDataSource(cartDao)
        val chuckerInterceptor = ChuckerInterceptor(this.applicationContext)
        val service = FoodStoreApiService.invoke(chuckerInterceptor)
        val apiDataSource = FoodStoreApiDataSource(service)
        val repo: CartRepository = CartRepositoryImpl(cartDataSource, apiDataSource)
        GenericViewModelFactory.create(
            DetailMenuViewModel(intent?.extras, repo)
        )
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        bindMenu(viewModel.menu)
        observeData()
        setClickListener()

    }

    private fun setClickListener() {
        binding.backBtn.setOnClickListener {
            onBackPressed()
        }
        binding.ivMinus.setOnClickListener{
            viewModel.minus()
        }
        binding.ivPlus.setOnClickListener{
            viewModel.add()
        }
        binding.tvLocation.setOnClickListener {
            viewModel.onLocationClicked()
        }
        binding.btnAddToCart.setOnClickListener {
            viewModel.addToCart()
        }
    }

    private fun observeData() {
        viewModel.priceLiveData.observe(this){
            binding.tvCalculatedProductPrice.text = it.toCurrencyFormat()
        }
        viewModel.menuCountLiveData.observe(this){
            binding.tvProductCount.text = it.toString()
        }
        viewModel.addToCartResult.observe(this) {
            it.proceedWhen(
                doOnSuccess = {
                    Toast.makeText(this, "Berhasil Tambahkan Ke Keranjang !", Toast.LENGTH_SHORT).show()
                    finish()
                }, doOnError = {
                    Toast.makeText(this, it.exception?.message.orEmpty(), Toast.LENGTH_SHORT).show()
                })
        }
        viewModel.navigateToMapsLiveData.observe(this) { location ->
            location?.let {
                navigateToMaps(location)
            }
        }
    }

    private fun bindMenu(menu: Menu?) {
        menu?.let { item ->
            binding.ivMenu.load(item.menuImgUrl) {
                crossfade(true)
            }
            binding.tvMenuName.text = item.name
            binding.tvDesc.text = item.desc
            binding.tvPriceMenu.text = item.price.toCurrencyFormat()
            binding.tvLocation.text = item.location
        }
    }

    private fun navigateToMaps(location: String) {
        val gmmIntentUri = Uri.parse("http://maps.google.com/?q=$location")
        val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
//        mapIntent.setPackage("com.google.android.apps.maps")
        startActivity(mapIntent)
    }

    companion object {
        const val EXTRA_PRODUCT = "EXTRA_PRODUCT"
        fun startActivity(context: Context, product: Menu) {
            val intent = Intent(context, DetailMenuActivity::class.java)
            intent.putExtra(EXTRA_PRODUCT, product)
            context.startActivity(intent)
        }
    }
}