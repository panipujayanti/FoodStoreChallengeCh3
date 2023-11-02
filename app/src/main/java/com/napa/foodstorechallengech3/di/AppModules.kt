package com.napa.foodstorechallengech3.di

import com.chuckerteam.chucker.api.ChuckerInterceptor
import com.google.firebase.auth.FirebaseAuth
import com.napa.foodstorechallengech3.data.local.database.AppDatabase
import com.napa.foodstorechallengech3.data.local.database.datasource.CartDataSource
import com.napa.foodstorechallengech3.data.local.database.datasource.CartDatabaseDataSource
import com.napa.foodstorechallengech3.data.local.datastore.UserPreferenceDataSource
import com.napa.foodstorechallengech3.data.local.datastore.UserPreferenceDataSourceImpl
import com.napa.foodstorechallengech3.data.local.datastore.appDataStore
import com.napa.foodstorechallengech3.data.network.api.datasource.FoodStoreApiDataSource
import com.napa.foodstorechallengech3.data.network.api.datasource.FoodStoreDataSource
import com.napa.foodstorechallengech3.data.network.api.service.FoodStoreApiService
import com.napa.foodstorechallengech3.data.network.firebase.auth.FirebaseAuthDataSource
import com.napa.foodstorechallengech3.data.network.firebase.auth.FirebaseAuthDataSourceImpl
import com.napa.foodstorechallengech3.data.repository.CartRepository
import com.napa.foodstorechallengech3.data.repository.CartRepositoryImpl
import com.napa.foodstorechallengech3.data.repository.MenuRepository
import com.napa.foodstorechallengech3.data.repository.MenuRepositoryImpl
import com.napa.foodstorechallengech3.data.repository.UserRepository
import com.napa.foodstorechallengech3.data.repository.UserRepositoryImpl
import com.napa.foodstorechallengech3.presentation.feature.cart.CartViewModel
import com.napa.foodstorechallengech3.presentation.feature.checkout.CheckoutViewModel
import com.napa.foodstorechallengech3.presentation.feature.detailmenu.DetailMenuViewModel
import com.napa.foodstorechallengech3.presentation.feature.home.HomeViewModel
import com.napa.foodstorechallengech3.presentation.feature.login.LoginViewModel
import com.napa.foodstorechallengech3.presentation.feature.profile.ProfileViewModel
import com.napa.foodstorechallengech3.presentation.feature.register.RegisterViewModel
import com.napa.foodstorechallengech3.presentation.splashscreen.SplashViewModel
import com.napa.foodstorechallengech3.utils.AssetWrapper
import com.napa.foodstorechallengech3.utils.PreferenceDataStoreHelper
import com.napa.foodstorechallengech3.utils.PreferenceDataStoreHelperImpl
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.core.module.Module
import org.koin.dsl.module

object AppModules {

    private val localModule = module {
        single { AppDatabase.getInstance(androidContext()) }
        single { get<AppDatabase>().cartDao() }
        single { androidContext().appDataStore }
        single<PreferenceDataStoreHelper> { PreferenceDataStoreHelperImpl(get()) }
    }

    private val networkModule = module {
        single { ChuckerInterceptor(androidContext()) }
        single { FoodStoreApiService.invoke(get()) }
        single { FirebaseAuth.getInstance() }
    }

    private val dataSourceModule = module {
        single<CartDataSource> { CartDatabaseDataSource(get()) }
        single<UserPreferenceDataSource> { UserPreferenceDataSourceImpl(get()) }
        single<FoodStoreDataSource> { FoodStoreApiDataSource(get()) }
        single<FirebaseAuthDataSource> { FirebaseAuthDataSourceImpl(get()) }
    }

    private val repositoryModule = module {
        single<CartRepository> { CartRepositoryImpl(get(), get()) }
        single<MenuRepository> { MenuRepositoryImpl(get()) }
        single<UserRepository> { UserRepositoryImpl(get()) }
    }

    private val viewModelModule = module {
        viewModelOf(::HomeViewModel)
        viewModelOf(::CartViewModel)
        viewModel { params -> DetailMenuViewModel(params.get(), get()) }
        viewModelOf(::CheckoutViewModel)
        viewModelOf(::ProfileViewModel)
        viewModelOf(::RegisterViewModel)
        viewModelOf(::LoginViewModel)
        viewModelOf(::SplashViewModel)
    }

    private val utilsModule = module {
        single { AssetWrapper(androidContext()) }
    }

    val modules: List<Module> = listOf(
        localModule,
        networkModule,
        dataSourceModule,
        repositoryModule,
        viewModelModule,
        utilsModule
    )
}
