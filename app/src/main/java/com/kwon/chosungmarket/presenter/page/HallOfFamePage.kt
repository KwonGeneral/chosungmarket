package com.kwon.chosungmarket.presenter.page

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.kwon.chosungmarket.presenter.widget.FriendlyBody
import com.kwon.chosungmarket.presenter.widget.FriendlyTitle
import com.kwon.chosungmarket.presenter.widget.RoundedCard
import com.kwon.chosungmarket.ui.theme.AppTheme
import org.koin.androidx.compose.koinViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import coil.compose.AsyncImage
import com.kwon.chosungmarket.R
import com.kwon.chosungmarket.domain.model.QuizGroupData
import com.kwon.chosungmarket.domain.model.UserData
import com.kwon.chosungmarket.domain.usecase.GetCurrentUserInfoUseCase
import com.kwon.chosungmarket.domain.usecase.GetTopQuizListUseCase
import com.kwon.chosungmarket.domain.usecase.GetTopUsersUseCase
import com.kwon.chosungmarket.presenter.route.CmRouter
import com.kwon.chosungmarket.presenter.route.navigateTo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
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
    val selectedTab by viewModel.selectedTab.collectAsState()
    val currentUser by viewModel.currentUser.collectAsState()

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(AppTheme.colors.RefColorWhite)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // TOP 100 헤더
            Text(
                text = "TOP 100",
                style = AppTheme.styles.TitleLargeEB(),
                fontSize = 32.sp,
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 24.dp)
            )

            // 탭 선택
            TabRow(
                selectedTabIndex = when(selectedTab) {
                    HallOfFameTab.USER -> 0
                    HallOfFameTab.QUIZ -> 1
                },
                containerColor = AppTheme.colors.RefColorWhite,
                contentColor = AppTheme.colors.CompColorBrand,
                divider = {
                    HorizontalDivider(thickness = 1.dp, color = AppTheme.colors.CompColorLineSecondary)
                }
            ) {
                Tab(
                    selected = selectedTab == HallOfFameTab.USER,
                    onClick = { viewModel.setTab(HallOfFameTab.USER) },
                    text = { Text("유저 랭킹") }
                )
                Tab(
                    selected = selectedTab == HallOfFameTab.QUIZ,
                    onClick = { viewModel.setTab(HallOfFameTab.QUIZ) },
                    text = { Text("퀴즈 랭킹") }
                )
            }

            when (val state = uiState) {
                is HallOfFameState.Loading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            color = AppTheme.colors.RefColorBlue50
                        )
                    }
                }
                is HallOfFameState.Success -> {
                    RankingsList(
                        uiState = state,
                        selectedTab = selectedTab,
                        modifier = Modifier.weight(1f)
                    )
                }
                is HallOfFameState.Error -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = state.message,
                            style = AppTheme.styles.BodySmallR(),
                            color = AppTheme.colors.CompColorTextDescription
                        )
                    }
                }
            }
        }

        // 하단 내 정보 (유저 랭킹 탭에서만 표시)
        if (selectedTab == HallOfFameTab.USER) {
            currentUser?.let { user ->
                Column(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .fillMaxWidth()
                ) {
                    HorizontalDivider(
                        color = AppTheme.colors.CompColorLineSecondary
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(AppTheme.colors.RefColorWhite)
                            .padding(20.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            AsyncImage(
                                model = user.image,
                                contentDescription = "프로필",
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape),
                                contentScale = ContentScale.Crop,
                                error = painterResource(id = R.drawable.app_logo)
                            )
                            Column {
                                Text(
                                    text = user.nickname,
                                    style = AppTheme.styles.BodySmallB()
                                )
                                viewModel.getMyRank()?.let { rank ->
                                    Text(
                                        text = "${rank}등",
                                        style = AppTheme.styles.SubSmallR(),
                                        color = AppTheme.colors.CompColorTextDescription
                                    )
                                }
                            }
                        }
                        Text(
                            text = "${user.point}점",
                            style = AppTheme.styles.BodySmallB(),
                            color = AppTheme.colors.CompColorTextDescription
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun RankingsList(
    uiState: HallOfFameState.Success,
    selectedTab: HallOfFameTab,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp),
        contentPadding = PaddingValues(vertical = 20.dp)
    ) {
        when (selectedTab) {
            HallOfFameTab.USER -> {
                items(uiState.userRankings) { rankedUser ->
                    UserRankingItem(rankedUser = rankedUser)
                }
            }
            HallOfFameTab.QUIZ -> {
                items(uiState.quizRankings) { rankedQuiz ->
                    QuizRankingItem(rankedQuiz = rankedQuiz)
                }
            }
        }
        item { Spacer(Modifier.height(60.dp)) }
    }
}

@Composable
private fun UserRankingItem(
    rankedUser: RankedUser,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        RankBadge(rankedUser.rank)

        AsyncImage(
            model = rankedUser.userData.image,
            contentDescription = "프로필",
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape),
            contentScale = ContentScale.Crop,
            error = painterResource(id = R.drawable.app_logo)
        )

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = rankedUser.userData.nickname,
                style = AppTheme.styles.BodySmallB()
            )
            Text(
                text = "${rankedUser.userData.point}점",
                style = AppTheme.styles.SubSmallR(),
                color = AppTheme.colors.CompColorTextDescription
            )
        }
    }
}

