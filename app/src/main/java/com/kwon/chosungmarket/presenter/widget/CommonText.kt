package com.kwon.chosungmarket.presenter.widget

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.kwon.chosungmarket.ui.theme.AppTheme

/**
 * 앱의 친근한 스타일의 제목 텍스트 Composable
 */
@Composable
fun FriendlyTitle(
    modifier: Modifier = Modifier,
    text: String,
    style: androidx.compose.ui.text.TextStyle = AppTheme.styles.TitleLargeEB(),
    color: androidx.compose.ui.graphics.Color = AppTheme.colors.CompColorTextPrimary,
    alignment: androidx.compose.ui.text.style.TextAlign = androidx.compose.ui.text.style.TextAlign.Start
) {
    Text(
        text = text,
        style = style,
        color = color,
        textAlign = alignment,
        modifier = modifier
    )
}

/**
 * 앱의 친근한 스타일의 본문 텍스트 Composable
 */
@Composable
fun FriendlyBody(
    modifier: Modifier = Modifier,
    text: String,
    style: androidx.compose.ui.text.TextStyle = AppTheme.styles.BodySmallR(),
    color: androidx.compose.ui.graphics.Color = AppTheme.colors.CompColorTextDescription,
    alignment: androidx.compose.ui.text.style.TextAlign = androidx.compose.ui.text.style.TextAlign.Start
) {
    Text(
        text = text,
        style = style,
        color = color,
        textAlign = alignment,
        modifier = modifier
    )
}