package com.kwon.chosungmarket.presenter.page

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import com.kwon.chosungmarket.common.types.QuizDifficulty
import com.kwon.chosungmarket.domain.model.QuizData
import com.kwon.chosungmarket.domain.usecase.CreateQuizGroupUseCase
import com.kwon.chosungmarket.presenter.route.CmRouter
import com.kwon.chosungmarket.presenter.widget.KToast
import com.kwon.chosungmarket.presenter.widget.RetryQuizDialog
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
 * 퀴즈 그룹의 제목, 설명, 태그를 설정하고
 * 여러 개의 초성 퀴즈를 단계별로 추가할 수 있습니다.
 *
 * @param navController 화면 전환을 위한 네비게이션 컨트롤러
 * @param modifier 레이아웃 수정을 위한 Modifier
 * @param viewModel 퀴즈 생성 상태를 관리하는 ViewModel
 */
@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun QuizCreatePage(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    viewModel: QuizCreateViewModel = koinViewModel()
) {
    val createState by viewModel.createState.collectAsState()
    val quizzes by viewModel.quizzes.collectAsState()

    var quizGroupTitle by remember { mutableStateOf("") }
    var quizGroupDescription by remember { mutableStateOf("") }
    var showExitDialog by remember { mutableStateOf(false) }
    var currentQuizIndex by remember { mutableIntStateOf(0) }

    val focusManager = LocalFocusManager.current

    // 뒤로가기 처리
    val handleBackPress: () -> Unit = {
        focusManager.clearFocus()

        // 단계에 따라 다른 처리
        if (currentQuizIndex > 0) {
            // 퀴즈 편집 단계에서는 이전 단계로 돌아감
            currentQuizIndex--
        } else {
            // 첫 단계에서는 취소 확인 다이얼로그 표시
            if (quizGroupTitle.isNotEmpty() || quizGroupDescription.isNotEmpty() ||
                quizzes.any { it.consonant.isNotEmpty() || it.answer.isNotEmpty() || it.description.isNotEmpty() }) {
                showExitDialog = true
            } else {
                navController.popBackStack()
            }
        }
    }

    // 뒤로가기 및 X버튼 팝업 처리
    BackHandler(enabled = true) {
        handleBackPress()
    }

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

    val moveToNextQuiz: () -> Unit = {
        focusManager.clearFocus()

        // 안전하게 퀴즈 인덱스 접근
        val currentIndex = currentQuizIndex - 1
        if (currentIndex >= 0 && currentIndex < quizzes.size) {
            val currentQuiz = quizzes[currentIndex]

            // 유효성 검증
            val validationResult = validateQuiz(currentQuiz)
            if (validationResult.isSuccess) {
                // 인덱스 범위 안전하게 처리
                if (currentQuizIndex < quizzes.size) {
                    // 이미 존재하는 다음 문제로 이동
                    currentQuizIndex++
                } else {
                    // 새 퀴즈 추가 후 즉시 이동
                    viewModel.addQuizForm()
                    // 추가된 퀴즈로 바로 이동 (기존 퀴즈 수 + 1이므로 퀴즈 수와 동일)
                    currentQuizIndex++
                }
            } else {
                KToast.show(validationResult.exceptionOrNull()?.message ?: "입력 정보를 확인해주세요", ToastType.ERROR)
            }
        } else {
            // 안전한 처리: 인덱스가 유효하지 않으면 첫 번째 퀴즈로 이동
            if (quizzes.isNotEmpty()) {
                currentQuizIndex = 1
            }
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "퀴즈 만들기",
                        style = AppTheme.styles.BodySmallB()
                    )
                },
                navigationIcon = {
                    IconButton(onClick = handleBackPress) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "뒤로 가기"
                        )
                    }
                },
                actions = {
                    // 퀴즈 편집 단계에서만 작성 완료 버튼 표시
                    if (currentQuizIndex > 0 && createState != QuizCreateState.Loading) {
                        IconButton(onClick = {
                            // 모든 퀴즈 유효성 검증
                            var allValid = true
                            var firstInvalidIndex = -1

                            for (i in 0 until quizzes.size) {
                                val quiz = quizzes.getOrNull(i) ?: continue
                                val validationResult = validateQuiz(quiz)

                                if (validationResult.isFailure) {
                                    allValid = false
                                    if (firstInvalidIndex == -1) {
                                        firstInvalidIndex = i
                                        // 첫 번째 오류 메시지 표시
                                        KToast.show("문제 #${i + 1}: ${validationResult.exceptionOrNull()?.message ?: "입력 정보를 확인해주세요"}", ToastType.ERROR)
                                    }
                                }
                            }

                            // 기본 정보 검증
                            if (allValid) {
                                if (quizGroupTitle.isBlank()) {
                                    allValid = false
                                    KToast.show("퀴즈 제목을 입력해주세요", ToastType.ERROR)
                                    // 기본 정보 화면으로 이동
                                    currentQuizIndex = 0
                                } else if (quizGroupDescription.isBlank()) {
                                    allValid = false
                                    KToast.show("퀴즈 설명을 입력해주세요", ToastType.ERROR)
                                    // 기본 정보 화면으로 이동
                                    currentQuizIndex = 0
                                }
                            } else if (firstInvalidIndex >= 0) {
                                // 오류가 있는 첫 번째 퀴즈로 이동
                                currentQuizIndex = firstInvalidIndex + 1
                            }

                            // 모든 검증 통과 시 생성 처리
                            if (allValid) {
                                viewModel.createQuizGroup(quizGroupTitle, quizGroupDescription)
                            }
                        }) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = "퀴즈 생성하기",
                                tint = AppTheme.colors.RefColorBlue50
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = AppTheme.colors.RefColorWhite
                )
            )
        },
        containerColor = AppTheme.colors.RefColorWhite
    ) { paddingValues ->
        Box(
            modifier = modifier
                .fillMaxSize()
                .background(AppTheme.colors.RefColorWhite)
                .padding(paddingValues)
                .imePadding()
        ) {
            // 단계별 진행 (기본 정보 → 퀴즈 추가)
            if (currentQuizIndex == 0) {
                // 기본 정보 입력 화면
                QuizGroupInfoSection(
                    title = quizGroupTitle,
                    onTitleChange = { quizGroupTitle = it },
                    description = quizGroupDescription,
                    onDescriptionChange = { quizGroupDescription = it },
                    onNext = {
                        focusManager.clearFocus()
                        if (quizGroupTitle.isBlank()) {
                            KToast.show("퀴즈 제목을 입력해주세요", ToastType.ERROR)
                        } else if (quizGroupDescription.isBlank()) {
                            KToast.show("퀴즈 설명을 입력해주세요", ToastType.ERROR)
                        } else {
                            currentQuizIndex = 1
                        }
                    }
                )
            } else {
                // 퀴즈 추가 화면
                QuizEditSection(
                    quizIndex = currentQuizIndex - 1,
                    totalQuizzes = quizzes.size,
                    quizForm = quizzes.getOrNull(currentQuizIndex - 1) ?: QuizFormData(), // null 안전 처리
                    onQuizUpdate = {
                        // 인덱스 유효성 검사 추가
                        if (currentQuizIndex - 1 < quizzes.size) {
                            viewModel.updateQuizForm(currentQuizIndex - 1, it)
                        }
                    },
                    onDeleteQuiz = {
                        focusManager.clearFocus()

                        // 인덱스 유효성 검사 강화
                        val safeIndex = currentQuizIndex - 1
                        if (quizzes.size > 1 && safeIndex >= 0 && safeIndex < quizzes.size) {
                            // 삭제 후 남은 퀴즈 개수 가져오기
                            val remainingQuizzes = viewModel.removeQuizForm(safeIndex)

                            // 삭제 후 인덱스 안전하게 조정
                            currentQuizIndex = when {
                                // 현재 인덱스가 남은 퀴즈 개수보다 크다면 마지막 퀴즈로 조정
                                currentQuizIndex > remainingQuizzes -> remainingQuizzes
                                // 최소 1 유지 (첫 번째 퀴즈는 인덱스 1)
                                else -> Math.max(1, currentQuizIndex)
                            }
                        } else {
                            KToast.show("최소 1개의 퀴즈가 필요합니다", ToastType.INFO)
                        }
                    },
                    onMoveToNext = moveToNextQuiz,
                    isCreating = createState is QuizCreateState.Loading
                )
            }

            // 작업 취소 확인 다이얼로그
            if (showExitDialog) {
                RetryQuizDialog(
                    title = "퀴즈 만들기를 취소하시겠습니까?",
                    confirmButtonText = "취소하기",
                    onConfirm = {
                        showExitDialog = false
                        navController.popBackStack()
                    },
                    onDismiss = {
                        showExitDialog = false
                    }
                )
            }
        }
    }
}

