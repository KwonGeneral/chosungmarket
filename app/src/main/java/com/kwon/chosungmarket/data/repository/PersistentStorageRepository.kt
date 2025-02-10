package com.kwon.chosungmarket.data.repository

import com.kwon.chosungmarket.data.db.SharedDataStore
import com.kwon.chosungmarket.domain.repository.PersistentStorageRepositoryImpl
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch

/**
 * 로컬 저장소 접근을 정의하는 인터페이스
 * 자동 로그인 등 앱 설정 데이터를 관리합니다.
 */
class PersistentStorageRepository(
    private val sharedDataStore: SharedDataStore
) : PersistentStorageRepositoryImpl {

    /**
     * 카카오 ID를 저장합니다.
     * 자동 로그인에 사용됩니다.
     */
    override suspend fun saveKakaoId(kakaoId: String): Result<Unit> {
        return try {
            sharedDataStore.saveKakaoId(kakaoId)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /** 저장된 카카오 ID를 관찰합니다. */
    override fun getKakaoId(): Flow<String?> {
        return sharedDataStore.getKakaoId()
            .catch { emit(null) }
    }

    /** 저장된 카카오 ID를 삭제합니다. */
    override suspend fun clearKakaoId(): Result<Unit> {
        return try {
            sharedDataStore.clear()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /** 자동 로그인 설정을 변경합니다. */
    override suspend fun setAutoLoginEnabled(enabled: Boolean): Result<Unit> {
        return try {
            sharedDataStore.saveAutoLoginEnabled(enabled)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /** 자동 로그인 설정을 관찰합니다. */
    override fun getAutoLoginEnabled(): Flow<Boolean> {
        return sharedDataStore.getAutoLoginEnabled()
            .catch { emit(false) }
    }
}