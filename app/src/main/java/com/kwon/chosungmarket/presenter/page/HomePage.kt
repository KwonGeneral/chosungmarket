package com.kwon.chosungmarket.presenter.page

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import com.kwon.chosungmarket.domain.model.QuizGroupData
import com.kwon.chosungmarket.domain.model.UserData
import com.kwon.chosungmarket.domain.usecase.GetCurrentUserInfoUseCase
import com.kwon.chosungmarket.domain.usecase.GetQuizGroupListUseCase
import com.kwon.chosungmarket.presenter.route.CmRouter
import com.kwon.chosungmarket.presenter.route.navigateTo
import com.kwon.chosungmarket.presenter.widget.FriendlyBody
import com.kwon.chosungmarket.presenter.widget.FriendlyTitle
import com.kwon.chosungmarket.presenter.widget.RoundedCard
import com.kwon.chosungmarket.ui.theme.AppTheme
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

/**
 * 앱의 메인 화면을 구성하는 Composable
 * 사용자가 생성한 퀴즈 그룹 목록을 표시하고, 새로운 퀴즈 생성 기능을 제공합니다.
 *
 * @param navController 화면 전환을 위한 네비게이션 컨트롤러
 * @param modifier 레이아웃 수정을 위한 Modifier
 * @param viewModel 화면의 상태를 관리하는 ViewModel
 */
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun HomePage(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    viewModel: HomePageViewModel = koinViewModel()
) {
    val homeState by viewModel.homeState.collectAsState()
    val isRefreshing by viewModel.isRefreshing.collectAsState()

    val pullRefreshState = rememberPullRefreshState(
        refreshing = isRefreshing,
        onRefresh = { viewModel.refresh() }
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .pullRefresh(pullRefreshState)
            .background(AppTheme.colors.CompColorPageDefaultBackground)
    ) {
        when (val state = homeState) {
            is HomeState.Loading -> {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                    color = AppTheme.colors.RefColorBlue50
                )
            }
            is HomeState.Success -> {
                HomeContent(
                    quizGroups = state.quizGroups,
                    onQuizGroupClick = { quizGroupId ->
//                        navController.navigate(CmRouter.QuizDetail.createRoute(quizGroupId)) {
//                            popUpTo(CmRouter.Home.route) {
//                                inclusive = true
//                            }
//                        }
                        navController.navigateTo(CmRouter.QuizDetail.createRoute(quizGroupId))
                    },
                )
            }
            is HomeState.Error -> {
                FriendlyBody(
                    text = state.message,
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(16.dp)
                )
            }
        }

        // FAB for creating new quiz
        FloatingActionButton(
            onClick = {
                navController.navigate(CmRouter.QuizCreate.route)
            },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp),
            containerColor = AppTheme.colors.CompColorBrand
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "퀴즈 만들기",
                tint = AppTheme.colors.RefColorWhite
            )
        }

        PullRefreshIndicator(
            refreshing = isRefreshing,
            state = pullRefreshState,
            modifier = Modifier.align(Alignment.TopCenter)
        )
    }
}

/**
 * 홈 화면의 메인 콘텐츠를 구성하는 Composable
 * 퀴즈 그룹 목록을 LazyColumn으로 표시합니다.
 *
 * @param quizGroups 표시할 퀴즈 그룹 목록
 * @param onQuizGroupClick 퀴즈 그룹 클릭 시 호출될 콜백
 */
@Composable
private fun HomeContent(
    quizGroups: List<QuizGroupData>,
    onQuizGroupClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item {
            FriendlyTitle(
                text = "초성 퀴즈",
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }

        items(quizGroups) { quizGroup ->
            QuizGroupCard(
                quizGroup = quizGroup,
                onClick = { onQuizGroupClick(quizGroup.id) }
            )
        }
    }
}

/**
 * 개별 퀴즈 그룹을 카드 형태로 표시하는 Composable
 * 퀴즈 제목, 설명, 작성자, 좋아요 수 등을 표시합니다.
 *
 * @param quizGroup 표시할 퀴즈 그룹 데이터
 * @param onClick 카드 클릭 시 호출될 콜백
 */
@Composable
private fun QuizGroupCard(
    quizGroup: QuizGroupData,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    RoundedCard(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = quizGroup.title,
                style = AppTheme.styles.BodySmallB(),
                color = AppTheme.colors.CompColorTextPrimary
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = quizGroup.description,
                style = AppTheme.styles.SubMediumR(),
                color = AppTheme.colors.CompColorTextDescription,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "by ${quizGroup.userNickname.isEmpty().let { if (it) "익명" else quizGroup.userNickname }}",
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

/**
 * 홈 화면의 상태를 관리하는 ViewModel
 * 퀴즈 그룹 목록 조회와 새로고침 기능을 제공합니다.
 *
 * @param getQuizGroupListUseCase 퀴즈 그룹 목록을 조회하는 UseCase
 * @param getCurrentUserInfoUseCase 현재 사용자 정보를 조회하는 UseCase
 */
class HomePageViewModel(
    private val getQuizGroupListUseCase: GetQuizGroupListUseCase,
    private val getCurrentUserInfoUseCase: GetCurrentUserInfoUseCase
) : ViewModel() {
    private val _homeState = MutableStateFlow<HomeState>(HomeState.Loading)
    val homeState = _homeState.asStateFlow()

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing = _isRefreshing.asStateFlow()

    init {
        loadData()
    }

    /** 퀴즈 그룹 목록과 사용자 정보를 로드합니다. */
    private fun loadData() {
        viewModelScope.launch {
            try {
                _homeState.value = HomeState.Loading
                val quizGroups = getQuizGroupListUseCase.invoke(10).first()
                val userData = getCurrentUserInfoUseCase.invoke().first()

                _homeState.value = HomeState.Success(
                    user = userData,
                    quizGroups = quizGroups.sortedByDescending { it.createdAt }
                )
            } catch (e: Exception) {
                _homeState.value = HomeState.Error(e.message ?: "알 수 없는 오류")
            } finally {
                _isRefreshing.value = false
            }
        }
    }

    /**
     * 데이터를 새로고침합니다.
     * Pull-to-refresh 동작 시 호출됩니다.
     */
    fun refresh() {
        _isRefreshing.value = true
        loadData()
    }
}

/**
 * 홈 화면의 상태를 나타내는 sealed class
 */
sealed class HomeState {
    /** 데이터 로딩 중 상태 */
    data object Loading : HomeState()

    /**
     * 데이터 로드 성공 상태
     *
     * @param user 현재 로그인한 사용자 정보
     * @param quizGroups 로드된 퀴즈 그룹 목록
     */
    data class Success(
        val user: UserData?,
        val quizGroups: List<QuizGroupData>
    ) : HomeState()

    /**
     * 에러 상태
     *
     * @param message 에러 메시지
     */
    data class Error(val message: String) : HomeState()
}