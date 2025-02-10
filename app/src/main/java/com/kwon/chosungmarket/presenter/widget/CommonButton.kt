package com.kwon.chosungmarket.presenter.widget

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.kwon.chosungmarket.ui.theme.AppTheme

/**
 * 앱 전반에서 사용되는 둥근 모서리의 버튼 Composable
 */
@Composable
fun RoundedButton(
    modifier: Modifier = Modifier,
    text: String,
    color: Color = AppTheme.colors.RefColorBlue50,
    isEnable: Boolean = true,
    leadingIcon: (@Composable () -> Unit)? = null,
    onClick: () -> Unit,
) {
    Button(
        onClick = onClick,
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        enabled = isEnable,
        colors = ButtonDefaults.buttonColors(
            containerColor = color
        )
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            leadingIcon?.let {
                it()
                Spacer(modifier = Modifier.width(8.dp))
            }
            Text(
                text = text,
                style = AppTheme.styles.BodySmallSB(),
                color = AppTheme.colors.RefColorWhite
            )
        }
    }
}