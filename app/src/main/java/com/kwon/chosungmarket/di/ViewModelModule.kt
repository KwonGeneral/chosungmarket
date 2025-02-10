package com.kwon.chosungmarket.di

import com.kwon.chosungmarket.presenter.page.HomePageViewModel
import com.kwon.chosungmarket.presenter.page.LoginPageViewModel
import com.kwon.chosungmarket.presenter.page.QuizCreateViewModel
import com.kwon.chosungmarket.presenter.page.QuizDetailViewModel
import com.kwon.chosungmarket.presenter.page.QuizGameViewModel
import com.kwon.chosungmarket.presenter.page.QuizResultViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {
    viewModel { LoginPageViewModel(get()) }
    viewModel { HomePageViewModel(get(), get()) }
    viewModel { QuizCreateViewModel(get()) }
    viewModel { QuizDetailViewModel(get(), get(), get(), get()) }
    viewModel { QuizGameViewModel(get(), get()) }
    viewModel { QuizResultViewModel(get()) }
}