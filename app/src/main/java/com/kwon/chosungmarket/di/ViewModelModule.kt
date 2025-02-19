package com.kwon.chosungmarket.di

import com.kwon.chosungmarket.presenter.page.HallOfFameViewModel
import com.kwon.chosungmarket.presenter.page.HomePageViewModel
import com.kwon.chosungmarket.presenter.page.LoginPageViewModel
import com.kwon.chosungmarket.presenter.page.QuizCreateViewModel
import com.kwon.chosungmarket.presenter.page.QuizDetailViewModel
import com.kwon.chosungmarket.presenter.page.QuizGameViewModel
import com.kwon.chosungmarket.presenter.page.QuizResultViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

/**
 * 화면별 ViewModel 의존성을 제공하는 Koin 모듈
 * 각 페이지의 ViewModel 인스턴스를 관리합니다.
 */
val viewModelModule = module {
    // 각 페이지별 ViewModel 제공
    viewModel { LoginPageViewModel(get()) }
    viewModel { HomePageViewModel(get(), get()) }
    viewModel { QuizCreateViewModel(get()) }
    viewModel { QuizDetailViewModel(get(), get(), get(), get(), get()) }
    viewModel { QuizGameViewModel(get(), get()) }
    viewModel { QuizResultViewModel(get()) }
    viewModel { HallOfFameViewModel(get()) }
}