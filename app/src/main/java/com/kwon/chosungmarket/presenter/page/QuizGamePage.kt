package com.kwon.chosungmarket.presenter.page

import android.annotation.SuppressLint
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import com.kwon.chosungmarket.domain.model.QuizData
import com.kwon.chosungmarket.domain.usecase.GetQuizGroupUseCase
import com.kwon.chosungmarket.domain.usecase.ProcessQuizResultUseCase
import com.kwon.chosungmarket.presenter.widget.FriendlyBody
import com.kwon.chosungmarket.presenter.widget.NextQuizDialog
import com.kwon.chosungmarket.presenter.widget.dialogs.KDialog
import com.kwon.chosungmarket.ui.theme.AppTheme
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch


/**
 * 퀴즈 게임 화면을 구성하는 Composable
 * 퀴즈를 순차적으로 풀고 답안을 제출할 수 있습니다.
 *
 * @param navController 화면 전환을 위한 네비게이션 컨트롤러
 * @param quizId 진행할 퀴즈 그룹의 ID
 * @param modifier 레이아웃 수정을 위한 Modifier
 * @param viewModel 게임 진행 상태를 관리하는 ViewModel
 */
@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
@Composable
fun QuizGamePage(
    navController: NavHostController,
    quizId: String?,
    modifier: Modifier = Modifier,
    viewModel: QuizGameViewModel = org.koin.androidx.compose.koinViewModel()
) {
    LaunchedEffect(quizId) {
        if (quizId != null) {
            viewModel.loadQuizGroup(quizId)
        }
    }

    val uiState by viewModel.uiState.collectAsState()
    val currentQuestionIndex by viewModel.currentQuestionIndex.collectAsState()
    val userAnswerList by viewModel.userAnswerList.collectAsState()
    val quizzes by viewModel.quizzes.collectAsState()
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusRequester = remember { FocusRequester() }

    // 키보드 높이를 추적하기 위한 로직
    val view = LocalView.current
    var keyboardHeight by remember { mutableStateOf(0) }

    // 키보드 높이 변화를 감지
    LaunchedEffect(view) {
        val windowInsets = ViewCompat.getRootWindowInsets(view)
        val imeHeight = windowInsets?.getInsets(WindowInsetsCompat.Type.ime())?.bottom ?: 0
        keyboardHeight = imeHeight
    }

    // 퀴즈 포기 확인 다이얼로그 상태
    var showQuitDialog by remember { mutableStateOf(false) }

    // 다음 문제 확인 다이얼로그 상태
    var showNextDialog by remember { mutableStateOf(false) }

    when (val state = uiState) {
        is QuizGameState.Loading -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                androidx.compose.material3.CircularProgressIndicator(
                    color = AppTheme.colors.RefColorBlue50
                )
            }
        }
        is QuizGameState.Error -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                FriendlyBody(
                    text = state.message,
                    modifier = Modifier.padding(16.dp)
                )
            }
        }
        is QuizGameState.Success -> {
            if (quizzes.isNotEmpty()) {
                val currentQuiz = quizzes[currentQuestionIndex]
                val answer = userAnswerList[currentQuestionIndex] ?: ""
                val isLastQuestion = viewModel.isLastQuestion()

                // 배경 그라디언트 생성
                val backgroundBrush = Brush.verticalGradient(
                    colors = listOf(
                        AppTheme.colors.RefColorBlue50,
                        AppTheme.colors.RefColorBlue60,
                        AppTheme.colors.RefColorBlue70
                    )
                )

                // 초성 글자 크기 계산
                val consonantSize = calculateConsonantSize(currentQuiz.consonant)

                // 메인 레이아웃
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(backgroundBrush)
                        .statusBarsPadding()
                ) {
                    // 상단 내용 및 X 버튼
                    Box(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        // X 버튼 (최상단 좌측)
                        IconButton(
                            onClick = { showQuitDialog = true },
                            modifier = Modifier
                                .align(Alignment.TopStart)
                                .padding(16.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "퀴즈 종료",
                                tint = AppTheme.colors.RefColorWhite
                            )
                        }

                        // 퀴즈 내용 (중앙)
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 20.dp)
                                .padding(top = 60.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Top
                        ) {
                            // 퀴즈 번호 정보
                            Text(
                                text = "총 ${quizzes.size}문제",
                                style = AppTheme.styles.SubMediumR(),
                                color = AppTheme.colors.RefColorWhite.copy(alpha = 0.7f),
                                textAlign = TextAlign.Center
                            )

                            Text(
                                text = "${currentQuestionIndex + 1}번 퀴즈",
                                style = AppTheme.styles.TitleMediumB(),
                                color = AppTheme.colors.RefColorWhite,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(bottom = 60.dp)
                            )

                            // 초성 표시
                            Text(
                                text = currentQuiz.consonant,
                                style = AppTheme.styles.TitleLargeEB().copy(fontSize = consonantSize.sp),
                                color = AppTheme.colors.RefColorWhite,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(bottom = 24.dp)
                            )

                            // 힌트 표시
                            if (currentQuiz.description.isNotBlank()) {
                                Text(
                                    text = "${currentQuiz.description}",
                                    style = AppTheme.styles.BodySmallR(),
                                    color = AppTheme.colors.RefColorWhite.copy(alpha = 0.7f),
                                    textAlign = TextAlign.Center
                                )
                            }
                        }

                        // 제출 버튼
                        Button(
                            onClick = { showNextDialog = true },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = AppTheme.colors.RefColorWhite
                            ),
                            shape = RoundedCornerShape(100.dp), // 완전 라운드 처리
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 20.dp, vertical = 16.dp)
                                .align(Alignment.BottomCenter)
                                .padding(bottom = 64.dp) // TextField 높이 고려한 여백
                        ) {
                            Text(
                                text = if (isLastQuestion) "제출하기" else "다음 문제로",
                                style = AppTheme.styles.BodySmallSB(),
                                color = AppTheme.colors.RefColorBlue50,
                                modifier = Modifier.padding(vertical = 8.dp)
                            )

                            if (!isLastQuestion) {
                                Icon(
                                    imageVector = Icons.Default.ArrowForward,
                                    contentDescription = null,
                                    tint = AppTheme.colors.RefColorBlue50,
                                    modifier = Modifier.padding(start = 8.dp)
                                )
                            }
                        }
                    }

                    // 정답 입력 필드 - 화면 하단에 배치하되 키보드와 함께 이동
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .align(Alignment.BottomCenter)
                    ) {
                        // 사용자 정의 레이아웃 수정자로 항상 화면 하단에 배치
                        TextField(
                            value = answer,
                            onValueChange = { viewModel.updateAnswer(it) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .focusRequester(focusRequester)
                                // 키보드 패딩을 명시적으로 추가
                                .imePadding(),
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = AppTheme.colors.RefColorWhite,
                                unfocusedContainerColor = AppTheme.colors.RefColorWhite,
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent,
                                cursorColor = AppTheme.colors.RefColorBlue50
                            ),
                            textStyle = AppTheme.styles.BodySmallB().copy(
                                textAlign = TextAlign.Center
                            ),
                            placeholder = {
                                Text(
                                    "정답 입력",
                                    style = AppTheme.styles.BodySmallR(),
                                    color = AppTheme.colors.CompColorTextDescription,
                                    modifier = Modifier.fillMaxWidth(),
                                    textAlign = TextAlign.Center
                                )
                            },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                            keyboardActions = KeyboardActions(
                                onDone = {
                                    keyboardController?.hide()
                                    showNextDialog = true
                                }
                            )
                        )
                    }
                }

                // 포기 확인 다이얼로그
                if (showQuitDialog) {
                    KDialog(
                        title = "퀴즈를 포기하시겠습니까?",
                        message = "지금 포기하면 지금까지 입력한 정답은 저장되지 않습니다.",
                        confirmButtonText = "계속 풀기",
                        dismissButtonText = "포기하기",
                        onConfirm = { showQuitDialog = false },
                        onDismiss = { navController.popBackStack() }
                    )
                }

                // 다음 문제 확인 다이얼로그
                if (showNextDialog) {
                    NextQuizDialog(
                        onConfirm = {
                            if (isLastQuestion) {
                                viewModel.submitQuiz()
                            } else {
                                viewModel.moveToNext()
                            }
                            showNextDialog = false
                        },
                        onDismiss = {
                            showNextDialog = false
                        },
                        isLastQuestion = isLastQuestion
                    )
                }

                // 새 문제로 이동할 때마다 포커스 요청
                LaunchedEffect(currentQuestionIndex) {
                    focusRequester.requestFocus()
                }
            }
        }
        is QuizGameState.Submitted -> {
            LaunchedEffect(Unit) {
                navController.navigate(com.kwon.chosungmarket.presenter.route.CmRouter.QuizResult.createRoute(state.resultId))
            }
        }
    }
}