@Composable
private fun QuizRankingItem(
    rankedQuiz: RankedQuizGroup,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        RankBadge(rankedQuiz.rank)

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            // 추천수
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Box(
                    modifier = Modifier
                        .background(
                            color = AppTheme.colors.RefColorRed95,
                            shape = RoundedCornerShape(12.dp)
                        )
                        .padding(horizontal = 8.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = "❤️ ${rankedQuiz.quizGroup.likeCount}",
                        style = AppTheme.styles.SubSmallR(),
                        color = AppTheme.colors.RefColorRed50
                    )
                }
            }

            // 퀴즈 제목
            Text(
                text = rankedQuiz.quizGroup.title,
                style = AppTheme.styles.BodySmallB(),
                color = AppTheme.colors.CompColorTextPrimary
            )

            // 작성자와 풀이 횟수
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "by ${rankedQuiz.quizGroup.userNickname}",
                    style = AppTheme.styles.SubSmallR(),
                    color = AppTheme.colors.CompColorTextDescription
                )

                Box(
                    modifier = Modifier
                        .background(
                            color = AppTheme.colors.RefColorBlue95,
                            shape = RoundedCornerShape(12.dp)
                        )
                        .padding(horizontal = 8.dp, vertical = 2.dp)
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(2.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "도전 횟수는.. ",
                            style = AppTheme.styles.SubSmallR(),
                            color = AppTheme.colors.RefColorGray70
                        )
                        Text(
                            text = "${rankedQuiz.quizGroup.quizResultCount}",
                            style = AppTheme.styles.SubSmallR(),
                            color = AppTheme.colors.RefColorBlue50
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun RankBadge(rank: Int) {
    when (rank) {
        1 -> Text("🥇", fontSize = 24.sp)
        2 -> Text("🥈", fontSize = 24.sp)
        3 -> Text("🥉", fontSize = 24.sp)
        else -> Text(
            text = rank.toString(),
            style = AppTheme.styles.BodySmallB(),
            modifier = Modifier.width(24.dp)
        )
    }
}

enum class HallOfFameTab {
    USER, QUIZ
}

class HallOfFameViewModel(
    private val getTopQuizListUseCase: GetTopQuizListUseCase,
    private val getCurrentUserInfoUseCase: GetCurrentUserInfoUseCase,
    private val getTopUsersUseCase: GetTopUsersUseCase
) : ViewModel() {
    private val _uiState = MutableStateFlow<HallOfFameState>(HallOfFameState.Loading)
    val uiState = _uiState.asStateFlow()

    private val _selectedTab = MutableStateFlow(HallOfFameTab.USER)
    val selectedTab = _selectedTab.asStateFlow()

    private val _currentUser = MutableStateFlow<UserData?>(null)
    val currentUser = _currentUser.asStateFlow()

    init {
        loadCurrentUser()
        loadRankings()
    }

    private fun loadCurrentUser() {
        viewModelScope.launch {
            _currentUser.value = getCurrentUserInfoUseCase.invoke().first()
        }
    }

    fun setTab(tab: HallOfFameTab) {
        _selectedTab.value = tab
        loadRankings()
    }

    fun getMyRank(): Int? {
        val currentUserId = _currentUser.value?.id ?: return null
        val state = _uiState.value as? HallOfFameState.Success ?: return null

        return when (_selectedTab.value) {
            HallOfFameTab.USER -> {
                state.userRankings.find { it.userData.id == currentUserId }?.rank
            }
            HallOfFameTab.QUIZ -> {
                state.quizRankings.find { it.quizGroup.userId == currentUserId }?.rank
            }
        }
    }

    private fun loadRankings() {
        viewModelScope.launch {
            try {
                _uiState.value = HallOfFameState.Loading

                when (_selectedTab.value) {
                    HallOfFameTab.USER -> {
                        getTopUsersUseCase.invoke().collect { users ->
                            val rankings = users.mapIndexed { index, user ->
                                RankedUser(rank = index + 1, userData = user)
                            }
                            _uiState.value = HallOfFameState.Success(userRankings = rankings)
                        }
                    }
                    HallOfFameTab.QUIZ -> {
                        getTopQuizListUseCase.invoke().collect { quizGroups ->
                            val rankings = quizGroups.mapIndexed { index, quizGroup ->
                                RankedQuizGroup(rank = index + 1, quizGroup = quizGroup)
                            }
                            _uiState.value = HallOfFameState.Success(quizRankings = rankings)
                        }
                    }
                }
            } catch (e: Exception) {
                _uiState.value = HallOfFameState.Error(e.message ?: "알 수 없는 오류가 발생했습니다.")
            }
        }
    }
}

sealed class HallOfFameState {
    data object Loading : HallOfFameState()
    data class Success(
        val userRankings: List<RankedUser> = emptyList(),
        val quizRankings: List<RankedQuizGroup> = emptyList()
    ) : HallOfFameState()
    data class Error(val message: String) : HallOfFameState()
}

data class RankedUser(
    val rank: Int,
    val userData: UserData
)

data class RankedQuizGroup(
    val rank: Int,
    val quizGroup: QuizGroupData
)