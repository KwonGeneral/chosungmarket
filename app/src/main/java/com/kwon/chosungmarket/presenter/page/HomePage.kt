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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.SecondaryIndicator
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import com.kwon.chosungmarket.common.types.QuizSortOption
import com.kwon.chosungmarket.common.types.QuizTags
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
 *
 * @param navController 화면 전환을 위한 네비게이션 컨트롤러
 * @param modifier 레이아웃 수정을 위한 Modifier
 * @param viewModel 화면의 상태를 관리하는 ViewModel
 */
@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
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
            .background(AppTheme.colors.RefColorWhite)
            .pullRefresh(pullRefreshState)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // 상단 헤더
            TopAppBar(
                title = {
                    Text(
                        text = "홈",
                        style = AppTheme.styles.TitleMediumB(),
                        color = AppTheme.colors.CompColorTextPrimary
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = AppTheme.colors.RefColorWhite
                )
            )

            when (val state = homeState) {
                is HomeState.Loading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = AppTheme.colors.RefColorBlue50)
                    }
                }
                is HomeState.Success -> {
                    // 카테고리 탭
                    ScrollableTabRow(
                        selectedTabIndex = QuizTags.mainTags.indexOf(state.selectedTag),
                        edgePadding = 16.dp,
                        indicator = { tabPositions ->
                            SecondaryIndicator(
                                modifier = Modifier.tabIndicatorOffset(tabPositions[QuizTags.mainTags.indexOf(state.selectedTag)]),
                                height = 2.dp,
                                color = AppTheme.colors.RefColorBlue50
                            )
                        },
                        containerColor = AppTheme.colors.RefColorWhite,
                        divider = {
                            HorizontalDivider(color = AppTheme.colors.CompColorLineSecondary)
                        }
                    ) {
                        QuizTags.mainTags.forEach { tag ->
                            Tab(
                                selected = state.selectedTag == tag,
                                onClick = { viewModel.updateSelectedTag(tag) },
                                text = {
                                    Text(
                                        text = tag,
                                        style = AppTheme.styles.SubMediumR(),
                                        color = if (state.selectedTag == tag)
                                            AppTheme.colors.CompColorTextPrimary
                                        else
                                            AppTheme.colors.CompColorTextDescription
                                    )
                                }
                            )
                        }
                    }

                    // 필터 영역
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        FilterDropdown(
                            selected = state.sortOption,
                            onOptionSelected = viewModel::updateSortOption
                        )
                    }

                    // 퀴즈 그룹 목록
                    LazyColumn(
                        modifier = Modifier.fillMaxWidth(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(state.quizGroups) { quizGroup ->
                            QuizGroupItem(
                                quizGroup = quizGroup,
                                onClick = {
                                    navController.navigate(CmRouter.QuizDetail.createRoute(quizGroup.id))
                                }
                            )
                        }
                    }
                }
                is HomeState.Error -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(state.message)
                    }
                }
            }
        }

        // FAB
        FloatingActionButton(
            onClick = { navController.navigate(CmRouter.QuizCreate.route) },
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
            modifier = Modifier.align(Alignment.TopCenter),
            backgroundColor = AppTheme.colors.RefColorWhite,
            contentColor = AppTheme.colors.RefColorBlue50
        )
    }
}

/**
 * 필터 옵션을 선택하는 Dropdown을 표시하는 Composable
 */
