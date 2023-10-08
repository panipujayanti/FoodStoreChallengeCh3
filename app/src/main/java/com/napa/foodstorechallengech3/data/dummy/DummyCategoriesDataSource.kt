package com.napa.foodstorechallengech3.data.dummy

import com.napa.foodstorechallengech3.model.Categories

interface DummyCategoriesDataSource{
    fun getMenuCategories(): List<Categories>
}

class DummyCategoriesDataSourceImpl() : DummyCategoriesDataSource {
    override fun getMenuCategories(): List<Categories> =
        listOf(
            Categories(
                name = "Snack",
                imgUrl = "https://raw.githubusercontent.com/panipujayanti/FoodStore_Challenge_BinarCH2/main/app/src/main/res/drawable/ic_snack.png"
            ),
            Categories(
                name = "Minuman",
                imgUrl = "https://raw.githubusercontent.com/panipujayanti/FoodStore_Challenge_BinarCH2/main/app/src/main/res/drawable/ic_drink.png"
            ),
            Categories(
                name = "Burger",
                imgUrl = "https://raw.githubusercontent.com/panipujayanti/FoodStore_Challenge_BinarCH2/main/app/src/main/res/drawable/ic_burger.png"
            ),
            Categories(
                name = "Dessert",
                imgUrl = "https://raw.githubusercontent.com/panipujayanti/FoodStore_Challenge_BinarCH2/main/app/src/main/res/drawable/ic_dessert.png"
            ),
            Categories(
                name = "Sushi",
                imgUrl = "https://raw.githubusercontent.com/panipujayanti/FoodStore_Challenge_BinarCH2/main/app/src/main/res/drawable/ic_sushi.png"
            ),
            Categories(
                name = "Chicken",
                imgUrl = "https://raw.githubusercontent.com/panipujayanti/FoodStore_Challenge_BinarCH2/main/app/src/main/res/drawable/ic_chicken.png"
            )
        )
}