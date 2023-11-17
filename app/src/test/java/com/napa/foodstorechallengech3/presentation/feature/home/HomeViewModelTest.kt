package com.napa.foodstorechallengech3.presentation.feature.home

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.napa.foodstorechallengech3.data.local.datastore.UserPreferenceDataSource
import com.napa.foodstorechallengech3.data.repository.MenuRepository
import com.napa.foodstorechallengech3.tools.MainCoroutineRule
import com.napa.foodstorechallengech3.utils.AssetWrapper
import io.mockk.MockKAnnotations
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.rules.TestRule

class HomeViewModelTest {

    @MockK
    private lateinit var repo: MenuRepository

    @MockK
    private lateinit var userPreferenceDataSource: UserPreferenceDataSource

    @MockK
    private lateinit var assetWrapper: AssetWrapper

    @get:Rule
    val testRule: TestRule = InstantTaskExecutorRule()

    @OptIn(ExperimentalCoroutinesApi::class)
    @get:Rule
    val coroutineRule: TestRule = MainCoroutineRule(UnconfinedTestDispatcher())

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
    }
}