/**
 * 퀴즈 유효성 검증 함수
 *
 * @param quizForm 검증할 퀴즈 폼 데이터
 * @return 검증 결과
 */
private fun validateQuiz(quizForm: QuizFormData): Result<Unit> {
    // 초성 검증 - 자음만 있어야 함
    val consonantPattern = Regex("^[ㄱ-ㅎ]+$")
    if (quizForm.consonant.isBlank()) {
        return Result.failure(Exception("초성을 입력해주세요"))
    } else if (!consonantPattern.matches(quizForm.consonant)) {
        return Result.failure(Exception("초성은 자음(ㄱ-ㅎ)만 입력 가능합니다"))
    }

    // 정답 검증 - 초성과 글자 수가 같아야 함
    if (quizForm.answer.isBlank()) {
        return Result.failure(Exception("정답을 입력해주세요"))
    } else if (quizForm.consonant.length != quizForm.answer.length) {
        return Result.failure(Exception("정답은 초성과 동일한 글자 수여야 합니다"))
    }

    // 초성과 정답의 자음 일치 여부 검증
    // 한글의 초성 매핑: 각 글자의 첫 자음이 초성과 일치하는지 확인
    for (i in quizForm.consonant.indices) {
        val consonant = quizForm.consonant[i]

        // 정답의 해당 위치 글자가 있는지 확인
        if (i >= quizForm.answer.length) {
            return Result.failure(Exception("정답이 초성보다 짧습니다"))
        }

        val answerChar = quizForm.answer[i]

        // 한글 자모 분리: 한글 유니코드 활용 (AC00부터 한글 시작, 초성 19개, 중성 21개)
        if (answerChar.code < 0xAC00 || answerChar.code > 0xD7A3) {
            return Result.failure(Exception("정답은 한글만 입력 가능합니다"))
        }

        // 초성 추출
        val charIndex = (answerChar.code - 0xAC00)
        val initialConsonantIndex = charIndex / (21 * 28)

        // 초성 인덱스에 따른 자음 (ㄱ=0, ㄲ=1, ㄴ=2, ...)
        val extractedConsonant = when (initialConsonantIndex) {
            0 -> 'ㄱ'
            1 -> 'ㄲ'
            2 -> 'ㄴ'
            3 -> 'ㄷ'
            4 -> 'ㄸ'
            5 -> 'ㄹ'
            6 -> 'ㅁ'
            7 -> 'ㅂ'
            8 -> 'ㅃ'
            9 -> 'ㅅ'
            10 -> 'ㅆ'
            11 -> 'ㅇ'
            12 -> 'ㅈ'
            13 -> 'ㅉ'
            14 -> 'ㅊ'
            15 -> 'ㅋ'
            16 -> 'ㅌ'
            17 -> 'ㅍ'
            18 -> 'ㅎ'
            else -> ' '
        }

        // ㄲ, ㄸ, ㅃ, ㅆ, ㅉ (쌍자음)이 있을 경우 기본 자음으로 처리 (ㄱ, ㄷ, ㅂ, ㅅ, ㅈ)
        val normalizedConsonant = when (extractedConsonant) {
            'ㄲ' -> 'ㄱ'
            'ㄸ' -> 'ㄷ'
            'ㅃ' -> 'ㅂ'
            'ㅆ' -> 'ㅅ'
            'ㅉ' -> 'ㅈ'
            else -> extractedConsonant
        }

        // 사용자가 입력한 자음도 정규화 (쌍자음 처리)
        val normalizedInputConsonant = when (consonant) {
            'ㄲ' -> 'ㄱ'
            'ㄸ' -> 'ㄷ'
            'ㅃ' -> 'ㅂ'
            'ㅆ' -> 'ㅅ'
            'ㅉ' -> 'ㅈ'
            else -> consonant
        }

        if (normalizedConsonant != normalizedInputConsonant) {
            return Result.failure(Exception("${i+1}번째 글자의 초성이 일치하지 않습니다"))
        }
    }

    // 힌트 검증 - 빈 값이 아니어야 함
    if (quizForm.description.isBlank()) {
        return Result.failure(Exception("힌트를 입력해주세요"))
    }

    return Result.success(Unit)
}

