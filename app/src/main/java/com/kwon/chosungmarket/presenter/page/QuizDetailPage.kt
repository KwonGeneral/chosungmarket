package com.kwon.chosungmarket.presenter.page

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.kwon.chosungmarket.domain.model.QuizData
import com.kwon.chosungmarket.presenter.widget.FriendlyBody
import com.kwon.chosungmarket.presenter.widget.FriendlyTitle
import com.kwon.chosungmarket.presenter.widget.RoundedButton
import com.kwon.chosungmarket.ui.theme.AppTheme
import org.koin.androidx.compose.koinViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kwon.chosungmarket.common.utils.KLog
import com.kwon.chosungmarket.domain.model.QuizGroupData
import com.kwon.chosungmarket.domain.usecase.GetQuizGroupUseCase
import com.kwon.chosungmarket.domain.usecase.GetQuizGroupListUseCase
import com.kwon.chosungmarket.domain.usecase.GetCurrentUserInfoUseCase
import com.kwon.chosungmarket.domain.usecase.ToggleQuizLikeUseCase
import com.kwon.chosungmarket.presenter.route.CmRouter
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuizDetailPage(
    navController: NavHostController,
    quizId: String?,
    modifier: Modifier = Modifier,
    viewModel: QuizDetailViewModel = koinViewModel()
) {
    LaunchedEffect(quizId) {
        if (quizId != null) {
            viewModel.loadQuizGroup(quizId)
        }
    }

    val uiState by viewModel.uiState.collectAsState()
    val quizzes by viewModel.quizzes.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("퀴즈 상세") },
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
                is QuizDetailState.Loading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center),
                        color = AppTheme.colors.RefColorBlue50
                    )
                }
                is QuizDetailState.Success -> {
                    QuizDetailContent(
                        quizGroup = state.quizGroup,
                        isLiked = state.isLiked,
                        onLikeClick = { viewModel.toggleLike(state.quizGroup.id) },
                        onStartQuiz = {
                            navController.navigate(CmRouter.QuizGame.createRoute(state.quizGroup.id))
                        },
                        quizzes = quizzes
                    )
                }
                is QuizDetailState.Error -> {
                    FriendlyBody(
                        text = state.message,
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(16.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun QuizDetailContent(
    quizGroup: QuizGroupData,
    isLiked: Boolean,
    onLikeClick: () -> Unit,
    onStartQuiz: () -> Unit,
    quizzes: List<QuizData> = emptyList(),
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            FriendlyTitle(
                text = quizGroup.title,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            FriendlyBody(text = quizGroup.description)
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "by ${quizGroup.userNickname.isEmpty().let { if (it) "익명" else quizGroup.userNickname }}",
                    style = AppTheme.styles.SubMediumR(),
                    color = AppTheme.colors.CompColorTextDescription
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    IconButton(onClick = onLikeClick) {
                        Icon(
                            imageVector = if (isLiked) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = "좋아요",
                            tint = if (isLiked) AppTheme.colors.RefColorRed50 else AppTheme.colors.CompColorIconLabelPrimary
                        )
                    }
                    Text(
                        text = quizGroup.likeCount.toString(),
                        style = AppTheme.styles.SubMediumR(),
                        color = AppTheme.colors.CompColorTextDescription
                    )
                }
            }
        }

        items(quizzes) { quiz ->
            QuizPreviewCard(quiz = quiz)
        }

        item {
            RoundedButton(
                text = "퀴즈 풀기",
                onClick = onStartQuiz,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp)
            )
        }
    }
}

