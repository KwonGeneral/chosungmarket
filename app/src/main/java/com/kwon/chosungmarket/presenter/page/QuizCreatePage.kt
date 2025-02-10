package com.kwon.chosungmarket.presenter.page

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import com.kwon.chosungmarket.common.types.QuizDifficulty
import com.kwon.chosungmarket.domain.model.QuizData
import com.kwon.chosungmarket.domain.usecase.CreateQuizGroupUseCase
import com.kwon.chosungmarket.presenter.route.CmRouter
import com.kwon.chosungmarket.presenter.widget.FriendlyTitle
import com.kwon.chosungmarket.presenter.widget.KToast
import com.kwon.chosungmarket.presenter.widget.RoundedButton
import com.kwon.chosungmarket.presenter.widget.ToastType
import com.kwon.chosungmarket.ui.theme.AppTheme
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import java.util.UUID

@Composable
fun QuizCreatePage(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    viewModel: QuizCreateViewModel = koinViewModel()
) {
    val createState by viewModel.createState.collectAsState()
    val quizzes by viewModel.quizzes.collectAsState()

    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var selectedDifficulty by remember { mutableStateOf(QuizDifficulty.MEDIUM) }

    LaunchedEffect(createState) {
        when (createState) {
            is QuizCreateState.Success -> {
                KToast.show("퀴즈가 생성되었어요!", ToastType.SUCCESS)
                navController.navigate(CmRouter.Home.route) {
                    popUpTo(CmRouter.QuizCreate.route) { inclusive = true }
                }
            }
            is QuizCreateState.Error -> {
                KToast.show((createState as QuizCreateState.Error).message, ToastType.ERROR)
            }
            else -> {}
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(AppTheme.colors.CompColorPageDefaultBackground)
            .padding(16.dp)
    ) {
        FriendlyTitle(
            text = "새로운 퀴즈 만들기",
            modifier = Modifier.padding(bottom = 24.dp)
        )

        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("퀴즈 제목") },
                    modifier = Modifier.fillMaxWidth()
                )
            }

            item {
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("퀴즈 설명") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3
                )
            }

            item {
                Column {
                    Text(
                        text = "난이도",
                        style = AppTheme.styles.BodySmallSB(),
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        QuizDifficulty.entries.forEach { difficulty ->
                            FilterChip(
                                selected = selectedDifficulty == difficulty,
                                onClick = { selectedDifficulty = difficulty },
                                label = { Text(difficulty.name) }
                            )
                        }
                    }
                }
            }

            itemsIndexed(quizzes) { index, quiz ->
                QuizFormCard(
                    quiz = quiz,
                    onQuizUpdate = { viewModel.updateQuizForm(index, it) },
                    onDelete = { viewModel.removeQuizForm(index) },
                    canDelete = quizzes.size > 1
                )
            }

            item {
                TextButton(
                    onClick = { viewModel.addQuizForm() },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.Add, contentDescription = "퀴즈 추가")
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("퀴즈 추가하기")
                }
            }
        }

        RoundedButton(
            text = if (createState is QuizCreateState.Loading) "생성 중..." else "퀴즈 생성하기",
            onClick = {
                viewModel.createQuizGroup(title, description, selectedDifficulty)
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            isEnable = createState !is QuizCreateState.Loading,
        )
    }
}

@Composable
private fun QuizFormCard(
    quiz: QuizFormData,
    onQuizUpdate: (QuizFormData) -> Unit,
    onDelete: () -> Unit,
    canDelete: Boolean,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = AppTheme.colors.RefColorWhite
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if (canDelete) {
                IconButton(
                    onClick = onDelete,
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Icon(Icons.Default.Close, contentDescription = "퀴즈 삭제")
                }
            }

            OutlinedTextField(
                value = quiz.consonant,
                onValueChange = { onQuizUpdate(quiz.copy(consonant = it)) },
                label = { Text("초성") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = quiz.answer,
                onValueChange = { onQuizUpdate(quiz.copy(answer = it)) },
                label = { Text("정답") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = quiz.description,
                onValueChange = { onQuizUpdate(quiz.copy(description = it)) },
                label = { Text("힌트") },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

class QuizCreateViewModel(
    private val createQuizGroupUseCase: CreateQuizGroupUseCase
) : ViewModel() {
    private val _createState = MutableStateFlow<QuizCreateState>(QuizCreateState.Initial)
    val createState = _createState.asStateFlow()

    private val _quizzes = MutableStateFlow<List<QuizFormData>>(listOf(QuizFormData()))
    val quizzes = _quizzes.asStateFlow()

    fun updateQuizForm(index: Int, form: QuizFormData) {
        val currentList = _quizzes.value.toMutableList()
        if (index < currentList.size) {
            currentList[index] = form
            _quizzes.value = currentList
        }
    }

    fun addQuizForm() {
        val currentList = _quizzes.value.toMutableList()
        currentList.add(QuizFormData())
        _quizzes.value = currentList
    }

    fun removeQuizForm(index: Int) {
        val currentList = _quizzes.value.toMutableList()
        if (currentList.size > 1) {  // 최소 1개의 퀴즈는 유지
            currentList.removeAt(index)
            _quizzes.value = currentList
        }
    }

    fun createQuizGroup(title: String, description: String, difficulty: QuizDifficulty) {
        viewModelScope.launch {
            try {
                _createState.value = QuizCreateState.Loading

                // 입력 검증
                if (title.isBlank()) {
                    _createState.value = QuizCreateState.Error("제목을 입력해주세요")
                    return@launch
                }

                if (description.isBlank()) {
                    _createState.value = QuizCreateState.Error("설명을 입력해주세요")
                    return@launch
                }

                val quizzes = _quizzes.value
                if (quizzes.any { it.consonant.isBlank() || it.answer.isBlank() }) {
                    _createState.value = QuizCreateState.Error("모든 퀴즈의 초성과 정답을 입력해주세요")
                    return@launch
                }

                val quizDataList = quizzes.map { form ->
                    QuizData(
                        id = UUID.randomUUID().toString(),
                        consonant = form.consonant,
                        answer = form.answer,
                        description = form.description,
                        tagList = form.tagList,
                        difficulty = difficulty
                    )
                }

                createQuizGroupUseCase.invoke(title, description, quizDataList, difficulty)
                    .onSuccess {
                        _createState.value = QuizCreateState.Success
                    }
                    .onFailure { error ->
                        _createState.value = QuizCreateState.Error(error.message ?: "퀴즈 생성에 실패했어요")
                    }

            } catch (e: Exception) {
                _createState.value = QuizCreateState.Error(e.message ?: "알 수 없는 오류가 발생했어요")
            }
        }
    }
}

sealed class QuizCreateState {
    data object Initial : QuizCreateState()
    data object Loading : QuizCreateState()
    data object Success : QuizCreateState()
    data class Error(val message: String) : QuizCreateState()
}

data class QuizFormData(
    val consonant: String = "",
    val answer: String = "",
    val description: String = "",
    val tagList: List<String> = emptyList()
)