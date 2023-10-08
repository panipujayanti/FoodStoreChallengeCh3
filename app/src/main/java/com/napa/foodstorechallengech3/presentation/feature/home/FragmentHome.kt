package com.napa.foodstorechallengech3.presentation.feature.home

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.napa.foodstorechallengech3.data.dummy.DummyCategoriesDataSource
import com.napa.foodstorechallengech3.data.dummy.DummyCategoriesDataSourceImpl
import com.napa.foodstorechallengech3.data.local.database.AppDatabase
import com.napa.foodstorechallengech3.data.local.database.datasource.MenuDatabaseDataSource
import com.napa.foodstorechallengech3.data.local.datastore.UserPreferenceDataSource
import com.napa.foodstorechallengech3.data.local.datastore.UserPreferenceDataSourceImpl
import com.napa.foodstorechallengech3.data.local.datastore.appDataStore
import com.napa.foodstorechallengech3.data.repository.MenuRepository
import com.napa.foodstorechallengech3.data.repository.MenuRepositoryImpl
import com.napa.foodstorechallengech3.databinding.FragmentHomeBinding
import com.napa.foodstorechallengech3.model.Categories
import com.napa.foodstorechallengech3.model.Menu
import com.napa.foodstorechallengech3.presentation.feature.detailmenu.DetailMenuActivity
import com.napa.foodstorechallengech3.presentation.feature.home.adapter.AdapterLayoutMode
import com.napa.foodstorechallengech3.presentation.feature.home.adapter.AdapterListCategories
import com.napa.foodstorechallengech3.presentation.feature.home.adapter.MenuListAdapter
import com.napa.foodstorechallengech3.utils.GenericViewModelFactory
import com.napa.foodstorechallengech3.utils.PreferenceDataStoreHelperImpl
import com.napa.foodstorechallengech3.utils.proceedWhen

class FragmentHome : Fragment() {
    private lateinit var binding: FragmentHomeBinding

    private val adapter: MenuListAdapter by lazy {
        MenuListAdapter(AdapterLayoutMode.LINEAR) {menu: Menu ->
            navigateToDetail(menu)
        }
    }

    private val viewModel: HomeViewModel by viewModels {
        GenericViewModelFactory.create(HomeViewModel(createProductRepo(), createPreferenceDataSource()))
    }

    private fun createProductRepo(): MenuRepository {
        val cds: DummyCategoriesDataSource = DummyCategoriesDataSourceImpl()
        val database = AppDatabase.getInstance(requireContext())
        val menuDao = database.menuDao()
        val menuDataSource = MenuDatabaseDataSource(menuDao)
        return MenuRepositoryImpl(menuDataSource, cds)
    }
    private fun createPreferenceDataSource() : UserPreferenceDataSource {
        val dataStore = this.requireContext().appDataStore
        val dataStoreHelper = PreferenceDataStoreHelperImpl(dataStore)
        return UserPreferenceDataSourceImpl(dataStoreHelper)
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
        showListCategories(viewModel.getCategoriesData())
        setupList()
        setupSwitch()
        setSwitchAction()
        observeGridPref()
        observeProductData()
    }

    private fun observeProductData() {
        viewModel.productListLiveData.observe(viewLifecycleOwner){
            it.proceedWhen(doOnSuccess = {
                it.payload?.let { it1 -> adapter.submitData(it1) }
            })
        }
    }
    private fun observeGridPref() {
        viewModel.usingGridLiveData.observe(viewLifecycleOwner) { isUsingGrid ->
            binding.switchListGrid.isChecked = isUsingGrid
            (binding.rvMenu.layoutManager as GridLayoutManager).spanCount = if (isUsingGrid) 2 else 1
            adapter.adapterLayoutMode = if(isUsingGrid) AdapterLayoutMode.GRID else AdapterLayoutMode.LINEAR
            adapter.refreshList()
        }
    }


    private fun setSwitchAction() {
        binding.switchListGrid.setOnCheckedChangeListener { _, isUsingGrid->
            viewModel.setUsingGridPref(isUsingGrid)
        }
    }


    private fun showListCategories(data : List<Categories>) {
        val categoryListAdapter = AdapterListCategories()
        binding.rvCategories.adapter = categoryListAdapter
        binding.rvCategories.layoutManager = LinearLayoutManager(requireContext(),
            LinearLayoutManager.HORIZONTAL, false )
        categoryListAdapter.setData(data)
    }

    private fun setupList() {
        val span = if(adapter.adapterLayoutMode == AdapterLayoutMode.LINEAR) 1 else 2
        binding.rvMenu.apply {
            layoutManager = GridLayoutManager(requireContext(),span)
            adapter = this@FragmentHome.adapter
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