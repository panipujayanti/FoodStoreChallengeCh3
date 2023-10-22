package com.napa.foodstorechallengech3.data.network.api.model.menu


import com.google.gson.annotations.SerializedName
import androidx.annotation.Keep
import com.napa.foodstorechallengech3.model.Menu

@Keep
data class MenuItemResponse(
    @SerializedName("image_url")
    val menuImgUrl: String?,
    @SerializedName("nama")
    val name: String?,
    @SerializedName("harga")
    val price: Int?,
    @SerializedName("harga_format")
    val priceFormat: String,
    @SerializedName("detail")
    val desc: String?,
    @SerializedName("alamat_resto")
    val location: String?

)

fun MenuItemResponse.toMenu() = Menu(
    name = this.name.orEmpty(),
    price = this.price ?: 0,
    priceFormat = this.priceFormat,
    desc = this.desc.orEmpty(),
    location = this.location.orEmpty(),
    menuImgUrl = this.menuImgUrl.orEmpty()
)

fun Collection<MenuItemResponse>.toMenuList() = this.map { it.toMenu() }