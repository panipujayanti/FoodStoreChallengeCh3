package com.napa.foodstorechallengech3.presentation.feature.home

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.chuckerteam.chucker.api.ChuckerInterceptor
import com.napa.foodstorechallengech3.data.dummy.DummyCategoriesDataSource
import com.napa.foodstorechallengech3.data.dummy.DummyCategoriesDataSourceImpl
import com.napa.foodstorechallengech3.data.local.database.AppDatabase
import com.napa.foodstorechallengech3.data.local.datastore.UserPreferenceDataSource
import com.napa.foodstorechallengech3.data.local.datastore.UserPreferenceDataSourceImpl
import com.napa.foodstorechallengech3.data.local.datastore.appDataStore
import com.napa.foodstorechallengech3.data.network.api.datasource.FoodStoreApiDataSource
import com.napa.foodstorechallengech3.data.network.api.service.FoodStoreApiService
import com.napa.foodstorechallengech3.data.repository.MenuRepository
import com.napa.foodstorechallengech3.data.repository.MenuRepositoryImpl
import com.napa.foodstorechallengech3.databinding.FragmentHomeBinding
import com.napa.foodstorechallengech3.model.Category
import com.napa.foodstorechallengech3.model.Menu
import com.napa.foodstorechallengech3.presentation.feature.detailmenu.DetailMenuActivity
import com.napa.foodstorechallengech3.presentation.feature.home.adapter.AdapterLayoutMode
import com.napa.foodstorechallengech3.presentation.feature.home.adapter.AdapterListCategories
import com.napa.foodstorechallengech3.presentation.feature.home.adapter.MenuListAdapter
import com.napa.foodstorechallengech3.utils.GenericViewModelFactory
import com.napa.foodstorechallengech3.utils.PreferenceDataStoreHelperImpl
import com.napa.foodstorechallengech3.utils.ResultWrapper
import com.napa.foodstorechallengech3.utils.proceedWhen
import kotlinx.coroutines.flow.Flow

class FragmentHome : Fragment() {
    private lateinit var binding: FragmentHomeBinding

    private val categoryAdapter: AdapterListCategories by lazy {
        AdapterListCategories {
            viewModel.getMenus(it.name)
        }
    }

    private val menuAdapter: MenuListAdapter by lazy {
        MenuListAdapter(AdapterLayoutMode.LINEAR) {menu: Menu ->
            navigateToDetail(menu)
        }
    }

    private val viewModel: HomeViewModel by viewModels {
        val chuckerInterceptor = ChuckerInterceptor(requireContext().applicationContext)
        val service = FoodStoreApiService.invoke(chuckerInterceptor)
        val dataSource = FoodStoreApiDataSource(service)
        val dataStore = this.requireContext().appDataStore
        val dataStoreHelper = PreferenceDataStoreHelperImpl(dataStore)
        val userPref: UserPreferenceDataSource = UserPreferenceDataSourceImpl(dataStoreHelper)
        val repo: MenuRepository =
            MenuRepositoryImpl(dataSource)
        GenericViewModelFactory.create(HomeViewModel(repo, userPref))
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupList()
        setupSwitch()
        setSwitchAction()
        observeGridPref()
        observeMenuData()
        observeData()
        getData()
    }

    private fun getData() {
        viewModel.getCategories()
        viewModel.getMenus()
    }

    private fun observeData() {
        viewModel.categories.observe(viewLifecycleOwner){
            it.proceedWhen(doOnSuccess = {
                binding.layoutStateCategory.root.isVisible = false
                binding.layoutStateCategory.pbLoading.isVisible = false
                binding.layoutStateCategory.tvError.isVisible = false
                binding.rvCategories.apply {
                    isVisible = true
                    adapter = categoryAdapter
                }
                it.payload?.let { data -> categoryAdapter.submitData(data) }
            }, doOnLoading = {
                binding.layoutStateCategory.root.isVisible = true
                binding.layoutStateCategory.pbLoading.isVisible = true
                binding.layoutStateCategory.tvError.isVisible = false
                binding.rvCategories.isVisible = false
            }, doOnError = {
                binding.layoutStateCategory.root.isVisible = true
                binding.layoutStateCategory.pbLoading.isVisible = false
                binding.layoutStateCategory.tvError.isVisible = true
                binding.layoutStateCategory.tvError.text = it.exception?.message.orEmpty()
                binding.rvCategories.isVisible = false
            })
        }
        viewModel.menus.observe(viewLifecycleOwner){
            it.proceedWhen(doOnSuccess = {
                binding.rvMenu.smoothScrollToPosition(0)
                binding.layoutStateMenu.root.isVisible = false
                binding.layoutStateMenu.pbLoading.isVisible = false
                binding.layoutStateMenu.tvError.isVisible = false
                binding.rvMenu.apply {
                    isVisible = true
                    adapter = menuAdapter
                }
                it.payload?.let { data -> menuAdapter.submitData(data) }
            }, doOnLoading = {
                binding.layoutStateMenu.root.isVisible = true
                binding.layoutStateMenu.pbLoading.isVisible = true
                binding.layoutStateMenu.tvError.isVisible = false
                binding.rvMenu.isVisible = false
            }, doOnError = {
                binding.layoutStateMenu.root.isVisible = true
                binding.layoutStateMenu.pbLoading.isVisible = false
                binding.layoutStateMenu.tvError.isVisible = true
                binding.layoutStateMenu.tvError.text = it.exception?.message.orEmpty()
                binding.rvMenu.isVisible = false
            }, doOnEmpty = {
                binding.layoutStateMenu.root.isVisible = true
                binding.layoutStateMenu.pbLoading.isVisible = false
                binding.layoutStateMenu.tvError.isVisible = true
                binding.layoutStateMenu.tvError.text = "Product not found"
                binding.rvMenu.isVisible = false
            })
        }
    }



    private fun observeMenuData() {
        viewModel.menuListLiveData.observe(viewLifecycleOwner){
            it.proceedWhen(doOnSuccess = {
                it.payload?.let { it1 -> menuAdapter.submitData(it1) }
            })
        }
    }
    private fun observeGridPref() {
        viewModel.usingGridLiveData.observe(viewLifecycleOwner) { isUsingGrid ->
            binding.switchListGrid.isChecked = isUsingGrid
            (binding.rvMenu.layoutManager as GridLayoutManager).spanCount = if (isUsingGrid) 2 else 1
            menuAdapter.adapterLayoutMode = if(isUsingGrid) AdapterLayoutMode.GRID else AdapterLayoutMode.LINEAR
            menuAdapter.refreshList()
        }
    }


    private fun setSwitchAction() {
        binding.switchListGrid.setOnCheckedChangeListener { _, isUsingGrid->
            viewModel.setUsingGridPref(isUsingGrid)
        }
    }


    private fun setupList() {
        val span = if(menuAdapter.adapterLayoutMode == AdapterLayoutMode.LINEAR) 1 else 2
        binding.rvMenu.apply {
            layoutManager = GridLayoutManager(requireContext(),span)
            adapter = this@FragmentHome.menuAdapter
        }
    }

    private fun setupSwitch() {
        binding.switchListGrid.setOnCheckedChangeListener { _, isChecked ->
            viewModel.setUsingGridPref(isChecked)
        }
    }

    private fun navigateToDetail(item: Menu) {
        DetailMenuActivity.startActivity(requireContext(), item)
    }

}