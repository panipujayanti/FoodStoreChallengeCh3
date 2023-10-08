package com.napa.foodstorechallengech3.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.UUID

@Parcelize
data class Menu(
    val id: Int? = null,
    val name: String,
    val price: Double,
    val menuImgUrl: String,
    val desc: String,
    val location: String,
) : Parcelable
