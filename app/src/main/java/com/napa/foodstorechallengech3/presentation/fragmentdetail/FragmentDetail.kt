package com.napa.foodstorechallengech3.presentation.fragmentdetail

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import coil.load
import com.napa.foodstorechallengech3.R
import com.napa.foodstorechallengech3.model.Menu
import com.napa.foodstorechallengech3.databinding.FragmentDetailBinding
import androidx.navigation.fragment.findNavController

class FragmentDetail : Fragment() {


    private fun Double.formatCurrency(currencySymbol: String): String {
        val formattedAmount = String.format("%, .0f",this).replace(",",".")
        return "$currencySymbol $formattedAmount"
    }

    private lateinit var binding : FragmentDetailBinding

    private val menu : Menu? by lazy {
        FragmentDetailArgs.fromBundle(arguments as Bundle).menu
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setClickListener()
        showDetailData()
        calculateProductTotalPrice()
    }

    private fun setClickListener() {
        binding.llLocation.setOnClickListener {
            navigateToMaps()
        }

        binding.backBtn.setOnClickListener{
            findNavController().popBackStack()
        }
    }

    private fun navigateToMaps() {
        binding.llLocation.setOnClickListener {
            val location = menu?.location

            val gmmIntentUri = Uri.parse("geo:0,0?q=" + Uri.encode(location))
            val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
            mapIntent.setPackage("com.google.android.apps.maps")

            if (mapIntent.resolveActivity(requireContext().packageManager) == null) {
                startActivity(mapIntent)
            } else {
                Toast.makeText(requireContext(), "Gagal", Toast.LENGTH_SHORT).show()
            }
        }
    }
    private fun showDetailData() {
        menu?.let { a ->
            binding.apply {
                ivMenu.load(a.img){
                    crossfade(true)
                }
                tvMenuName.text = a.name
                tvPriceMenu.text= a.price.formatCurrency("Rp. ")
                tvDesc.apply{
                    text = a.detail
                    movementMethod = ScrollingMovementMethod()
                }
                tvLocation.text = a.location
                btnAdd.text =
                    getString(R.string.text_product_price_calculate, a.price.toInt())
            }
        }
    }
    private fun calculateProductTotalPrice(){
        var totalProduct : Int = 1
        var totalPrice : Double
        val minusImage = binding.ivMinus
        val plusImage = binding.ivPlus
        val textTotalProduct = binding.tvProductCalculate
        val textTotalPrice = binding.btnAdd
        plusImage.setOnClickListener{
            totalProduct += 1
            totalPrice = (totalProduct * (menu?.price?.toInt() ?: 0)).toDouble()
            textTotalProduct.text = totalProduct.toString()
            textTotalPrice.text = getString(R.string.text_product_price_calculate, totalPrice.toInt())
        }
        minusImage.setOnClickListener{
            if (totalProduct <= 1){
                totalProduct = 1
            } else {
                totalProduct -= 1
                totalPrice = (totalProduct * (menu?.price?.toInt() ?: 0)).toDouble()
                textTotalProduct.text = totalProduct.toString()
                textTotalPrice.text = getString(R.string.text_product_price_calculate, totalPrice.toInt())
            }
        }
    }

}