package com.kwon.chosungmarket.presenter.page

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import com.kwon.chosungmarket.domain.model.QuizResultData
import com.kwon.chosungmarket.domain.usecase.GetCurrentUserInfoUseCase
import com.kwon.chosungmarket.domain.usecase.GetQuizGroupUseCase
import com.kwon.chosungmarket.domain.usecase.GetQuizResultUseCase
import com.kwon.chosungmarket.domain.usecase.ToggleQuizLikeUseCase
import com.kwon.chosungmarket.presenter.route.CmRouter
import com.kwon.chosungmarket.presenter.widget.RetryQuizDialog
import com.kwon.chosungmarket.ui.theme.AppTheme
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

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
    viewModel: QuizResultViewModel = org.koin.androidx.compose.koinViewModel()
) {
    LaunchedEffect(Unit) {
        // 결과 페이지로 이동 시 이전 페이지들 삭제
        if (quizId != null) {
            viewModel.loadQuizResult(quizId)
        }
    }

    val uiState by viewModel.uiState.collectAsState()

    // 재도전 다이얼로그 상태
    var showRetryDialog by remember { mutableStateOf(false) }
    // 종료 확인 다이얼로그 상태
    var showExitDialog by remember { mutableStateOf(false) }

    // 뒤로가기 처리
    BackHandler {
        showExitDialog = true
    }

    // 홈으로 이동 함수
    val navigateToHome = {
        navController.navigate(CmRouter.Home.route) {
            popUpTo(0)
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "퀴즈 결과",
                        style = AppTheme.styles.BodySmallB()
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { showExitDialog = true }) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "홈으로"
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { showRetryDialog = true }) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "다시 풀기"
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = AppTheme.colors.RefColorWhite
                )
            )
        },
        containerColor = AppTheme.colors.RefColorWhite
    ) { padding ->
        Box(
            modifier = modifier
                .fillMaxSize()
                .padding(padding)
                .background(AppTheme.colors.RefColorWhite)
        ) {
            when (val state = uiState) {
                is QuizResultState.Loading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        androidx.compose.material3.CircularProgressIndicator(
                            color = AppTheme.colors.RefColorBlue50
                        )
                    }
                }
                is QuizResultState.Error -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = state.message,
                            style = AppTheme.styles.BodySmallR(),
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                }
                is QuizResultState.Success -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                    ) {
                        // 점수 표시 (최상단)
                        ScoreHeader(
                            score = state.score,
                            totalQuestions = state.results.size,
                            correctAnswers = state.results.count { it.isCorrect }
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        // 결과 목록 (확장/축소 가능)
                        LazyColumn(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(0.dp) // 간격 제거
                        ) {
                            items(state.results.withIndex().toList()) { (index, result) ->
                                ExpandableResultItem(
                                    result = result,
                                    questionNumber = index + 1
                                )
                                // 마지막 항목이 아니면 구분선 추가
                                if (index < state.results.size - 1) {
                                    Divider(
                                        modifier = Modifier.padding(vertical = 8.dp),
                                        color = AppTheme.colors.CompColorLineSecondary.copy(alpha = 0.3f) // 더 희미하게 조정
                                    )
                                }
                            }
                        }
                    }

                    // 종료 확인 다이얼로그
                    if (showExitDialog) {
                        RetryQuizDialog(
                            title = "홈으로 돌아가시겠습니까?",
                            confirmButtonText = "홈으로",
                            onConfirm = {
                                showExitDialog = false
                                navigateToHome()
                            },
                            onDismiss = {
                                showExitDialog = false
                            }
                        )
                    }

                    // 재도전 다이얼로그
                    if (showRetryDialog) {
                        RetryQuizDialog(
                            title = "문제를 다시 풀겠습니까?",
                            confirmButtonText = "재도전",
                            onConfirm = {
                                showRetryDialog = false
                                navController.navigate(CmRouter.QuizGame.createRoute(state.resultData.quizGroupId)) {
                                    popUpTo(0)
                                    launchSingleTop = true
                                }
                            },
                            onDismiss = {
                                showRetryDialog = false
                            }
                        )
                    }
                }
            }

            // 하단 좋아요 버튼
            if (uiState is QuizResultState.Success) {
                val state = uiState as QuizResultState.Success

                Box(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .fillMaxWidth()
                        .background(AppTheme.colors.RefColorWhite)
                ) {
                    val isLiked by viewModel.isLiked.collectAsState()

                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "퀴즈가 마음에 드셨다면",
                                style = AppTheme.styles.BodySmallR(),
                                color = AppTheme.colors.CompColorTextDescription
                            )
                            Text(
                                text = " 좋아요",
                                style = AppTheme.styles.BodySmallB(),
                                color = AppTheme.colors.RefColorRed50
                            )
                            Text(
                                text = "를 눌러주세요!",
                                style = AppTheme.styles.BodySmallR(),
                                color = AppTheme.colors.CompColorTextDescription
                            )
                        }

                        Button(
                            onClick = {
                                viewModel.toggleLike(state.resultData.quizGroupId)
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if(isLiked) AppTheme.colors.RefColorWhite else AppTheme.colors.RefColorGray90
                            ),
                            shape = RectangleShape,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                        ) {
                            Icon(
                                imageVector = if (isLiked) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                                contentDescription = "좋아요",
                                tint = if (isLiked) AppTheme.colors.RefColorRed50 else AppTheme.colors.CompColorTextDescription,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * 점수 헤더 섹션을 구성하는 Composable
 */
@Composable
private fun ScoreHeader(
    score: Int,
    totalQuestions: Int,
    correctAnswers: Int,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // 점수 - 색상 추가
        Text(
            text = buildAnnotatedString {
                withStyle(SpanStyle(
                    color = AppTheme.colors.RefColorBlue50,
                    fontWeight = FontWeight.ExtraBold
                )) {
                    append("$score")
                }
                append("점")
            },
            style = AppTheme.styles.TitleLargeEB(),
            color = AppTheme.colors.CompColorTextPrimary,
            textAlign = TextAlign.Center
        )

        // 맞춘 문제 수 - 색상 추가
        Text(
            text = buildAnnotatedString {
                append("총 ")
                withStyle(SpanStyle(
                    color = AppTheme.colors.CompColorTextPrimary,
                    fontWeight = FontWeight.Bold
                )) {
                    append("$totalQuestions")
                }
                append("문제 중 ")
                withStyle(SpanStyle(
                    color = if (correctAnswers > 0) AppTheme.colors.RefColorMint40 else AppTheme.colors.RefColorRed50,
                    fontWeight = FontWeight.Bold
                )) {
                    append("$correctAnswers")
                }
                append("문제 맞췄어요!")
            },
            style = AppTheme.styles.BodySmallR(),
            color = AppTheme.colors.CompColorTextPrimary,
            textAlign = TextAlign.Center
        )

        // 격려 메시지
        Text(
            text = when {
                score >= 90 -> "정말 대단해요! 🌟"
                score >= 70 -> "잘 하셨어요! 👍"
                score >= 50 -> "좋은 시도였어요! 💪"
                else -> "다음에는 더 잘할 수 있어요! ✨"
            },
            style = AppTheme.styles.BodySmallR(),
            color = AppTheme.colors.CompColorTextDescription,
            textAlign = TextAlign.Center
        )
    }
}

/**
 * 확장/축소 가능한 결과 아이템을 구성하는 Composable
 */
@Composable
private fun ExpandableResultItem(
    result: QuizAnswer,
    questionNumber: Int,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }
    val rotationState by animateFloatAsState(
        targetValue = if (expanded) 180f else 0f
    )

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clickable { expanded = !expanded }
            .padding(vertical = 8.dp, horizontal = 4.dp)
    ) {
        // 상단 행 (항상 표시)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 왼쪽: O/X 표시와 문제 번호, 초성
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                // 정답/오답 표시 (최좌측으로 이동)
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .background(
                            color = if (result.isCorrect)
                                AppTheme.colors.RefColorMint95
                            else
                                AppTheme.colors.RefColorRed95,
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    if (result.isCorrect) {
                        Text(
                            text = "✓",
                            color = AppTheme.colors.RefColorMint40,
                            style = AppTheme.styles.BodySmallB()
                        )
                    } else {
                        Text(
                            text = "✗",
                            color = AppTheme.colors.RefColorRed50,
                            style = AppTheme.styles.BodySmallB()
                        )
                    }
                }

                // 문제 번호와 초성 정보
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = "문제 ${questionNumber}",
                        style = AppTheme.styles.SubMediumR(),
                        color = AppTheme.colors.CompColorTextDescription
                    )

                    Text(
                        text = result.question,
                        style = AppTheme.styles.BodySmallB()
                    )

                    // 입력한 답 (강조)
                    Text(
                        text = "입력한 답: ${result.userAnswer}",
                        style = AppTheme.styles.BodySmallB(),
                        color = if (result.isCorrect)
                            AppTheme.colors.RefColorMint40
                        else
                            AppTheme.colors.RefColorRed50
                    )
                }
            }

            // 오른쪽: 확장 버튼만 표시
            IconButton(
                onClick = { expanded = !expanded }
            ) {
                Icon(
                    imageVector = Icons.Default.KeyboardArrowDown,
                    contentDescription = if (expanded) "접기" else "펼치기",
                    modifier = Modifier.rotate(rotationState)
                )
            }
        }

        // 확장된 콘텐츠 (선택적 표시)
        AnimatedVisibility(
            visible = expanded,
            enter = expandVertically(),
            exit = shrinkVertically()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp, start = 44.dp) // 왼쪽 패딩으로 정렬 맞춤
            ) {
                // 정답
                Text(
                    text = "정답: ${result.correctAnswer}",
                    style = AppTheme.styles.BodySmallSB(),
                    color = AppTheme.colors.CompColorTextPrimary
                )

                // 힌트
                if (result.hint.isNotBlank()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "힌트: ${result.hint}",
                        style = AppTheme.styles.BodySmallR(),
                        color = AppTheme.colors.CompColorTextDescription
                    )
                }
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
    private val getQuizResultUseCase: GetQuizResultUseCase,
    private val toggleQuizLikeUseCase: ToggleQuizLikeUseCase,
    private val getQuizGroupUseCase: GetQuizGroupUseCase,
    private val getCurrentUserInfoUseCase: GetCurrentUserInfoUseCase
) : ViewModel() {
    private val _uiState = MutableStateFlow<QuizResultState>(QuizResultState.Loading)
    val uiState = _uiState.asStateFlow()

    private val _isLiked = MutableStateFlow(false)
    val isLiked = _isLiked.asStateFlow()

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

                    // 퀴즈 그룹 정보 로드하여 좋아요 상태 체크
                    loadQuizGroup(quizResult.quizGroupId)

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

    /**
     * 퀴즈 그룹 정보를 로드하여 좋아요 상태를 확인합니다.
     */
    private suspend fun loadQuizGroup(quizGroupId: String) {
        try {
            val userId = getCurrentUserInfoUseCase.invoke().first()?.id ?: return

            getQuizGroupUseCase.invoke(quizGroupId)
                .onSuccess { (quizGroup, _) ->
                    _isLiked.value = quizGroup.likedUserIdList.contains(userId)
                }
        } catch (e: Exception) {
            // 에러 처리
        }
    }

    /**
     * 현재 퀴즈 그룹의 좋아요 상태를 반환합니다.
     */
    fun isQuizLiked(): Boolean {
        return _isLiked.value
    }

    /**
     * 퀴즈 그룹의 좋아요 상태를 토글합니다.
     */
    fun toggleLike(quizGroupId: String) {
        viewModelScope.launch {
            try {
                // UI 즉시 업데이트
                _isLiked.value = !_isLiked.value

                // 서버 반영
                toggleQuizLikeUseCase.invoke(quizGroupId)
                    .onFailure { error ->
                        // 실패 시 원래 상태로 롤백
                        _isLiked.value = !_isLiked.value
                    }
            } catch (e: Exception) {
                // 에러 처리
                _isLiked.value = !_isLiked.value
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