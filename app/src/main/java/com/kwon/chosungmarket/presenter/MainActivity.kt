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
import com.kwon.chosungmarket.presenter.route.AppNavigation
import com.kwon.chosungmarket.presenter.route.CmRouter
import com.kwon.chosungmarket.presenter.widget.BottomNavigation
import com.kwon.chosungmarket.presenter.widget.KToast
import com.kwon.chosungmarket.presenter.widget.ToastType
import com.kwon.chosungmarket.ui.theme.AppTheme
import com.kwon.chosungmarket.ui.theme.ChosungmarketTheme
import org.koin.java.KoinJavaComponent

/**
 * 앱의 메인 액티비티
 * 앱의 전체 UI 구조와 네비게이션을 설정합니다.
 */
class MainActivity : ComponentActivity() {
    companion object {
        /** 액티비티 인스턴스에 대한 전역 참조 */
        var mThis: MainActivity? = null
    }

    private var nv: NavHostController? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Edge-to-edge 디스플레이 설정
        enableEdgeToEdge()

        // 시스템 바 설정
        WindowCompat.getInsetsController(window, window.decorView).apply {
            isAppearanceLightStatusBars = true
            isAppearanceLightNavigationBars = true
        }

        // 화면 방향 설정
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED

        // Koin 스코프 설정
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
                // 메인 UI 구조 설정
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

                    // 토스트 메시지 오버레이
                    KToast.Toast()
                }
            }
        }
    }

    /**
     * 뒤로가기 두 번 누르면 앱 종료하는 기능을 구현하는 Composable
     */
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