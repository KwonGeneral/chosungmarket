package com.kwon.chosungmarket.presenter.widget.dialogs

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.kwon.chosungmarket.domain.model.QuizGroupData
import com.kwon.chosungmarket.ui.theme.AppTheme

/**
 * 퀴즈 시작 전 정보를 보여주는 대화상자
 *
 * @param quizGroup 시작할 퀴즈 그룹 데이터
 * @param onStart 퀴즈 시작 버튼 클릭 시 콜백
 * @param onDismiss 취소 버튼 클릭 또는 대화상자 외부 클릭 시 콜백
 */
@Composable
fun QuizDialog(
    quizGroup: QuizGroupData,
    onStart: () -> Unit,
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
            shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp),
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
                    text = quizGroup.title,
                    style = AppTheme.styles.TitleMediumB(),
                    color = AppTheme.colors.CompColorTextPrimary,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                // 퀴즈 설명
                Text(
                    text = quizGroup.description,
                    style = AppTheme.styles.BodySmallR(),
                    color = AppTheme.colors.CompColorTextDescription,
                    textAlign = TextAlign.Center,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 5,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp)
                )

                // 추가 정보
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "문제 수: ${quizGroup.quizIdList.size}문제",
                        style = AppTheme.styles.SubMediumR(),
                        color = AppTheme.colors.CompColorTextDescription,
                        textAlign = TextAlign.Center
                    )

                    Text(
                        text = "만든이: ${quizGroup.userNickname}",
                        style = AppTheme.styles.SubMediumR(),
                        color = AppTheme.colors.CompColorTextDescription,
                        textAlign = TextAlign.Center
                    )

                    Text(
                        text = "풀이 횟수: ${quizGroup.quizResultCount}회",
                        style = AppTheme.styles.SubMediumR(),
                        color = AppTheme.colors.CompColorTextDescription,
                        textAlign = TextAlign.Center
                    )
                }

                // 버튼 그룹 - 여백 없이 바로 하단에 붙도록 수정
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
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

                    // 풀기 버튼
                    Button(
                        onClick = onStart,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = AppTheme.colors.CompColorBrand
                        ),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = "퀴즈 풀기",
                            style = AppTheme.styles.BodySmallSB(),
                            color = AppTheme.colors.RefColorWhite
                        )
                    }
                }
            }
        }
    }
}