/**
 * 퀴즈 그룹 기본 정보 입력 섹션
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun QuizGroupInfoSection(
    title: String,
    onTitleChange: (String) -> Unit,
    description: String,
    onDescriptionChange: (String) -> Unit,
    onNext: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        // 안내 메시지
        Text(
            text = "새로운 초성 퀴즈를 만들어보세요!",
            style = AppTheme.styles.TitleMediumB(),
            color = AppTheme.colors.CompColorTextPrimary,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Text(
            text = "친구들과 함께 풀 수 있는 재미있는 퀴즈를 만들어 공유해보세요.",
            style = AppTheme.styles.BodySmallR(),
            color = AppTheme.colors.CompColorTextDescription,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        // 제목 입력
        Text(
            text = "퀴즈 제목",
            style = AppTheme.styles.BodySmallSB(),
            modifier = Modifier.padding(bottom = 8.dp)
        )

        OutlinedTextField(
            value = title,
            onValueChange = onTitleChange,
            placeholder = { Text("재미있는 퀴즈 제목을 입력해주세요") },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = AppTheme.colors.RefColorBlue50,
                unfocusedBorderColor = AppTheme.colors.CompColorLineSecondary
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        )

        // 설명 입력
        Text(
            text = "퀴즈 설명",
            style = AppTheme.styles.BodySmallSB(),
            modifier = Modifier.padding(bottom = 8.dp, top = 16.dp)
        )

        OutlinedTextField(
            value = description,
            onValueChange = onDescriptionChange,
            placeholder = { Text("퀴즈에 대한 설명을 입력해주세요") },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = AppTheme.colors.RefColorBlue50,
                unfocusedBorderColor = AppTheme.colors.CompColorLineSecondary
            ),
            minLines = 3,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp)
        )

        Spacer(modifier = Modifier.weight(1f))

        // 다음 버튼
        RoundedButton(
            text = "문제 만들기",
            color = AppTheme.colors.CompColorBrand,
            onClick = onNext,
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.ArrowForward,
                    contentDescription = null,
                    tint = AppTheme.colors.RefColorWhite
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp)
        )
    }
}

/**
 * 퀴즈 편집 섹션
 */
