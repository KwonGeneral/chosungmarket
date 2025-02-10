package com.kwon.chosungmarket.domain.usecase

import com.kwon.chosungmarket.common.utils.KLog
import com.kwon.chosungmarket.domain.repository.PersistentStorageRepositoryImpl
import com.kwon.chosungmarket.domain.repository.SessionRepositoryImpl
import com.kwon.chosungmarket.domain.repository.UserRepositoryImpl
import kotlinx.coroutines.flow.first

class SignInWithKakaoUseCase(
    private val userRepositoryImpl: UserRepositoryImpl,
    private val sessionRepositoryImpl: SessionRepositoryImpl,
    private val persistentStorageRepositoryImpl: PersistentStorageRepositoryImpl
) {
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