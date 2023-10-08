package com.napa.foodstorechallengech3.data.local.database.datasource

import com.napa.foodstorechallengech3.data.local.database.dao.MenuDao
import com.napa.foodstorechallengech3.data.local.database.entity.MenuEntity
import kotlinx.coroutines.flow.Flow

interface MenuDataSource {
    fun getAllMenus(): Flow<List<MenuEntity>>
    fun getMenuById(id: Int): Flow<MenuEntity>
    suspend fun insertMenus(menu: List<MenuEntity>)
    suspend fun deleteMenu(menu: MenuEntity): Int
    suspend fun updateMenu(menu: MenuEntity): Int
}

class MenuDatabaseDataSource(private val dao : MenuDao) : MenuDataSource {
    override fun getAllMenus(): Flow<List<MenuEntity>> {
        return dao.getAllMenus()
    }

    override fun getMenuById(id: Int): Flow<MenuEntity> {
        return dao.getMenuById(id)
    }

    override suspend fun insertMenus(menu: List<MenuEntity>) {
        return dao.insertMenu(menu)
    }

    override suspend fun deleteMenu(menu: MenuEntity): Int {
        return dao.deleteMenu(menu)
    }

    override suspend fun updateMenu(product: MenuEntity): Int {
        return dao.updateMenu(product)
    }

}