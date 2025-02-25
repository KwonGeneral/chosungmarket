package com.kwon.chosungmarket.presenter.widget

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.kwon.chosungmarket.ui.theme.AppTheme

/**
 * 퀴즈 관련 확인 대화상자
 *
 * @param title 대화상자 제목
 * @param confirmButtonText 확인 버튼 텍스트
 * @param onConfirm 확인 버튼 클릭 시 콜백
 * @param onDismiss 취소 버튼 클릭 또는 대화상자 외부 클릭 시 콜백
 */
@Composable
fun RetryQuizDialog(
    title: String,
    confirmButtonText: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = true
        )
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = AppTheme.colors.RefColorWhite
            ),
            elevation = CardDefaults.cardElevation(
                defaultElevation = 6.dp
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 24.dp, start = 24.dp, end = 24.dp, bottom = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // 제목
                Text(
                    text = title,
                    style = AppTheme.styles.TitleMediumB(),
                    color = AppTheme.colors.CompColorTextPrimary,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )

                // 적절한 여백 추가 (고정 크기)
                Spacer(modifier = Modifier.height(24.dp))

                // 버튼 그룹
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // 취소 버튼
                    OutlinedButton(
                        onClick = onDismiss,
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = AppTheme.colors.CompColorTextDescription
                        ),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = "취소",
                            style = AppTheme.styles.BodySmallSB()
                        )
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    // 확인 버튼
                    Button(
                        onClick = onConfirm,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = AppTheme.colors.CompColorBrand
                        ),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = confirmButtonText,
                            style = AppTheme.styles.BodySmallSB(),
                            color = AppTheme.colors.RefColorWhite
                        )
                    }
                }
            }
        }
    }
}