package com.kwon.chosungmarket.presenter.page

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.kwon.chosungmarket.presenter.widget.FriendlyBody
import com.kwon.chosungmarket.presenter.widget.FriendlyTitle
import com.kwon.chosungmarket.presenter.widget.RoundedCard
import com.kwon.chosungmarket.ui.theme.AppTheme
import org.koin.androidx.compose.koinViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kwon.chosungmarket.domain.model.QuizGroupData
import com.kwon.chosungmarket.domain.usecase.GetTopQuizListUseCase
import com.kwon.chosungmarket.presenter.route.CmRouter
import com.kwon.chosungmarket.presenter.route.navigateTo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * 명예의 전당 화면을 구성하는 Composable
 * 상위 랭킹 퀴즈 그룹 목록을 표시합니다.
 *
 * @param navController 화면 전환을 위한 네비게이션 컨트롤러
 * @param modifier 레이아웃 수정을 위한 Modifier
 * @param viewModel 화면의 상태를 관리하는 ViewModel
 */
@Composable
fun HallOfFamePage(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    viewModel: HallOfFameViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(AppTheme.colors.CompColorPageDefaultBackground)
    ) {
        when (val state = uiState) {
            is HallOfFameState.Loading -> {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                    color = AppTheme.colors.RefColorBlue50
                )
            }
            is HallOfFameState.Success -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    item {
                        FriendlyTitle(
                            text = "명예의 전당",
                            modifier = Modifier.padding(bottom = 16.dp)
                        )
                    }

                    itemsIndexed(state.rankings) { _, rankedQuiz ->
                        RankingItem(
                            rank = rankedQuiz.rank,
                            quizGroup = rankedQuiz.quizGroup,
                            onItemClick = {
                                navController.navigateTo(
                                    CmRouter.QuizDetail.createRoute(rankedQuiz.quizGroup.id)
                                )
                            }
                        )
                    }
                }
            }
            is HallOfFameState.Error -> {
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

@Composable
private fun RankingItem(
    rank: Int,
    quizGroup: QuizGroupData,
    onItemClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    RoundedCard(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onItemClick)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            RankBadge(rank = rank)

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = quizGroup.title,
                    style = AppTheme.styles.BodySmallB(),
                    color = AppTheme.colors.CompColorTextPrimary
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "by ${quizGroup.userNickname}",
                        style = AppTheme.styles.SubSmallR(),
                        color = AppTheme.colors.CompColorTextDescription
                    )

                    Text(
                        text = "❤️ ${quizGroup.likeCount}",
                        style = AppTheme.styles.SubSmallR(),
                        color = AppTheme.colors.CompColorTextDescription
                    )
                }
            }
        }
    }
}

@Composable
private fun RankBadge(
    rank: Int,
    modifier: Modifier = Modifier
) {
    val (backgroundColor, textColor) = when {
        rank == 1 -> AppTheme.colors.RefColorBlue50 to AppTheme.colors.RefColorWhite
        rank <= 10 -> AppTheme.colors.RefColorMint50 to AppTheme.colors.RefColorWhite
        rank <= 50 -> AppTheme.colors.RefColorGray60 to AppTheme.colors.RefColorWhite
        else -> AppTheme.colors.RefColorOrange50 to AppTheme.colors.RefColorWhite
    }

    Box(
        modifier = modifier
            .size(40.dp)
            .background(
                color = backgroundColor,
                shape = MaterialTheme.shapes.small
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = rank.toString(),
            style = AppTheme.styles.BodySmallB(),
            color = textColor,
            textAlign = TextAlign.Center
        )
    }
}

class HallOfFameViewModel(
    private val getTopQuizListUseCase: GetTopQuizListUseCase
) : ViewModel() {
    private val _uiState = MutableStateFlow<HallOfFameState>(HallOfFameState.Loading)
    val uiState = _uiState.asStateFlow()

    init {
        loadRankings()
    }

    private fun loadRankings() {
        viewModelScope.launch {
            try {
                getTopQuizListUseCase.invoke()
                    .collect { quizGroups ->
                        val sortedGroups = quizGroups
                            .sortedByDescending { it.likeCount }
                            .take(100)
                            .mapIndexed { index, quizGroup ->
                                RankedQuizGroup(
                                    rank = index + 1,
                                    quizGroup = quizGroup
                                )
                            }
                        _uiState.value = HallOfFameState.Success(sortedGroups)
                    }
            } catch (e: Exception) {
                _uiState.value = HallOfFameState.Error(e.message ?: "알 수 없는 오류가 발생했습니다.")
            }
        }
    }
}

data class RankedQuizGroup(
    val rank: Int,
    val quizGroup: QuizGroupData
)

sealed class HallOfFameState {
    data object Loading : HallOfFameState()
    data class Success(val rankings: List<RankedQuizGroup>) : HallOfFameState()
    data class Error(val message: String) : HallOfFameState()
}