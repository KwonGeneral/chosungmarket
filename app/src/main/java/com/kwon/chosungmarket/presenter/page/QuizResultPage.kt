package com.kwon.chosungmarket.presenter.page

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.kwon.chosungmarket.common.utils.KLog
import com.kwon.chosungmarket.presenter.route.CmRouter
import com.kwon.chosungmarket.presenter.widget.FriendlyBody
import com.kwon.chosungmarket.presenter.widget.FriendlyTitle
import com.kwon.chosungmarket.presenter.widget.RoundedButton
import com.kwon.chosungmarket.ui.theme.AppTheme
import org.koin.androidx.compose.koinViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kwon.chosungmarket.domain.model.QuizData
import com.kwon.chosungmarket.domain.model.QuizGroupData
import com.kwon.chosungmarket.domain.model.QuizResultData
import com.kwon.chosungmarket.domain.repository.QuizRepositoryImpl
import com.kwon.chosungmarket.domain.repository.QuizResultRepositoryImpl
import com.kwon.chosungmarket.domain.usecase.GetQuizResultUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuizResultPage(
    navController: NavHostController,
    quizId: String?,
    modifier: Modifier = Modifier,
    viewModel: QuizResultViewModel = koinViewModel()
) {
    LaunchedEffect(quizId) {
        if (quizId != null) {
            viewModel.loadQuizResult(quizId)
        }
    }

    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("퀴즈 결과") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = AppTheme.colors.RefColorWhite
                )
            )
        }
    ) { padding ->
        Box(
            modifier = modifier
                .fillMaxSize()
                .background(AppTheme.colors.CompColorPageDefaultBackground)
                .padding(padding)
        ) {
            when (val state = uiState) {
                is QuizResultState.Loading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center),
                        color = AppTheme.colors.RefColorBlue50
                    )
                }
                is QuizResultState.Error -> {
                    FriendlyBody(
                        text = state.message,
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(16.dp)
                    )
                }
                is QuizResultState.Success -> {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        item {
                            ScoreCard(
                                score = state.score,
                                totalQuestions = state.results.size,
                                correctAnswerList = state.results.count { it.isCorrect }
                            )
                        }

                        items(state.results) { result ->
                            QuizResultCard(result = result)
                        }

                        item {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 16.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                RoundedButton(
                                    text = "다시 풀기",
                                    onClick = {
                                        navController.navigate(CmRouter.QuizGame.createRoute(state.resultData.quizGroupId)) {
                                            popUpTo(CmRouter.QuizGame.route) { inclusive = true }
                                        }
                                    },
                                    modifier = Modifier.fillMaxWidth(),
                                    leadingIcon = {
                                        Icon(Icons.Default.Refresh, contentDescription = null)
                                    }
                                )
                                RoundedButton(
                                    text = "홈으로",
                                    onClick = {
                                        navController.navigate(CmRouter.Home.route) {
                                            popUpTo(0)
                                        }
                                    },
                                    modifier = Modifier.fillMaxWidth(),
                                    leadingIcon = {
                                        Icon(Icons.Default.Home, contentDescription = null)
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ScoreCard(
    score: Int,
    totalQuestions: Int,
    correctAnswerList: Int,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = AppTheme.colors.RefColorBlue95
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FriendlyTitle(
                text = "${score}점",
                alignment = TextAlign.Center
            )
            FriendlyBody(
                text = "총 ${totalQuestions}문제 중 ${correctAnswerList}문제 맞췄어요!",
                alignment = TextAlign.Center
            )

            Text(
                text = when {
                    score >= 90 -> "정말 대단해요! 😊"
                    score >= 70 -> "잘 하셨어요! 👍"
                    score >= 50 -> "좋은 시도였어요! 💪"
                    else -> "다음에는 더 잘할 수 있어요! 🌟"
                },
                style = AppTheme.styles.BodySmallR(),
                color = AppTheme.colors.CompColorTextDescription,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun QuizResultCard(
    result: QuizAnswer,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (result.isCorrect)
                AppTheme.colors.RefColorMint95
            else
                AppTheme.colors.RefColorRed95
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = if (result.isCorrect) Icons.Default.Done else Icons.Default.Close,
                    contentDescription = if (result.isCorrect) "정답" else "오답",
                    tint = if (result.isCorrect)
                        AppTheme.colors.RefColorMint40
                    else
                        AppTheme.colors.RefColorRed50
                )
                Text(
                    text = if (result.isCorrect) "정답" else "오답",
                    style = AppTheme.styles.BodySmallB(),
                    color = if (result.isCorrect)
                        AppTheme.colors.RefColorMint40
                    else
                        AppTheme.colors.RefColorRed50
                )
            }

            Divider(
                modifier = Modifier.padding(vertical = 8.dp),
                color = if (result.isCorrect)
                    AppTheme.colors.RefColorMint80
                else
                    AppTheme.colors.RefColorRed60.copy(alpha = 0.3f)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = "초성",
                        style = AppTheme.styles.SubMediumR(),
                        color = AppTheme.colors.CompColorTextDescription
                    )
                    Text(
                        text = result.question,
                        style = AppTheme.styles.BodySmallB()
                    )
                }
                Column(
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = "정답",
                        style = AppTheme.styles.SubMediumR(),
                        color = AppTheme.colors.CompColorTextDescription
                    )
                    Text(
                        text = result.correctAnswer,
                        style = AppTheme.styles.BodySmallB()
                    )
                }
            }

            if (!result.isCorrect) {
                Text(
                    text = "입력한 답: ${result.userAnswer}",
                    style = AppTheme.styles.BodySmallR(),
                    color = AppTheme.colors.RefColorRed50
                )
            }

            if (result.hint.isNotBlank()) {
                Text(
                    text = "힌트: ${result.hint}",
                    style = AppTheme.styles.SubMediumR(),
                    color = AppTheme.colors.CompColorTextDescription
                )
            }
        }
    }
}

class QuizResultViewModel(
    private val getQuizResultUseCase: GetQuizResultUseCase
) : ViewModel() {
    private val _uiState = MutableStateFlow<QuizResultState>(QuizResultState.Loading)
    val uiState = _uiState.asStateFlow()

    fun loadQuizResult(resultId: String) {
        viewModelScope.launch {
            _uiState.value = QuizResultState.Loading

            getQuizResultUseCase.invoke(resultId)
                .onSuccess { quizResult ->
                    val results = getQuizResultUseCase.getQuizAnswerDetails(quizResult)
                    val score = ((results.count { it.isCorrect }.toFloat() / results.size) * 100).toInt()

                    _uiState.value = QuizResultState.Success(
                        score = score,
                        results = results,
                        resultData = quizResult
                    )
                }
                .onFailure { error ->
                    _uiState.value = QuizResultState.Error(
                        error.localizedMessage ?: "알 수 없는 오류"
                    )
                }
        }
    }
}

sealed class QuizResultState {
    data object Loading : QuizResultState()
    data class Success(
        val score: Int,
        val results: List<QuizAnswer>,
        val resultData: QuizResultData
    ) : QuizResultState()
    data class Error(val message: String) : QuizResultState()
}

data class QuizAnswer(
    val question: String,
    val correctAnswer: String,
    val userAnswer: String,
    val isCorrect: Boolean,
    val hint: String
)