package com.kwon.chosungmarket.presenter.page

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.kwon.chosungmarket.common.utils.KLog
import com.kwon.chosungmarket.presenter.widget.FriendlyBody
import com.kwon.chosungmarket.presenter.widget.FriendlyTitle
import com.kwon.chosungmarket.ui.theme.AppTheme
import org.koin.androidx.compose.koinViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kwon.chosungmarket.domain.model.QuizGroupData
import com.kwon.chosungmarket.domain.repository.QuizRepositoryImpl
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import androidx.lifecycle.viewModelScope
import com.kwon.chosungmarket.domain.model.QuizData
import com.kwon.chosungmarket.domain.usecase.GetQuizGroupUseCase
import com.kwon.chosungmarket.domain.usecase.ProcessQuizResultUseCase
import com.kwon.chosungmarket.presenter.route.CmRouter
import com.kwon.chosungmarket.presenter.widget.KToast
import com.kwon.chosungmarket.presenter.widget.RoundedButton
import com.kwon.chosungmarket.presenter.widget.ToastType
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

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

    fun updateAnswer(answer: String) {
        val currentAnswerList = _userAnswerList.value.toMutableMap()
        currentAnswerList[_currentQuestionIndex.value] = answer
        _userAnswerList.value = currentAnswerList
    }

    fun moveToNext() {
        if (_currentQuestionIndex.value < _quizzes.value.size - 1) {
            _currentQuestionIndex.value += 1
        }
    }

    fun moveToPrevious() {
        if (_currentQuestionIndex.value > 0) {
            _currentQuestionIndex.value -= 1
        }
    }

    fun canMoveToNext(): Boolean {
        return _currentQuestionIndex.value < _quizzes.value.size - 1
    }

    fun canMoveToPrevious(): Boolean {
        return _currentQuestionIndex.value > 0
    }

    fun isLastQuestion(): Boolean {
        return _currentQuestionIndex.value == _quizzes.value.size - 1
    }
}

sealed class QuizGameState {
    data object Loading : QuizGameState()
    data class Success(val quizGroupId: String) : QuizGameState()
    data class Submitted(val resultId: String) : QuizGameState()
    data class Error(val message: String) : QuizGameState()
}