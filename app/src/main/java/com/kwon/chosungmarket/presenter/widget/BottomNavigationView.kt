package com.kwon.chosungmarket.presenter.widget

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape

import androidx.compose.material3.Icon

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

import androidx.compose.ui.res.painterResource

import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.kwon.chosungmarket.R
import com.kwon.chosungmarket.presenter.route.CmRouter
import com.kwon.chosungmarket.presenter.route.navigateTo
import com.kwon.chosungmarket.ui.theme.AppTheme

/**
 * 하단 네비게이션 바의 각 아이템을 구성하는 Composable
 */
@Composable
fun BottomItem(
    modifier: Modifier = Modifier,
    nv: NavHostController?,
    name: String,
    path: ArrayList<String>,
    icon: Int
) {
    val host = remember {
        mutableStateOf(nv?.currentDestination?.route ?: CmRouter.Home.route)
    }

    LaunchedEffect(nv) {
        val listener =
            NavController.OnDestinationChangedListener { controller, destination, arguments ->
                host.value = destination.route ?: CmRouter.Home.route
            }
        nv?.addOnDestinationChangedListener(listener)
    }
    Column(
        modifier = modifier.clickable {
            nv?.navigateTo(path.first())
        },
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            painter = painterResource(id = icon),
            contentDescription = name,
            modifier = Modifier
                .width(24.dp)
                .height(24.dp),
            tint = when (path.filter { it == host.value }.size) {
                0 -> {
                    AppTheme.colors.CompColorIconLabelPrimary
                }

                else -> {
                    AppTheme.colors.CompColorIconLabelBrandPrimary
                }
            }

        )
        Text(
            text = name,
            style = AppTheme.styles.SubSmallR(),
            color = AppTheme.colors.CompColorIconLabelPrimary,
            modifier = Modifier.padding(top = 4.dp)
        )

    }
}

/**
 * 하단 네비게이션 바의 아이템 리스트
 */
val bottomNavigationList = listOf(
    CmRouter.Home,
    CmRouter.HallOfFame,
    CmRouter.MyInfo,
)

/**
 * 앱의 하단 네비게이션 바를 구성하는 Composable
 */
@Composable
fun BottomNavigation(
    nv: NavHostController?,
    modifier: Modifier = Modifier.fillMaxWidth().background(
        color = AppTheme.colors.RefColorWhite,
    )
) {
    val showBottomNavigation = remember { mutableStateOf(true) }

    nv?.addOnDestinationChangedListener() { controller, destination, arguments ->
        showBottomNavigation.value = bottomNavigationList.any { it.route == destination.route }
    }

    if (!showBottomNavigation.value)
        return

    Column {
        Box(modifier = Modifier.height(1.dp).fillMaxWidth().background(color = AppTheme.colors.CompColorLineSecondary))

        Row(
            modifier = modifier.height(80.dp).fillMaxWidth()
                .background(
                    color = AppTheme.colors.RefColorWhite,
                    shape = RoundedCornerShape(
                        topStart = 16.dp,
                        topEnd = 16.dp,
                    )
                )
        ) {

            BottomItem(
                name = "명예의 전당",
                path = arrayListOf(CmRouter.HallOfFame.route),
                icon = R.drawable.icon_usage,
                nv = nv,
                modifier = Modifier.weight(1f).fillMaxHeight()
            )

            BottomItem(
                name = "홈",
                path = arrayListOf(CmRouter.Home.route),
                icon = R.drawable.icon_home,
                nv = nv,
                modifier = Modifier.weight(1f).fillMaxHeight()
            )

            BottomItem(
                name = "내 정보",
                path = arrayListOf(CmRouter.MyInfo.route),
                icon = R.drawable.icon_information,
                nv = nv,
                modifier = Modifier.weight(1f).fillMaxHeight()
            )
        }
    }
}