package com.kwon.chosungmarket.di

import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

/**
 * 네비게이션 관련 의존성을 제공하는 Koin 모듈
 * 네비게이션 상태를 관리하는 ViewModel을 제공합니다.
 */
val navigationModule = module {
    viewModel { NavigationViewModel() }
}