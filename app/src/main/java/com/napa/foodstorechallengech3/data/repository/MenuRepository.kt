package com.napa.foodstorechallengech3.data.repository

import com.napa.foodstorechallengech3.data.dummy.DummyCategoriesDataSource
import com.napa.foodstorechallengech3.data.local.database.datasource.MenuDataSource
import com.napa.foodstorechallengech3.data.local.database.mapper.toMenuList
import com.napa.foodstorechallengech3.model.Categories
import com.napa.foodstorechallengech3.model.Menu
import com.napa.foodstorechallengech3.utils.ResultWrapper
import com.napa.foodstorechallengech3.utils.proceed
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart

interface MenuRepository {
    fun getCategories(): List<Categories>
    fun getProducts(): Flow<ResultWrapper<List<Menu>>>
}

class MenuRepositoryImpl(
    private val menuDataSource: MenuDataSource,
    private val dummyCategoriesDataSource: DummyCategoriesDataSource
) : MenuRepository {

    override fun getCategories(): List<Categories> {
        return dummyCategoriesDataSource.getMenuCategories()
    }

    override fun getProducts(): Flow<ResultWrapper<List<Menu>>> {
        return menuDataSource.getAllMenus().map {
            proceed { it.toMenuList() }
        }.onStart {
            emit(ResultWrapper.Loading())
            delay(2000)
        }
    }
}