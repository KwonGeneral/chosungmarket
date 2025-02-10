package com.kwon.chosungmarket.presenter.route

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class NavigationViewModel : ViewModel() {
    private val _navigationState = MutableStateFlow<CmRouter>(CmRouter.Home)
    val navigationState = _navigationState.asStateFlow()

    fun navigate(route: CmRouter) {
        _navigationState.value = route
    }
}