package com.kwon.chosungmarket.presenter.page

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.user.UserApiClient
import com.kwon.chosungmarket.R
import com.kwon.chosungmarket.domain.usecase.SignInWithKakaoUseCase
import com.kwon.chosungmarket.presenter.route.CmRouter
import com.kwon.chosungmarket.presenter.route.NavigationViewModel
import com.kwon.chosungmarket.presenter.route.navigateTo
import com.kwon.chosungmarket.presenter.widget.FriendlyBody
import com.kwon.chosungmarket.presenter.widget.FriendlyTitle
import com.kwon.chosungmarket.presenter.widget.RoundedButton
import com.kwon.chosungmarket.ui.theme.AppTheme
import com.kwon.chosungmarket.ui.theme.ChosungmarketTheme
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

@Composable
fun LoginPage(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    navigationViewModel: NavigationViewModel = koinViewModel(),
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
                    .aspectRatio(7.5f)  // 카카오 로그인 버튼의 가로:세로 비율
                    .clickable(onClick = onLoginClick),
                contentScale = ContentScale.FillWidth  // 가로 기준으로 비율 유지
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

class LoginPageViewModel(
    private val signInWithKakaoUseCase: SignInWithKakaoUseCase
) : ViewModel() {
    private val _loginState = MutableStateFlow<LoginState>(LoginState.Initial)
    val loginState = _loginState.asStateFlow()

    fun signInWithKakao(context: Context) {
        viewModelScope.launch {
            try {
                _loginState.value = LoginState.Loading

                if (UserApiClient.instance.isKakaoTalkLoginAvailable(context)) {
                    handleKakaoLogin(context) { client, callback ->
                        client.loginWithKakaoTalk(context) { token, error ->
                            callback(token, error)
                        }
                    }
                } else {
                    handleKakaoLogin(context) { client, callback ->
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

    private suspend fun handleKakaoLogin(
        context: Context,
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

sealed class LoginState {
    data object Initial : LoginState()
    data object Loading : LoginState()
    data object Success : LoginState()
    data class Error(val message: String) : LoginState()
}
