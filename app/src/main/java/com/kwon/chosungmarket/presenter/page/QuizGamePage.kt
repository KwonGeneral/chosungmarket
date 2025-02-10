package com.kwon.chosungmarket.presenter.page

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import com.kwon.chosungmarket.domain.model.QuizData
import com.kwon.chosungmarket.domain.usecase.GetQuizGroupUseCase
import com.kwon.chosungmarket.domain.usecase.ProcessQuizResultUseCase
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
 * 퀴즈 게임 화면을 구성하는 Composable
 * 퀴즈를 순차적으로 풀고 답안을 제출할 수 있습니다.
 *
 * @param navController 화면 전환을 위한 네비게이션 컨트롤러
 * @param quizId 진행할 퀴즈 그룹의 ID
 * @param modifier 레이아웃 수정을 위한 Modifier
 * @param viewModel 게임 진행 상태를 관리하는 ViewModel
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuizGamePage(
    navController: NavHostController,
    quizId: String?,
    modifier: Modifier = Modifier,
    viewModel: QuizGameViewModel = koinViewModel()
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

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("퀴즈 풀기") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "뒤로가기"
                        )
                    }
                }
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
                is QuizGameState.Loading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center),
                        color = AppTheme.colors.RefColorBlue50
                    )
                }
                is QuizGameState.Error -> {
                    FriendlyBody(
                        text = state.message,
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(16.dp)
                    )
                }
                is QuizGameState.Success -> {
                    QuizContent(
                        onBack = { navController.popBackStack() },
                        currentQuestionIndex = currentQuestionIndex,
                        userAnswerList = userAnswerList,
                        onAnswerUpdate = viewModel::updateAnswer,
                        onNext = viewModel::moveToNext,
                        onPrevious = viewModel::moveToPrevious,
                        onSubmit = viewModel::submitQuiz,
                        quizzes = quizzes,
                        canMoveToNext = viewModel.canMoveToNext(),
                        canMoveToPrevious = viewModel.canMoveToPrevious(),
                        isLastQuestion = viewModel.isLastQuestion()
                    )
                }
                is QuizGameState.Submitted -> {
                    LaunchedEffect(Unit) {
                        navController.navigate(CmRouter.QuizResult.createRoute(state.resultId))
                    }
                }
            }
        }
    }
}

/**
 * 퀴즈 게임의 실제 내용을 구성하는 Composable
 * 현재 문제, 답안 입력 필드, 이동 버튼을 표시합니다.
 *
 * @param quizzes 전체 퀴즈 목록
 * @param currentQuestionIndex 현재 보여주는 문제의 인덱스
 * @param userAnswerList 사용자가 입력한 답안 목록
 * @param onAnswerUpdate 답안 업데이트 콜백
 * @param onNext 다음 문제 이동 콜백
 * @param onPrevious 이전 문제 이동 콜백
 * @param onSubmit 답안 제출 콜백
 * @param onBack 뒤로가기 콜백
 * @param canMoveToNext 다음 문제 이동 가능 여부
 * @param canMoveToPrevious 이전 문제 이동 가능 여부
 * @param isLastQuestion 마지막 문제 여부
 */
@Composable
private fun QuizContent(
    quizzes: List<QuizData>,
    currentQuestionIndex: Int,
    userAnswerList: Map<Int, String>,
    onAnswerUpdate: (String) -> Unit,
    onNext: () -> Unit,
    onPrevious: () -> Unit,
    onSubmit: () -> Unit,
    onBack: () -> Unit,
    canMoveToNext: Boolean,
    canMoveToPrevious: Boolean,
    isLastQuestion: Boolean,
    modifier: Modifier = Modifier
){
    if (quizzes.isEmpty()) {
        Box(
            modifier = modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "퀴즈 문제가 없습니다.",
                    style = AppTheme.styles.BodySmallB(),
                    color = AppTheme.colors.CompColorTextPrimary
                )
                Spacer(modifier = Modifier.height(16.dp))
                RoundedButton(
                    text = "돌아가기",
                    onClick = onBack,
                    modifier = Modifier.width(200.dp)
                )
            }
        }
        return
    }

    val currentQuiz = quizzes[currentQuestionIndex]
    val keyboardController = LocalSoftwareKeyboardController.current

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        FriendlyBody(
            text = "문제 ${currentQuestionIndex + 1}/${quizzes.size}",
            alignment = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )

        FriendlyTitle(
            text = "퀴즈 #${currentQuestionIndex + 1}",
            alignment = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = AppTheme.colors.RefColorBlue95
            )
        ) {
            Text(
                text = currentQuiz.consonant,
                style = AppTheme.styles.TitleLargeEB(),
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 32.dp)
            )
        }

        if (currentQuiz.description.isNotBlank()) {
            FriendlyBody(
                text = "힌트: ${currentQuiz.description}",
                alignment = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }

        OutlinedTextField(
            value = userAnswerList[currentQuestionIndex] ?: "",
            onValueChange = { onAnswerUpdate(it) },
            label = { Text("정답을 입력하세요") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(
                onDone = { keyboardController?.hide() }
            )
        )

        Spacer(modifier = Modifier.weight(1f))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            TextButton(
                onClick = onPrevious,
                enabled = canMoveToPrevious
            ) {
                Icon(Icons.Default.ArrowBack, contentDescription = "이전")
                Spacer(Modifier.width(8.dp))
                Text("이전")
            }

            if (isLastQuestion) {
                RoundedButton(
                    text = "제출하기",
                    onClick = onSubmit,
                    modifier = Modifier.fillMaxWidth()
                )
            } else {
                TextButton(
                    onClick = onNext,
                    enabled = canMoveToNext
                ) {
                    Text("다음")
                    Spacer(Modifier.width(8.dp))
                    Icon(Icons.Default.ArrowForward, contentDescription = "다음")
                }
            }
        }
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