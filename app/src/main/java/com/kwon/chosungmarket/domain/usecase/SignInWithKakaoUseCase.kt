package com.kwon.chosungmarket.domain.usecase

import com.kwon.chosungmarket.common.utils.KLog
import com.kwon.chosungmarket.domain.repository.PersistentStorageRepositoryImpl
import com.kwon.chosungmarket.domain.repository.SessionRepositoryImpl
import com.kwon.chosungmarket.domain.repository.UserRepositoryImpl
import kotlinx.coroutines.flow.first

/**
 * 카카오 로그인 처리를 담당하는 UseCase
 * 카카오 로그인과 자동 로그인 설정을 처리합니다.
 */
class SignInWithKakaoUseCase(
    private val userRepositoryImpl: UserRepositoryImpl,
    private val sessionRepositoryImpl: SessionRepositoryImpl,
    private val persistentStorageRepositoryImpl: PersistentStorageRepositoryImpl
) {

    /**
     * 카카오 로그인을 수행합니다.
     *
     * @param kakaoId 카카오 사용자 ID
     * @param enableAutoLogin 자동 로그인 활성화 여부
     * @return 로그인 성공 여부
     */
    suspend fun invoke(kakaoId: String, enableAutoLogin: Boolean): Result<Boolean> {
        return try {
            userRepositoryImpl.signInWithKakao(kakaoId)
                .onSuccess { success ->
                    if (success) {
                        val userData = userRepositoryImpl.getCurrentUser().first()

                        userData?.let { user ->
                            sessionRepositoryImpl.saveUserId(user.id)
                        }

                        if (enableAutoLogin) {
                            persistentStorageRepositoryImpl.saveKakaoId(kakaoId)
                            persistentStorageRepositoryImpl.setAutoLoginEnabled(true)
                        }
                    }
                }
                .onFailure { e ->
                    KLog.e("SignInWithKakaoUseCase", e)
                }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}