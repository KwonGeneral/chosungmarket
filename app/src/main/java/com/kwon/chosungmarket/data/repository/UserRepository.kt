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

/**
 * 사용자 인증 및 프로필 관리를 담당하는 Repository 구현체
 * 카카오 로그인과 사용자 정보 관리를 담당합니다.
 */
class UserRepository(
    private val firebaseUserDb: FirebaseUserDb,
    private val sharedDataStore: SharedDataStore
) : UserRepositoryImpl {

    /**
     * 카카오 로그인을 처리합니다.
     * 최초 로그인 시 새로운 사용자를 생성하고,
     * 기존 사용자는 마지막 로그인 시간을 업데이트합니다.
     */
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

    /** 사용자 프로필을 업데이트합니다. */
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

    /** 현재 로그인한 사용자 정보를 조회합니다. */
    override fun getCurrentUser(): Flow<UserData?> = flow {
        val kakaoId = sharedDataStore.getKakaoId().first()

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

    /** 사용자 정보를 삭제합니다. */
    override suspend fun withdrawUser(userId: String): Result<Unit> {
        return try {
            firebaseUserDb.deleteUser(userId)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /** 카카오 사용자 정보를 조회합니다. */
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

    /** 카카오 사용자 정보를 담는 데이터 클래스 */
    private data class KakaoUserInfo(
        val id: String,
        val nickname: String?
    )
}