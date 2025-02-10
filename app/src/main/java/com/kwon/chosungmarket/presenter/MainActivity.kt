package com.kwon.chosungmarket.presenter

import android.content.pm.ActivityInfo
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.OnBackPressedCallback
import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.core.view.WindowCompat
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.kwon.chosungmarket.common.utils.KLog
import com.kwon.chosungmarket.presenter.route.AppNavigation
import com.kwon.chosungmarket.presenter.route.CmRouter
import com.kwon.chosungmarket.presenter.widget.BottomNavigation
import com.kwon.chosungmarket.presenter.widget.KToast
import com.kwon.chosungmarket.presenter.widget.ToastType
import com.kwon.chosungmarket.ui.theme.AppTheme
import com.kwon.chosungmarket.ui.theme.ChosungmarketTheme
import org.koin.java.KoinJavaComponent

class MainActivity : ComponentActivity() {
    companion object {
        var mThis: MainActivity? = null
    }

    private var nv: NavHostController? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        WindowCompat.getInsetsController(window, window.decorView).apply {
            isAppearanceLightStatusBars = true
            isAppearanceLightNavigationBars = true
        }

        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED

        if (mThis == null) {
            try {
                KoinJavaComponent.getKoin().createScope<MainActivity>("MainActivity")
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        mThis = this

        setContent {
            nv = rememberNavController()
            DoubleBackToExit(nv!!)

            ChosungmarketTheme {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .systemBarsPadding()
                ) {
                    Scaffold(
                        topBar = {},
                        bottomBar = {
                            BottomNavigation(
                                modifier = Modifier.navigationBarsPadding(),
                                nv = nv
                            )
                        }
                    ) { innerPadding ->
                        Surface(
                            color = AppTheme.colors.RefColorWhite,
                            modifier = Modifier
                                .background(AppTheme.colors.RefColorWhite)
                                .padding(innerPadding)
                        ) {
                            nv?.let { navController ->
                                AppNavigation(navController)
                            }
                        }
                    }

                    // Toast overlay
                    KToast.Toast()
                }
            }
        }
    }

    @Composable
    fun DoubleBackToExit(navController: NavController) {
        val lastBackPressed = remember { mutableLongStateOf(0L) }
        val dispatcher = LocalOnBackPressedDispatcherOwner.current?.onBackPressedDispatcher

        DisposableEffect(Unit) {
            val callback = object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    if (navController.currentDestination?.route?.contains(CmRouter.Home.route) == true
                        || navController.currentDestination?.route == null) {
                        val currentTime = System.currentTimeMillis()
                        if (currentTime - lastBackPressed.longValue < 2000) {
                            this.remove()
                            finishAndRemoveTask()
                        } else {
                            KToast.show("한 번 더 누르시면 앱이 종료됩니다", ToastType.INFO)
                            lastBackPressed.longValue = currentTime
                        }
                    } else {
                        navController.popBackStack()
                    }
                }
            }
            dispatcher?.addCallback(callback)

            onDispose {
                callback.remove()
            }
        }
    }
}