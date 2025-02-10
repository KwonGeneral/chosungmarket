package com.kwon.chosungmarket.presenter.page

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import com.kwon.chosungmarket.presenter.route.NavigationViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun NotificationPage(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    navigationViewModel: NavigationViewModel = koinViewModel()
) {
    Text("NotificationPage")
}