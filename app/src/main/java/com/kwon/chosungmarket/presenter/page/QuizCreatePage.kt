package com.kwon.chosungmarket.presenter.page

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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

/**
 * 퀴즈 생성 화면을 구성하는 Composable
 * 퀴즈 그룹의 제목, 설명, 난이도를 설정하고
 * 여러 개의 초성 퀴즈를 추가할 수 있습니다.
 *
 * @param navController 화면 전환을 위한 네비게이션 컨트롤러
 * @param modifier 레이아웃 수정을 위한 Modifier
 * @param viewModel 퀴즈 생성 상태를 관리하는 ViewModel
 */
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

/**
 * 개별 퀴즈 폼을 나타내는 카드 Composable
 * 초성, 정답, 힌트를 입력받는 필드들을 포함합니다.
 *
 * @param quiz 현재 편집 중인 퀴즈 데이터
 * @param onQuizUpdate 퀴즈 데이터 업데이트 콜백
 * @param onDelete 퀴즈 삭제 콜백
 * @param canDelete 삭제 가능 여부 (최소 1개의 퀴즈는 유지해야 함)
 */
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

/**
 * 퀴즈 생성 화면의 상태를 관리하는 ViewModel
 * 여러 개의 퀴즈 폼을 관리하고 최종 퀴즈 그룹 생성을 처리합니다.
 *
 * @param createQuizGroupUseCase 퀴즈 그룹 생성을 처리하는 UseCase
 */
class QuizCreateViewModel(
    private val createQuizGroupUseCase: CreateQuizGroupUseCase
) : ViewModel() {
    private val _createState = MutableStateFlow<QuizCreateState>(QuizCreateState.Initial)
    val createState = _createState.asStateFlow()

    private val _quizzes = MutableStateFlow<List<QuizFormData>>(listOf(QuizFormData()))
    val quizzes = _quizzes.asStateFlow()

    /**
     * 특정 인덱스의 퀴즈 폼을 업데이트합니다.
     * @param index 업데이트할 퀴즈의 인덱스
     * @param form 새로운 퀴즈 데이터
     */
    fun updateQuizForm(index: Int, form: QuizFormData) {
        val currentList = _quizzes.value.toMutableList()
        if (index < currentList.size) {
            currentList[index] = form
            _quizzes.value = currentList
        }
    }

    /**
     * 새로운 퀴즈 폼을 추가합니다.
     */
    fun addQuizForm() {
        val currentList = _quizzes.value.toMutableList()
        currentList.add(QuizFormData())
        _quizzes.value = currentList
    }

    /**
     * 특정 인덱스의 퀴즈 폼을 삭제합니다.
     * 최소 1개의 퀴즈는 유지되어야 합니다.
     *
     * @param index 삭제할 퀴즈의 인덱스
     */
    fun removeQuizForm(index: Int) {
        val currentList = _quizzes.value.toMutableList()
        if (currentList.size > 1) {  // 최소 1개의 퀴즈는 유지
            currentList.removeAt(index)
            _quizzes.value = currentList
        }
    }

    /**
     * 퀴즈 그룹을 생성합니다.
     * 입력값 검증 후 실제 생성을 수행합니다.
     *
     * @param title 퀴즈 그룹 제목
     * @param description 퀴즈 그룹 설명
     * @param difficulty 퀴즈 난이도
     */
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

/**
 * 퀴즈 생성 화면의 상태를 나타내는 sealed class
 */
sealed class QuizCreateState {
    /** 초기 상태 */
    data object Initial : QuizCreateState()

    /** 퀴즈 그룹 생성 중 상태 */
    data object Loading : QuizCreateState()

    /** 생성 성공 상태 */
    data object Success : QuizCreateState()

    /**
     * 생성 실패 상태
     * @param message 실패 사유 메시지
     */
    data class Error(val message: String) : QuizCreateState()
}

/**
 * 퀴즈 폼의 데이터를 담는 데이터 클래스
 *
 * @param consonant 초성 문제
 * @param answer 정답
 * @param description 힌트 또는 설명
 * @param tagList 태그 목록
 */
data class QuizFormData(
    val consonant: String = "",
    val answer: String = "",
    val description: String = "",
    val tagList: List<String> = emptyList()
)