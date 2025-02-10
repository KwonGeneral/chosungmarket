package com.kwon.chosungmarket.presenter.page

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.kwon.chosungmarket.presenter.route.NavigationViewModel
import com.kwon.chosungmarket.ui.theme.AppTheme
import com.kwon.chosungmarket.ui.theme.ChosungmarketTheme
import org.koin.androidx.compose.koinViewModel

@Preview(showBackground = true)
@Composable
fun SettingPagePreview() {
    ChosungmarketTheme {
        SettingPage()
    }
}

@Composable
fun SettingPage(
    navController: NavHostController = rememberNavController(),
    @SuppressLint("ModifierParameter") modifier: Modifier = Modifier,
    navigationViewModel: NavigationViewModel = koinViewModel()
) {
    Box(modifier = Modifier.fillMaxSize()) {
        Text("Setting Page", modifier = modifier, style = AppTheme.styles.SubSmallR())
        Column {
            Text("Setting Page", modifier = modifier, style = AppTheme.styles.SubSmallR())
        }
    }
}