@Composable
private fun QuizPreviewCard(
    quiz: QuizData,
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
            Text(
                text = "초성: ${quiz.consonant}",
                style = AppTheme.styles.BodySmallB(),
                color = AppTheme.colors.CompColorTextPrimary
            )
            if (quiz.description.isNotEmpty()) {
                Text(
                    text = "힌트: ${quiz.description}",
                    style = AppTheme.styles.SubMediumR(),
                    color = AppTheme.colors.CompColorTextDescription,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
            ElevatedFilterChip(
                selected = false,
                onClick = { },
                label = {
                    Text(
                        text = quiz.difficulty.name,
                        style = AppTheme.styles.SubSmallR(),
                        color = AppTheme.colors.CompColorTextPrimary
                    )
                },
                colors = FilterChipDefaults.elevatedFilterChipColors(
                    containerColor = AppTheme.colors.RefColorBlue95
                )
            )
        }
    }
}

class QuizDetailViewModel(
    private val getQuizGroupListUseCase: GetQuizGroupListUseCase,
    private val getQuizGroupUseCase: GetQuizGroupUseCase,
    private val toggleQuizLikeUseCase: ToggleQuizLikeUseCase,
    private val getCurrentUserInfoUseCase: GetCurrentUserInfoUseCase,
) : ViewModel() {
    private val _uiState = MutableStateFlow<QuizDetailState>(QuizDetailState.Loading)
    val uiState = _uiState.asStateFlow()

    private val _quizzes = MutableStateFlow<List<QuizData>>(emptyList())
    val quizzes = _quizzes.asStateFlow()

    private var userId: String? = null

    init {
        viewModelScope.launch {
            userId = getCurrentUserInfoUseCase.invoke().first()?.id
        }
    }

    fun loadQuizGroup(quizGroupId: String) {
        viewModelScope.launch {
            try {
                _uiState.value = QuizDetailState.Loading

                val quizGroups = getQuizGroupListUseCase.invoke(limit = 10).first()
                val quizGroup = quizGroups.find { it.id == quizGroupId }

                if (quizGroup != null) {
                    val quizGroupData: Pair<QuizGroupData, List<QuizData>> = getQuizGroupUseCase.invoke(quizGroupId).getOrElse {
                        _uiState.value = QuizDetailState.Error("퀴즈를 불러올 수 없습니다.")
                        return@launch
                    }

                    val quizzes = quizGroupData.second

                    _quizzes.value = quizzes
                    _uiState.value = QuizDetailState.Success(
                        quizGroup = quizGroup,
                        isLiked = quizGroup.likedUserIdList.contains(userId)
                    )
                } else {
                    _uiState.value = QuizDetailState.Error("퀴즈 그룹을 찾을 수 없습니다.")
                }
            } catch (e: Exception) {
                _uiState.value = QuizDetailState.Error(e.localizedMessage ?: "알 수 없는 오류")
            }
        }
    }

    fun toggleLike(quizGroupId: String) {
        viewModelScope.launch {
            try {
                val currentUserId = userId ?: run {
                    return@launch
                }

                val currentState = _uiState.value
                if (currentState is QuizDetailState.Success) {
                    val updatedQuizGroup = currentState.quizGroup.copy(
                        likeCount = if (currentState.isLiked)
                            currentState.quizGroup.likeCount - 1
                        else
                            currentState.quizGroup.likeCount + 1,
                        likedUserIdList = if (currentState.isLiked)
                            currentState.quizGroup.likedUserIdList - currentUserId
                        else
                            currentState.quizGroup.likedUserIdList + currentUserId
                    )
                    _uiState.value = QuizDetailState.Success(
                        quizGroup = updatedQuizGroup,
                        isLiked = !currentState.isLiked
                    )
                }

                toggleQuizLikeUseCase.invoke(quizGroupId)
                    .onFailure { e ->
                        // 실패 시 원래 상태로 되돌리기
                        loadQuizGroup(quizGroupId)
                    }
            } catch (e: Exception) {
                KLog.e("좋아요 토글 중 오류 발생", e)
            }
        }
    }
}

sealed class QuizDetailState {
    data object Loading : QuizDetailState()
    data class Success(
        val quizGroup: QuizGroupData,
        val isLiked: Boolean
    ) : QuizDetailState()
    data class Error(val message: String) : QuizDetailState()
}