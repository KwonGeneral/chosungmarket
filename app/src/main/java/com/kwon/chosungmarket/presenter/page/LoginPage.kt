package com.kwon.chosungmarket.presenter.page

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.user.UserApiClient
import com.kwon.chosungmarket.R
import com.kwon.chosungmarket.domain.usecase.SignInWithKakaoUseCase
import com.kwon.chosungmarket.presenter.route.CmRouter
import com.kwon.chosungmarket.presenter.route.navigateTo
import com.kwon.chosungmarket.presenter.widget.FriendlyBody
import com.kwon.chosungmarket.presenter.widget.FriendlyTitle
import com.kwon.chosungmarket.ui.theme.AppTheme
import com.kwon.chosungmarket.ui.theme.ChosungmarketTheme
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

/**
 * 로그인 화면을 구성하는 Composable
 * 카카오 소셜 로그인 기능을 제공합니다.
 * 로그인 성공 시 홈 화면으로 자동 이동합니다.
 *
 * @param navController 화면 전환을 위한 네비게이션 컨트롤러
 * @param modifier 레이아웃 수정을 위한 Modifier
 * @param loginViewModel 로그인 상태를 관리하는 ViewModel
 */
@Composable
fun LoginPage(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    loginViewModel: LoginPageViewModel = koinViewModel()
) {
    val loginState by loginViewModel.loginState.collectAsState()

    // 로그인 상태 변경 감지 및 홈 화면으로 이동
    LaunchedEffect(loginState) {
        if (loginState is LoginState.Success) {
            navController.navigateTo(CmRouter.Home.route)
        }
    }

    val context = LocalContext.current
    LoginContent(
        modifier = modifier,
        loginState = loginState,
        onLoginClick = {
            loginViewModel.signInWithKakao(context)
        }
    )
}

/**
 * 로그인 화면의 콘텐츠를 구성하는 Composable
 * 앱 로고, 환영 메시지, 카카오 로그인 버튼을 표시합니다.
 *
 * @param loginState 현재 로그인 상태
 * @param onLoginClick 로그인 버튼 클릭 시 호출될 콜백
 */
@Composable
private fun LoginContent(
    modifier: Modifier = Modifier,
    loginState: LoginState,
    onLoginClick: () -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(AppTheme.colors.RefColorWhite)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.Center)
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.app_logo),
                contentDescription = "앱 로고",
                modifier = Modifier.size(120.dp)
            )

            FriendlyTitle(
                text = "초성 마켓에 오신 것을\n환영합니다!",
                alignment = androidx.compose.ui.text.style.TextAlign.Center,
                modifier = Modifier.padding(top = 24.dp)
            )

            FriendlyBody(
                text = "카카오 로그인으로 시작해보세요",
                alignment = androidx.compose.ui.text.style.TextAlign.Center,
                modifier = Modifier.padding(bottom = 48.dp)
            )

            Image(
                painter = painterResource(id = R.drawable.kakao_login_large_wide),
                contentDescription = "카카오 로그인",
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(7.5f)
                    .clickable(onClick = onLoginClick),
                contentScale = ContentScale.FillWidth
            )
        }

        if (loginState is LoginState.Loading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(AppTheme.colors.RefColorTransparentBlack20),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    color = AppTheme.colors.RefColorBlue50
                )
            }
        }

        if (loginState is LoginState.Error) {
            FriendlyBody(
                text = (loginState as LoginState.Error).message,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 16.dp)
            )
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun LoginPagePreview() {
    ChosungmarketTheme {
        LoginContent(
            loginState = LoginState.Initial,
            onLoginClick = {}
        )
    }
}

/**
 * 로그인 화면의 상태를 관리하는 ViewModel
 * 카카오 로그인 프로세스를 처리하고 결과를 관리합니다.
 *
 * @param signInWithKakaoUseCase 카카오 로그인을 처리하는 UseCase
 */
class LoginPageViewModel(
    private val signInWithKakaoUseCase: SignInWithKakaoUseCase
) : ViewModel() {
    private val _loginState = MutableStateFlow<LoginState>(LoginState.Initial)
    val loginState = _loginState.asStateFlow()

    /**
     * 카카오 로그인을 시도합니다.
     * 카카오톡 앱이 설치되어 있으면 앱을 통해 로그인하고,
     * 없으면 웹 브라우저를 통해 로그인합니다.
     *
     * @param context 안드로이드 Context
     */
    fun signInWithKakao(context: Context) {
        viewModelScope.launch {
            try {
                _loginState.value = LoginState.Loading

                if (UserApiClient.instance.isKakaoTalkLoginAvailable(context)) {
                    handleKakaoLogin() { client, callback ->
                        client.loginWithKakaoTalk(context) { token, error ->
                            callback(token, error)
                        }
                    }
                } else {
                    handleKakaoLogin() { client, callback ->
                        client.loginWithKakaoAccount(context) { token, error ->
                            callback(token, error)
                        }
                    }
                }
            } catch (e: Exception) {
                _loginState.value = LoginState.Error(e.message ?: "로그인에 실패했어요")
            }
        }
    }

    /**
     * 카카오 로그인 프로세스를 처리하는 내부 메서드
     * 로그인 방식(카카오톡/웹)에 따라 적절한 로그인 흐름을 처리합니다.
     *
     * @param loginMethod 실제 로그인을 수행할 메서드
     */
    private suspend fun handleKakaoLogin(
        loginMethod: (UserApiClient, (OAuthToken?, Throwable?) -> Unit) -> Unit
    ) {
        val token = suspendCoroutine { continuation ->
            loginMethod(UserApiClient.instance) { token, error ->
                if (error != null) {
                    continuation.resumeWithException(error)
                } else if (token != null) {
                    continuation.resume(token)
                } else {
                    continuation.resumeWithException(Exception("토큰이 없어요"))
                }
            }
        }

        val user = suspendCoroutine { continuation ->
            UserApiClient.instance.me { user, error ->
                if (error != null) {
                    continuation.resumeWithException(error)
                } else if (user != null) {
                    continuation.resume(user)
                } else {
                    continuation.resumeWithException(Exception("사용자 정보가 없어요"))
                }
            }
        }

        signInWithKakaoUseCase.invoke(user.id.toString(), true)
            .onSuccess {
                _loginState.value = LoginState.Success
            }
            .onFailure { error ->
                _loginState.value = LoginState.Error(error.message ?: "로그인에 실패했어요")
            }
    }
}

/**
 * 로그인 화면의 상태를 나타내는 sealed class
 */
sealed class LoginState {
    /** 초기 상태 */
    data object Initial : LoginState()

    /** 로그인 진행 중 상태 */
    data object Loading : LoginState()

    /** 로그인 성공 상태 */
    data object Success : LoginState()

    /**
     * 로그인 실패 상태
     *
     * @param message 실패 사유 메시지
     */
    data class Error(val message: String) : LoginState()
}
