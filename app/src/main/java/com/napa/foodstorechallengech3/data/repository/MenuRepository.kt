package com.napa.foodstorechallengech3.data.repository

import com.napa.foodstorechallengech3.data.network.api.datasource.FoodStoreDataSource
import com.napa.foodstorechallengech3.data.network.api.model.category.toCategoryList
import com.napa.foodstorechallengech3.data.network.api.model.menu.toMenuList
import com.napa.foodstorechallengech3.model.Category
import com.napa.foodstorechallengech3.model.Menu
import com.napa.foodstorechallengech3.utils.ResultWrapper
import com.napa.foodstorechallengech3.utils.proceedFlow
import kotlinx.coroutines.flow.Flow

interface MenuRepository {
    fun getCategories(): Flow<ResultWrapper<List<Category>>>
    fun getMenus(category: String? = null): Flow<ResultWrapper<List<Menu>>>
}

class MenuRepositoryImpl(
    private val apiDataSource: FoodStoreDataSource,
) : MenuRepository {

    override fun getCategories(): Flow<ResultWrapper<List<Category>>> {
        return proceedFlow {
            apiDataSource.getCategories().data?.toCategoryList() ?: emptyList()
        }
    }

    override fun getMenus(category: String?): Flow<ResultWrapper<List<Menu>>> {
        return proceedFlow {
            apiDataSource.getProducts(category).data?.toMenuList() ?: emptyList()
        }
    }
}