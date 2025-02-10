package com.kwon.chosungmarket.presenter.widget

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.kwon.chosungmarket.ui.theme.AppTheme
import kotlinx.coroutines.delay

data class ToastData(
    val message: String,
    val type: ToastType = ToastType.ERROR
)

enum class ToastType {
    SUCCESS,
    ERROR,
    INFO
}

object KToast {
    private var toastState: MutableState<ToastData?> = mutableStateOf(null)

    fun show(message: String, type: ToastType = ToastType.ERROR) {
        toastState.value = ToastData(message, type)
    }

    @Composable
    fun Toast() {
        val toastData = toastState.value
        val visible = remember { mutableStateOf(false) }

        LaunchedEffect(toastData) {
            if (toastData != null) {
                visible.value = true
                delay(2000)
                visible.value = false
                delay(300)
                toastState.value = null
            }
        }

        AnimatedVisibility(
            visible = visible.value,
            enter = slideInVertically(
                initialOffsetY = { -it },
                animationSpec = tween(300)
            ) + fadeIn(animationSpec = tween(300)),
            exit = slideOutVertically(
                targetOffsetY = { -it },
                animationSpec = tween(300)
            ) + fadeOut(animationSpec = tween(300))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                contentAlignment = Alignment.TopCenter
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(
                            when (toastData?.type) {
                                ToastType.SUCCESS -> AppTheme.colors.SysColorPositive
                                ToastType.ERROR -> AppTheme.colors.SysColorNegative
                                ToastType.INFO -> AppTheme.colors.SysColorInformative
                                null -> AppTheme.colors.SysColorNegative
                            }
                        )
                        .padding(horizontal = 24.dp, vertical = 12.dp)
                ) {
                    FriendlyBody(
                        text = toastData?.message ?: "",
                        color = AppTheme.colors.RefColorWhite
                    )
                }
            }
        }
    }
}