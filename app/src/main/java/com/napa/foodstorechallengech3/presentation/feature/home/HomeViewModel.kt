package com.napa.foodstorechallengech3.presentation.feature.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.napa.foodstorechallengech3.R
import com.napa.foodstorechallengech3.data.local.datastore.UserPreferenceDataSource
import com.napa.foodstorechallengech3.data.repository.MenuRepository
import com.napa.foodstorechallengech3.model.Category
import com.napa.foodstorechallengech3.model.Menu
import com.napa.foodstorechallengech3.utils.AssetWrapper
import com.napa.foodstorechallengech3.utils.ResultWrapper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class HomeViewModel(
    private val repository: MenuRepository,
    private val userPreferenceDataSource: UserPreferenceDataSource,
    private val assetWrapper: AssetWrapper
) : ViewModel() {

    fun setUsingGridPref(isUsingGrid: Boolean) {
        viewModelScope.launch {
            userPreferenceDataSource.setUsingGridPref(isUsingGrid)
        }
    }

    private val _categories = MutableLiveData<ResultWrapper<List<Category>>>()
    val categories: LiveData<ResultWrapper<List<Category>>>
        get() = _categories

    private val _menus = MutableLiveData<ResultWrapper<List<Menu>>>()
    val menus: LiveData<ResultWrapper<List<Menu>>>
        get() = _menus

    fun getCategories() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.getCategories().collect {
                _categories.postValue(it)
            }
        }
    }

    fun getMenus(category: String? = null) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.getMenus(if (category == assetWrapper.getString(R.string.all)) null else category?.lowercase()).collect {
                _menus.postValue(it)
            }
        }
    }

    val usingGridLiveData = userPreferenceDataSource.isUsingGridPrefFlow().asLiveData(
        Dispatchers.IO
    )
    val menuListLiveData = repository.getMenus().asLiveData(
        Dispatchers.IO
    )
}
