package com.kwon.chosungmarket.domain.usecase

import com.kwon.chosungmarket.common.utils.KLog
import com.kwon.chosungmarket.domain.repository.PersistentStorageRepositoryImpl
import com.kwon.chosungmarket.domain.repository.SessionRepositoryImpl
import com.kwon.chosungmarket.domain.repository.UserRepositoryImpl
import kotlinx.coroutines.flow.first

class WithdrawUserUseCase(
    private val userRepositoryImpl: UserRepositoryImpl,
    private val sessionRepositoryImpl: SessionRepositoryImpl,
    private val persistentStorageRepositoryImpl: PersistentStorageRepositoryImpl
) {
    suspend fun invoke(): Result<Unit> {
        val userId = sessionRepositoryImpl.getUserId().first()
            ?: return Result.failure(Exception("로그인 상태가 아닙니다."))

        return userRepositoryImpl.withdrawUser(userId)
            .onSuccess {
                sessionRepositoryImpl.clearUserId()
                persistentStorageRepositoryImpl.clearKakaoId()
                persistentStorageRepositoryImpl.setAutoLoginEnabled(false)
            }
            .onFailure { e ->
                KLog.e("회원 탈퇴 실패", e)
            }
    }
}