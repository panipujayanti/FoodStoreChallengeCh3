package com.napa.foodstorechallengech3.data.local.database.mapper

import com.napa.foodstorechallengech3.data.local.database.entity.MenuEntity
import com.napa.foodstorechallengech3.model.Menu

fun MenuEntity?.toMenu() = Menu(
    id = this?.id ?: 0,
    name = this?.name.orEmpty(),
    price = this?.price ?: 0.0,
    desc = this?.desc.orEmpty(),
    menuImgUrl = this?.menuImgUrl.orEmpty(),
    location = this?.location.orEmpty()
)

fun Menu?.toMenuEntity() = MenuEntity(
    id = this?.id,
    name = this?.name.orEmpty(),
    price = this?.price ?: 0.0,
    desc = this?.desc.orEmpty(),
    menuImgUrl = this?.menuImgUrl.orEmpty(),
    location = this?.location.orEmpty()
)

fun List<MenuEntity?>.toMenuList() = this.map { it.toMenu() }
fun List<Menu?>.toMenuEntity() = this.map { it.toMenuEntity() }
