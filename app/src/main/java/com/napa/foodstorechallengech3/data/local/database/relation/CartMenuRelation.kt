package com.napa.foodstorechallengech3.data.local.database.relation

import androidx.room.Embedded
import androidx.room.Relation
import com.napa.foodstorechallengech3.data.local.database.entity.CartEntity
import com.napa.foodstorechallengech3.data.local.database.entity.MenuEntity

data class CartMenuRelation(
    @Embedded
    val cart: CartEntity,
    @Relation(parentColumn = "menu_id", entityColumn = "id")
    val menu: MenuEntity
)