@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
private fun QuizEditSection(
    quizIndex: Int,
    totalQuizzes: Int,
    quizForm: QuizFormData,
    onQuizUpdate: (QuizFormData) -> Unit,
    onDeleteQuiz: () -> Unit,
    onMoveToNext: () -> Unit,
    isCreating: Boolean
) {
    var tagInput by remember { mutableStateOf(quizForm.tagInput) }

    // 각 필드의 오류 상태
    var consonantError by remember { mutableStateOf(false) }
    var answerError by remember { mutableStateOf(false) }
    var descriptionError by remember { mutableStateOf(false) }

    // 필드 유효성 검사
    val validateFields = {
        consonantError = quizForm.consonant.isBlank() || !Regex("^[ㄱ-ㅎ]+$").matches(quizForm.consonant)
        answerError = quizForm.answer.isBlank() || quizForm.consonant.length != quizForm.answer.length
        descriptionError = quizForm.description.isBlank()
    }

    // 다음 버튼 클릭 시 유효성 검사
    LaunchedEffect(quizForm) {
        // 필드 값이 변경되면 오류 상태 초기화
        consonantError = false
        answerError = false
        descriptionError = false
    }

    val addTag = {
        if (tagInput.isNotBlank()) {
            // 특수문자 제거 (알파벳, 숫자, 한글만 허용)
            val sanitizedTag = tagInput.replace(Regex("[^A-Za-z0-9가-힣ㄱ-ㅎㅏ-ㅣ\\s]"), "").trim()

            if (sanitizedTag.isNotBlank()) {
                val newTag = if (sanitizedTag.startsWith("#")) sanitizedTag else "#$sanitizedTag"
                if (!quizForm.tagList.contains(newTag)) {
                    onQuizUpdate(quizForm.copy(
                        tagList = quizForm.tagList + newTag,
                        tagInput = ""
                    ))
                }
            } else {
                // 특수문자만 있었던 경우 사용자에게 알림
                KToast.show("태그에는 특수문자를 사용할 수 없습니다", ToastType.ERROR)
            }
            tagInput = ""
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        // 퀴즈 번호 표시
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        ) {
            for (i in 0 until totalQuizzes) {
                Box(
                    modifier = Modifier
                        .size(if (i == quizIndex) 40.dp else 28.dp)
                        .clip(CircleShape)
                        .background(
                            if (i == quizIndex) AppTheme.colors.RefColorBlue50
                            else AppTheme.colors.RefColorGray90
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "${i + 1}",
                        style = if (i == quizIndex) AppTheme.styles.BodySmallB() else AppTheme.styles.SubSmallR(),
                        color = if (i == quizIndex) AppTheme.colors.RefColorWhite else AppTheme.colors.CompColorTextDescription
                    )
                }

                if (i < totalQuizzes - 1) {
                    Divider(
                        modifier = Modifier
                            .width(16.dp)
                            .padding(horizontal = 4.dp),
                        color = AppTheme.colors.CompColorLineSecondary.copy(alpha = 0.3f)
                    )
                }
            }
        }

        // 퀴즈 정보 안내
        Text(
            text = "퀴즈 #${quizIndex + 1}",
            style = AppTheme.styles.TitleMediumB(),
            color = AppTheme.colors.CompColorTextPrimary,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        // 초성 입력
        Text(
            text = "초성",
            style = AppTheme.styles.BodySmallSB(),
            modifier = Modifier.padding(bottom = 8.dp, top = 16.dp)
        )

        OutlinedTextField(
            value = quizForm.consonant,
            onValueChange = {
                consonantError = false // 입력 시 오류 상태 초기화
                onQuizUpdate(quizForm.copy(consonant = it))
            },
            placeholder = { Text("예: ㄱㅇㄱㅅ") },
            isError = consonantError,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = if (consonantError) AppTheme.colors.RefColorRed50 else AppTheme.colors.RefColorBlue50,
                unfocusedBorderColor = if (consonantError) AppTheme.colors.RefColorRed50 else AppTheme.colors.CompColorLineSecondary,
                errorBorderColor = AppTheme.colors.RefColorRed50
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        )

        // 정답 입력
        Text(
            text = "정답",
            style = AppTheme.styles.BodySmallSB(),
            modifier = Modifier.padding(bottom = 8.dp)
        )

        OutlinedTextField(
            value = quizForm.answer,
            onValueChange = {
                answerError = false // 입력 시 오류 상태 초기화
                onQuizUpdate(quizForm.copy(answer = it))
            },
            placeholder = { Text("예: 강아지") },
            isError = answerError,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = if (answerError) AppTheme.colors.RefColorRed50 else AppTheme.colors.RefColorBlue50,
                unfocusedBorderColor = if (answerError) AppTheme.colors.RefColorRed50 else AppTheme.colors.CompColorLineSecondary,
                errorBorderColor = AppTheme.colors.RefColorRed50
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        )

        // 힌트 입력
        Text(
            text = "힌트",
            style = AppTheme.styles.BodySmallSB(),
            modifier = Modifier.padding(bottom = 8.dp)
        )

        OutlinedTextField(
            value = quizForm.description,
            onValueChange = {
                descriptionError = false // 입력 시 오류 상태 초기화
                onQuizUpdate(quizForm.copy(description = it))
            },
            placeholder = { Text("퀴즈 힌트를 입력해주세요") },
            isError = descriptionError,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = if (descriptionError) AppTheme.colors.RefColorRed50 else AppTheme.colors.RefColorBlue50,
                unfocusedBorderColor = if (descriptionError) AppTheme.colors.RefColorRed50 else AppTheme.colors.CompColorLineSecondary,
                errorBorderColor = AppTheme.colors.RefColorRed50
            ),
            minLines = 2,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        )

        // 난이도 선택
        Text(
            text = "난이도",
            style = AppTheme.styles.BodySmallSB(),
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(bottom = 24.dp)
        ) {
            QuizDifficulty.entries.forEach { difficulty ->
                val isSelected = quizForm.difficulty == difficulty

                Card(
                    modifier = Modifier
                        .weight(1f)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                            onClick = { onQuizUpdate(quizForm.copy(difficulty = difficulty)) }
                        ),
                    colors = CardDefaults.cardColors(
                        containerColor = when {
                            isSelected && difficulty == QuizDifficulty.EASY -> AppTheme.colors.RefColorMint95
                            isSelected && difficulty == QuizDifficulty.MEDIUM -> AppTheme.colors.RefColorBlue95
                            isSelected && difficulty == QuizDifficulty.HARD -> AppTheme.colors.RefColorOrange95
                            else -> AppTheme.colors.RefColorGray95
                        }
                    ),
                    shape = RoundedCornerShape(16.dp),
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = when(difficulty) {
                                QuizDifficulty.EASY -> "쉬움"
                                QuizDifficulty.MEDIUM -> "보통"
                                QuizDifficulty.HARD -> "어려움"
                            },
                            style = AppTheme.styles.BodySmallR(),
                            color = when {
                                isSelected && difficulty == QuizDifficulty.EASY -> AppTheme.colors.RefColorMint40
                                isSelected && difficulty == QuizDifficulty.MEDIUM -> AppTheme.colors.RefColorBlue50
                                isSelected && difficulty == QuizDifficulty.HARD -> AppTheme.colors.RefColorOrange50
                                else -> AppTheme.colors.CompColorTextDescription
                            }
                        )
                    }
                }
            }
        }

        // 태그 입력
        Text(
            text = "태그 추가",
            style = AppTheme.styles.BodySmallSB(),
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp)
        ) {
            OutlinedTextField(
                value = tagInput,
                onValueChange = { tagInput = it },
                placeholder = { Text("태그 입력 (예: 영화)") },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = AppTheme.colors.RefColorBlue50,
                    unfocusedBorderColor = AppTheme.colors.CompColorLineSecondary
                ),
                singleLine = true,
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 8.dp)
            )

            IconButton(
                onClick = addTag,
                modifier = Modifier
                    .size(48.dp)
                    .background(
                        color = AppTheme.colors.RefColorBlue50,
                        shape = CircleShape
                    )
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "태그 추가",
                    tint = AppTheme.colors.RefColorWhite
                )
            }
        }

        // 태그 표시 - FlowRow로 변경하여 여러 줄로 표시
        if (quizForm.tagList.isNotEmpty()) {
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(bottom = 24.dp)
            ) {
                quizForm.tagList.forEach { tag ->
                    TagChip(
                        tag = tag,
                        onRemove = {
                            onQuizUpdate(quizForm.copy(
                                tagList = quizForm.tagList.filter { it != tag }
                            ))
                        }
                    )
                }
            }
        } else {
            // 태그가 없을 때도 동일한 여백 유지
            Spacer(modifier = Modifier.height(16.dp))
        }

        // 삭제 버튼
        Button(
            onClick = onDeleteQuiz,
            colors = ButtonDefaults.buttonColors(
                containerColor = AppTheme.colors.RefColorGray95,
                contentColor = AppTheme.colors.RefColorRed50
            ),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Delete,
                contentDescription = "삭제",
                tint = AppTheme.colors.RefColorRed50,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "이 문제 삭제하기",
                style = AppTheme.styles.BodySmallSB(),
                color = AppTheme.colors.RefColorRed50
            )
        }

        // 다음 문제 버튼
        RoundedButton(
            text = "다음 문제",
            color = AppTheme.colors.CompColorBrand,
            onClick = {
                // 먼저 유효성 검증
                val validationResult = validateQuiz(quizForm)
                if (validationResult.isSuccess) {
                    // 성공 시 바로 다음 문제로 이동
                    onMoveToNext()
                } else {
                    // 오류 메시지 표시
                    KToast.show(validationResult.exceptionOrNull()?.message ?: "입력 정보를 확인해주세요", ToastType.ERROR)

                    // 오류 상태 설정
                    validateFields()

                    // 오류에 해당하는 필드 포커스
                    val errorMessage = validationResult.exceptionOrNull()?.message ?: ""
                    when {
                        errorMessage.contains("초성") -> consonantError = true
                        errorMessage.contains("정답") || errorMessage.contains("글자") -> answerError = true
                        errorMessage.contains("힌트") -> descriptionError = true
                    }
                }
            },
            leadingIcon = {
                if (isCreating) {
                    CircularProgressIndicator(
                        color = AppTheme.colors.RefColorWhite,
                        modifier = Modifier.size(16.dp),
                        strokeWidth = 2.dp
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.ArrowForward,
                        contentDescription = null,
                        tint = AppTheme.colors.RefColorWhite
                    )
                }
            },
            isEnable = !isCreating,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        )
    }
}

