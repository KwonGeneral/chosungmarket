package com.kwon.chosungmarket.domain.usecase

import com.kwon.chosungmarket.domain.repository.PersistentStorageRepositoryImpl
import com.kwon.chosungmarket.domain.repository.UserRepositoryImpl
import kotlinx.coroutines.flow.first

/**
 * 자동 로그인 처리를 담당하는 UseCase
 * 저장된 카카오 ID와 자동 로그인 설정을 확인하여 로그인을 시도합니다.
 */
class AutoLoginUseCase(
    private val persistentStorageRepositoryImpl: PersistentStorageRepositoryImpl,
    private val userRepositoryImpl: UserRepositoryImpl
) {

    /**
     * 자동 로그인을 시도합니다.
     *
     * @return 로그인 성공 여부
     */
    suspend fun invoke(): Result<Boolean> {
        try {
            val isAutoLoginEnabled = persistentStorageRepositoryImpl.getAutoLoginEnabled().first()
            if (!isAutoLoginEnabled) return Result.success(false)

            val kakaoId = persistentStorageRepositoryImpl.getKakaoId().first()
                ?: return Result.success(false)

            return userRepositoryImpl.signInWithKakao(kakaoId)
        } catch (e: Exception) {
            return Result.failure(e)
        }
    }
}