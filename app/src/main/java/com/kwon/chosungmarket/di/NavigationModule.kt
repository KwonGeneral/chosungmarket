package com.kwon.chosungmarket.di

import com.kwon.chosungmarket.presenter.route.NavigationViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val navigationModule = module {
    viewModel { NavigationViewModel() }
}