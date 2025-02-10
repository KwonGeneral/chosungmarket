package com.kwon.chosungmarket.data.repository

import com.kakao.sdk.user.UserApiClient
import com.kwon.chosungmarket.data.db.FirebaseUserDb
import com.kwon.chosungmarket.data.db.SharedDataStore
import com.kwon.chosungmarket.data.mapper.UserMapper
import com.kwon.chosungmarket.domain.model.UserData
import com.kwon.chosungmarket.domain.repository.UserRepositoryImpl
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class UserRepository(
    private val firebaseUserDb: FirebaseUserDb,
    private val sharedDataStore: SharedDataStore
) : UserRepositoryImpl {
    override suspend fun signInWithKakao(kakaoId: String): Result<Boolean> {
        return try {
            val existingUsers = firebaseUserDb.findUserByField("kakaoId", kakaoId)

            if (existingUsers.isEmpty()) {
                val userInfo = getUserInfo()
                val newUser = UserData(
                    id = "",
                    kakaoId = kakaoId,
                    nickname = userInfo.nickname ?: "",
                    profileImageId = 1,
                )

                val userData = UserMapper.toFirestore(newUser)
                val newUserId = firebaseUserDb.getNewUserId()
                firebaseUserDb.createUser(newUserId, userData)
            } else {
                val userMap = existingUsers.first()
                val userId = userMap["id"] as? String

                if (!userId.isNullOrEmpty()) {
                    firebaseUserDb.updateUser(userId, mapOf(
                        "lastLoginAt" to System.currentTimeMillis()
                    ))
                } else {
                    return Result.failure(Exception("사용자 ID를 찾을 수 없습니다"))
                }
            }

            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateUserProfile(userId: String, nickname: String, profileImageId: Int): Result<Unit> {
        return try {
            firebaseUserDb.updateUser(userId, mapOf(
                "nickname" to nickname.trim(),
                "profileImageId" to profileImageId,
                "updatedAt" to System.currentTimeMillis()
            ))
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun getCurrentUser(): Flow<UserData?> = flow {
        val kakaoId = sharedDataStore.observeKakaoId().first()

        if (kakaoId != null) {
            val existingUsers = firebaseUserDb.findUserByField("kakaoId", kakaoId)
            val userData = existingUsers.firstOrNull()?.let {
                UserMapper.fromFirestore(it["id"] as String, it)
            }
            emit(userData)
        } else {
            emit(null)
        }
    }

    override suspend fun withdrawUser(userId: String): Result<Unit> {
        return try {
            firebaseUserDb.deleteUser(userId)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private suspend fun getUserInfo() = suspendCancellableCoroutine<KakaoUserInfo> { continuation ->
        UserApiClient.instance.me { user, error ->
            if (error != null) {
                continuation.resumeWithException(error)
            } else if (user != null) {
                continuation.resume(KakaoUserInfo(
                    id = user.id.toString(),
                    nickname = user.kakaoAccount?.profile?.nickname
                ))
            } else {
                continuation.resumeWithException(Exception("Unknown error"))
            }
        }
    }

    private data class KakaoUserInfo(
        val id: String,
        val nickname: String?
    )
}