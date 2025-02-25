package com.kwon.chosungmarket.presenter.widget.dialogs

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.kwon.chosungmarket.ui.theme.AppTheme

/**
 * 앱 전체에서 재사용 가능한 대화상자 컴포넌트
 *
 * @param title 대화상자 제목
 * @param message 대화상자 내용 (선택사항)
 * @param confirmButtonText 확인 버튼 텍스트
 * @param dismissButtonText 취소 버튼 텍스트 (선택사항)
 * @param onConfirm 확인 버튼 클릭 시 콜백
 * @param onDismiss 취소 버튼 클릭 또는 대화상자 외부 클릭 시 콜백
 * @param properties 대화상자 속성 (선택사항)
 * @param content 커스텀 콘텐츠 (선택사항)
 */
@Composable
fun KDialog(
    title: String,
    message: String? = null,
    confirmButtonText: String,
    dismissButtonText: String? = null,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    properties: DialogProperties = DialogProperties(
        dismissOnBackPress = true,
        dismissOnClickOutside = true
    ),
    content: @Composable (() -> Unit)? = null
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = properties
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
                    .padding(24.dp),
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

                Spacer(modifier = Modifier.height(16.dp))

                // 메시지
                if (message != null) {
                    Text(
                        text = message,
                        style = AppTheme.styles.BodySmallR(),
                        color = AppTheme.colors.CompColorTextDescription,
                        textAlign = TextAlign.Center,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(24.dp))
                }

                // 커스텀 콘텐츠
                content?.invoke()

                // 버튼
                if (dismissButtonText != null) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        // 취소 버튼
                        TextButton(
                            onClick = onDismiss,
                            colors = ButtonDefaults.textButtonColors(
                                contentColor = AppTheme.colors.CompColorTextDescription
                            ),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                text = dismissButtonText,
                                style = AppTheme.styles.BodySmallSB()
                            )
                        }

                        Spacer(modifier = Modifier.width(16.dp))

                        // 확인 버튼
                        TextButton(
                            onClick = onConfirm,
                            colors = ButtonDefaults.textButtonColors(
                                contentColor = AppTheme.colors.CompColorBrand
                            ),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                text = confirmButtonText,
                                style = AppTheme.styles.BodySmallSB()
                            )
                        }
                    }
                } else {
                    // 확인 버튼만 있는 경우
                    TextButton(
                        onClick = onConfirm,
                        colors = ButtonDefaults.textButtonColors(
                            contentColor = AppTheme.colors.CompColorBrand
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = confirmButtonText,
                            style = AppTheme.styles.BodySmallSB()
                        )
                    }
                }
            }
        }
    }
}