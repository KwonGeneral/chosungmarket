package com.kwon.chosungmarket.presenter.page

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import com.kwon.chosungmarket.domain.model.QuizResultData
import com.kwon.chosungmarket.domain.usecase.GetQuizResultUseCase
import com.kwon.chosungmarket.presenter.route.CmRouter
import com.kwon.chosungmarket.presenter.widget.FriendlyBody
import com.kwon.chosungmarket.presenter.widget.FriendlyTitle
import com.kwon.chosungmarket.presenter.widget.RoundedButton
import com.kwon.chosungmarket.ui.theme.AppTheme
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

/**
 * 퀴즈 결과 화면을 구성하는 Composable
 * 점수, 정답/오답 목록, 문제별 상세 결과를 표시합니다.
 *
 * @param navController 화면 전환을 위한 네비게이션 컨트롤러
 * @param quizId 결과를 조회할 퀴즈 결과 ID
 * @param modifier 레이아웃 수정을 위한 Modifier
 * @param viewModel 결과 화면의 상태를 관리하는 ViewModel
 */
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

/**
 * 점수 카드를 구성하는 Composable
 * 총점, 정답 개수, 격려 메시지를 표시합니다.
 *
 * @param score 획득한 점수 (100점 만점)
 * @param totalQuestions 전체 문제 수
 * @param correctAnswerList 맞은 문제 수
 */
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

/**
 * 개별 퀴즈 결과 카드를 구성하는 Composable
 * 문제, 정답, 사용자 답안, 힌트를 표시합니다.
 *
 * @param result 개별 퀴즈의 결과 데이터
 */
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

/**
 * 퀴즈 결과 화면의 상태를 관리하는 ViewModel
 * 결과 데이터 로드와 상세 정보 제공을 처리합니다.
 *
 * @param getQuizResultUseCase 퀴즈 결과 조회 UseCase
 */
class QuizResultViewModel(
    private val getQuizResultUseCase: GetQuizResultUseCase
) : ViewModel() {
    private val _uiState = MutableStateFlow<QuizResultState>(QuizResultState.Loading)
    val uiState = _uiState.asStateFlow()

    /**
     * 퀴즈 결과를 로드하고 점수를 계산합니다.
     *
     * @param resultId 조회할 결과 ID
     */
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

/**
 * 퀴즈 결과 화면의 상태를 나타내는 sealed class
 */
sealed class QuizResultState {
    /** 데이터 로딩 중 상태 */
    data object Loading : QuizResultState()

    /**
     * 결과 로드 성공 상태
     * @param score 획득한 점수
     * @param results 문제별 결과 목록
     * @param resultData 전체 결과 데이터
     */
    data class Success(
        val score: Int,
        val results: List<QuizAnswer>,
        val resultData: QuizResultData
    ) : QuizResultState()

    /**
     * 에러 상태
     * @param message 에러 메시지
     */
    data class Error(val message: String) : QuizResultState()
}

/**
 * 개별 퀴즈 문제의 결과를 담는 데이터 클래스
 *
 * @param question 문제 번호 또는 내용
 * @param correctAnswer 정답
 * @param userAnswer 사용자가 입력한 답안
 * @param isCorrect 정답 여부
 * @param hint 문제의 힌트
 */
data class QuizAnswer(
    val question: String,
    val correctAnswer: String,
    val userAnswer: String,
    val isCorrect: Boolean,
    val hint: String
)