@Composable
private fun FilterDropdown(
    selected: QuizSortOption,
    onOptionSelected: (QuizSortOption) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Box {
        Row(
            modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .background(AppTheme.colors.RefColorGray95)
                .clickable { expanded = true }
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = when (selected) {
                    QuizSortOption.RECOMMENDED -> "추천순"
                    QuizSortOption.NEWEST -> "최신순"
                    QuizSortOption.OLDEST -> "오래된순"
                },
                style = AppTheme.styles.SubMediumR(),
                color = AppTheme.colors.CompColorTextPrimary
            )
            Icon(
                imageVector = Icons.Default.ArrowDropDown,
                contentDescription = null,
                tint = AppTheme.colors.CompColorIconLabelPrimary
            )
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            QuizSortOption.values().forEach { option ->
                DropdownMenuItem(
                    text = {
                        Text(
                            text = when (option) {
                                QuizSortOption.RECOMMENDED -> "추천순"
                                QuizSortOption.NEWEST -> "최신순"
                                QuizSortOption.OLDEST -> "오래된순"
                            },
                            style = AppTheme.styles.SubMediumR()
                        )
                    },
                    onClick = {
                        onOptionSelected(option)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
private fun QuizGroupItem(
    quizGroup: QuizGroupData,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = AppTheme.colors.RefColorWhite
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
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
                    text = "풀이 ${quizGroup.quizResultCount}회",
                    style = AppTheme.styles.SubSmallR(),
                    color = AppTheme.colors.CompColorTextDescription
                )
                Text(
                    text = "by ${quizGroup.userNickname}",
                    style = AppTheme.styles.SubSmallR(),
                    color = AppTheme.colors.CompColorTextDescription
                )
            }

            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                quizGroup.tagList.forEach { tag ->
                    Surface(
                        modifier = Modifier.height(24.dp),
                        shape = RoundedCornerShape(12.dp),
                        color = AppTheme.colors.RefColorGray95
                    ) {
                        Text(
                            text = tag,
                            style = AppTheme.styles.SubSmallR(),
                            color = AppTheme.colors.CompColorTextDescription,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
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

    fun refresh() {
        _isRefreshing.value = true
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            try {
                val quizGroups = getQuizGroupListUseCase.invoke(10).first()
                val userData = getCurrentUserInfoUseCase.invoke().first()

                _homeState.value = HomeState.Success(
                    user = userData,
                    quizGroups = quizGroups,
                    selectedTag = QuizTags.ALL,
                    sortOption = QuizSortOption.RECOMMENDED,
                    mainTags = QuizTags.mainTags
                )
            } catch (e: Exception) {
                _homeState.value = HomeState.Error(e.message ?: "알 수 없는 오류")
            } finally {
                _isRefreshing.value = false
            }
        }
    }

    fun updateSelectedTag(tag: String) {
        val currentState = _homeState.value
        if (currentState is HomeState.Success) {
            _homeState.value = currentState.copy(selectedTag = tag)
            filterAndSortQuizGroups()
        }
    }

    fun updateSortOption(option: QuizSortOption) {
        val currentState = _homeState.value
        if (currentState is HomeState.Success) {
            _homeState.value = currentState.copy(sortOption = option)
            filterAndSortQuizGroups()
        }
    }

    private fun filterAndSortQuizGroups() {
        viewModelScope.launch {
            val currentState = _homeState.value as? HomeState.Success ?: return@launch
            val allQuizGroups = getQuizGroupListUseCase.invoke(10).first()

            val filteredGroups = if (currentState.selectedTag == QuizTags.ALL) {
                allQuizGroups
            } else {
                allQuizGroups.filter { it.tagList.contains(currentState.selectedTag) }
            }

            val sortedGroups = when (currentState.sortOption) {
                QuizSortOption.RECOMMENDED -> filteredGroups.sortedByDescending { it.likeCount }
                QuizSortOption.NEWEST -> filteredGroups.sortedByDescending { it.createdAt }
                QuizSortOption.OLDEST -> filteredGroups.sortedBy { it.createdAt }
            }

            _homeState.value = currentState.copy(quizGroups = sortedGroups)
        }
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
     * @param mainTags 메인 태그 목록
     * @param selectedTag 선택된 태그
     * @param sortOption 선택된 정렬 옵션
     */
    data class Success(
        val user: UserData?,
        val quizGroups: List<QuizGroupData>,
        val mainTags: List<String>,
        val selectedTag: String = "전체",
        val sortOption: QuizSortOption = QuizSortOption.RECOMMENDED
    ) : HomeState()

    /**
     * 에러 상태
     *
     * @param message 에러 메시지
     */
    data class Error(val message: String) : HomeState()
}