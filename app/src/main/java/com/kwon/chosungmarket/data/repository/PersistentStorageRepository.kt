package com.kwon.chosungmarket.data.repository

import com.kwon.chosungmarket.data.db.SharedDataStore
import com.kwon.chosungmarket.domain.repository.PersistentStorageRepositoryImpl
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map

class PersistentStorageRepository(
    private val sharedDataStore: SharedDataStore
) : PersistentStorageRepositoryImpl {
    override suspend fun saveKakaoId(kakaoId: String): Result<Unit> {
        return try {
            sharedDataStore.saveKakaoId(kakaoId)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun observeKakaoId(): Flow<String?> {
        return sharedDataStore.observeKakaoId()
            .catch { emit(null) }
    }

    override suspend fun clearKakaoId(): Result<Unit> {
        return try {
            sharedDataStore.clear()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun setAutoLoginEnabled(enabled: Boolean): Result<Unit> {
        return try {
            sharedDataStore.saveAutoLoginEnabled(enabled)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun observeAutoLoginEnabled(): Flow<Boolean> {
        return sharedDataStore.observeAutoLoginEnabled()
            .catch { emit(false) }
    }
}