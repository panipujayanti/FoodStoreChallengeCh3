package com.napa.foodstorechallengech3.presentation.feature.fragmenthome

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.napa.foodstorechallengech3.data.local.datastore.UserPreferenceDataSource
import com.napa.foodstorechallengech3.data.repository.MenuRepository
import com.napa.foodstorechallengech3.model.Categories
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class HomeViewModel(private val repo: MenuRepository,
                    private val userPreferenceDataSource: UserPreferenceDataSource
) : ViewModel() {

    fun setUsingGridPref(isUsingGrid: Boolean) {
        viewModelScope.launch {
            userPreferenceDataSource.setUsingGridPref(isUsingGrid)
        }
    }

    fun getCategoriesData(): List<Categories> {
        return repo.getCategories()
    }

    val usingGridLiveData = userPreferenceDataSource.isUsingGridPrefFlow().asLiveData(
        Dispatchers.IO)
    val productListLiveData = repo.getProducts().asLiveData(
        Dispatchers.IO)

}