/**
 * 태그 칩 컴포넌트
 */
@Composable
private fun TagChip(
    tag: String,
    onRemove: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = AppTheme.colors.RefColorBlue95
        ),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.padding(vertical = 4.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
            Text(
                text = tag,
                style = AppTheme.styles.BodySmallR(),
                color = AppTheme.colors.RefColorBlue50
            )
            Spacer(modifier = Modifier.width(4.dp))
            Box(
                modifier = Modifier
                    .size(16.dp)
                    .clickable(onClick = onRemove),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "태그 삭제",
                    tint = AppTheme.colors.RefColorBlue50,
                    modifier = Modifier.size(12.dp)
                )
            }
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
     * @return 삭제 후 남은 퀴즈 개수
     */
    fun removeQuizForm(index: Int): Int {
        val currentList = _quizzes.value.toMutableList()
        if (currentList.size > 1 && index >= 0 && index < currentList.size) {  // 인덱스 유효성 검사 추가
            currentList.removeAt(index)
            _quizzes.value = currentList
        }
        return _quizzes.value.size
    }

    /**
     * 퀴즈 그룹을 생성합니다.
     * 입력값 검증 후 실제 생성을 수행합니다.
     *
     * @param title 퀴즈 그룹 제목
     * @param description 퀴즈 그룹 설명
     */
    fun createQuizGroup(title: String, description: String) {
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
                if (quizzes.any { it.consonant.isBlank() || it.answer.isBlank() || it.description.isBlank() }) {
                    _createState.value = QuizCreateState.Error("모든 퀴즈의 초성, 정답, 힌트를 입력해주세요")
                    return@launch
                }

                // 모든 퀴즈마다 유효성 검증
                quizzes.forEachIndexed { index, quiz ->
                    val validationResult = validateQuiz(quiz)
                    if (validationResult.isFailure) {
                        _createState.value = QuizCreateState.Error("문제 #${index + 1}: ${validationResult.exceptionOrNull()?.message}")
                        return@launch
                    }
                }

                // 각 퀴즈의 태그를 모아서 중복 제거
                val allTags = quizzes
                    .flatMap { it.tagList }
                    .distinct()

                // 퀴즈 데이터 생성
                val quizDataList = quizzes.map { form ->
                    QuizData(
                        id = UUID.randomUUID().toString(),
                        consonant = form.consonant,
                        answer = form.answer,
                        description = form.description,
                        tagList = form.tagList,
                        difficulty = form.difficulty
                    )
                }

                // 대표 난이도는 가장 많이 선택된 난이도로 설정 (기본값은 MEDIUM)
                val representativeDifficulty = quizzes
                    .groupBy { it.difficulty }
                    .maxByOrNull { it.value.size }
                    ?.key ?: QuizDifficulty.MEDIUM

                createQuizGroupUseCase.invoke(
                    title,
                    description,
                    quizDataList,
                    representativeDifficulty,
                    image = "",
                    tags = allTags
                ).onSuccess {
                    _createState.value = QuizCreateState.Success
                }.onFailure { error ->
                    _createState.value = QuizCreateState.Error(error.message ?: "퀴즈 생성에 실패했어요")
                }

            } catch (e: Exception) {
                _createState.value = QuizCreateState.Error(e.message ?: "알 수 없는 오류가 발생했어요")
            }
        }
    }
}

/**
 * 퀴즈 폼의 데이터를 담는 데이터 클래스
 *
 * @param consonant 초성 문제
 * @param answer 정답
 * @param description 힌트 또는 설명
 * @param tagList 태그 목록
 * @param tagInput 태그 입력 필드 값
 * @param difficulty 난이도
 */
data class QuizFormData(
    val consonant: String = "",
    val answer: String = "",
    val description: String = "",
    val tagList: List<String> = emptyList(),
    val tagInput: String = "",
    val difficulty: QuizDifficulty = QuizDifficulty.MEDIUM
)

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