/**
 * 초성의 길이에 따라 적절한 글자 크기를 계산합니다.
 */
private fun calculateConsonantSize(consonant: String): Int {
    return when {
        consonant.length <= 2 -> 80
        consonant.length <= 4 -> 60
        consonant.length <= 6 -> 48
        consonant.length <= 8 -> 36
        else -> 28
    }
}

/**
 * 퀴즈 게임의 상태를 관리하는 ViewModel
 * 문제 진행, 답안 입력, 결과 제출을 처리합니다.
 *
 * @param processQuizResultUseCase 퀴즈 결과 처리 UseCase
 * @param getQuizGroupUseCase 퀴즈 그룹 조회 UseCase
 */
class QuizGameViewModel(
    private val processQuizResultUseCase: ProcessQuizResultUseCase,
    private val getQuizGroupUseCase: GetQuizGroupUseCase
) : ViewModel() {
    private val _uiState = MutableStateFlow<QuizGameState>(QuizGameState.Loading)
    val uiState = _uiState.asStateFlow()

    private val _currentQuestionIndex = MutableStateFlow(0)
    val currentQuestionIndex = _currentQuestionIndex.asStateFlow()

    private val _userAnswerList = MutableStateFlow<MutableMap<Int, String>>(mutableMapOf())
    val userAnswerList = _userAnswerList.asStateFlow()

    private val _quizzes = MutableStateFlow<List<QuizData>>(emptyList())
    val quizzes = _quizzes.asStateFlow()

    private var currentQuizGroupId: String? = null

    /**
     * 퀴즈 그룹 데이터를 로드합니다.
     *
     * @param quizGroupId 로드할 퀴즈 그룹의 ID
     */
    fun loadQuizGroup(quizGroupId: String) {
        viewModelScope.launch {
            try {
                _uiState.value = QuizGameState.Loading

                getQuizGroupUseCase.invoke(quizGroupId)
                    .onSuccess { (quizGroup, quizzes) ->
                        currentQuizGroupId = quizGroup.id
                        _quizzes.value = quizzes
                        _uiState.value = QuizGameState.Success(quizGroup.id)
                    }
                    .onFailure { error ->
                        _uiState.value = QuizGameState.Error(error.localizedMessage ?: "알 수 없는 오류")
                    }
            } catch (e: Exception) {
                _uiState.value = QuizGameState.Error(e.localizedMessage ?: "알 수 없는 오류")
            }
        }
    }

    /**
     * 모든 답안을 제출하고 결과를 처리합니다.
     * 성공 시 결과 화면으로 이동합니다.
     */
    fun submitQuiz() {
        val quizGroupId = currentQuizGroupId ?: run {
            _uiState.value = QuizGameState.Error("Quiz group ID not found")
            return
        }
        val answerList = _userAnswerList.value.values.toList()

        viewModelScope.launch {
            processQuizResultUseCase.invoke(quizGroupId, answerList)
                .onSuccess { resultId ->
                    _uiState.value = QuizGameState.Submitted(resultId)
                }
                .onFailure { error ->
                    _uiState.value = QuizGameState.Error(error.message ?: "퀴즈 제출에 실패했습니다")
                }
        }
    }

    /**
     * 현재 문제의 답안을 업데이트합니다.
     *
     * @param answer 사용자가 입력한 답안
     */
    fun updateAnswer(answer: String) {
        val currentAnswerList = _userAnswerList.value.toMutableMap()
        currentAnswerList[_currentQuestionIndex.value] = answer
        _userAnswerList.value = currentAnswerList
    }

    /** 다음 문제로 이동합니다. */
    fun moveToNext() {
        if (_currentQuestionIndex.value < _quizzes.value.size - 1) {
            _currentQuestionIndex.value += 1
        }
    }

    /** 이전 문제로 이동합니다. */
    fun moveToPrevious() {
        if (_currentQuestionIndex.value > 0) {
            _currentQuestionIndex.value -= 1
        }
    }

    /** 다음 문제로 이동 가능한지 확인합니다. */
    fun canMoveToNext(): Boolean {
        return _currentQuestionIndex.value < _quizzes.value.size - 1
    }

    /** 이전 문제로 이동 가능한지 확인합니다. */
    fun canMoveToPrevious(): Boolean {
        return _currentQuestionIndex.value > 0
    }

    /** 현재 문제가 마지막 문제인지 확인합니다. */
    fun isLastQuestion(): Boolean {
        return _currentQuestionIndex.value == _quizzes.value.size - 1
    }
}

/**
 * 퀴즈 게임 화면의 상태를 나타내는 sealed class
 */
sealed class QuizGameState {
    /** 데이터 로딩 중 상태 */
    data object Loading : QuizGameState()

    /**
     * 게임 진행 중 상태
     * @param quizGroupId 진행 중인 퀴즈 그룹 ID
     */
    data class Success(val quizGroupId: String) : QuizGameState()

    /**
     * 제출 완료 상태
     * @param resultId 생성된 결과 ID
     */
    data class Submitted(val resultId: String) : QuizGameState()

    /**
     * 에러 상태
     * @param message 에러 메시지
     */
    data class Error(val message: String) : QuizGameState()
}