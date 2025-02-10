package com.kwon.chosungmarket.data.repository

import com.kwon.chosungmarket.data.db.FirebaseQuizDb
import com.kwon.chosungmarket.data.db.FirebaseQuizGroupsDb
import com.kwon.chosungmarket.data.mapper.QuizGroupMapper
import com.kwon.chosungmarket.data.mapper.QuizMapper
import com.kwon.chosungmarket.domain.model.QuizData
import com.kwon.chosungmarket.domain.model.QuizGroupData
import com.kwon.chosungmarket.domain.repository.QuizRepositoryImpl
import com.kwon.chosungmarket.domain.repository.SessionRepositoryImpl
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow

/**
 * 퀴즈 관련 데이터 처리를 담당하는 Repository 구현체
 * 퀴즈 생성, 조회, 좋아요 등의 기능을 제공합니다.
 */
class QuizRepository(
    private val firebaseQuizDb: FirebaseQuizDb,
    private val firebaseQuizGroupsDb: FirebaseQuizGroupsDb,
    private val sessionRepositoryImpl: SessionRepositoryImpl
) : QuizRepositoryImpl {

    /** 새로운 퀴즈를 생성합니다. */
    override suspend fun createQuiz(quiz: QuizData): Result<String> {
        return try {
            val quizData = QuizMapper.toFirestore(quiz)
            val documentId = firebaseQuizDb.createQuiz(quizData)
            Result.success(documentId)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /** 새로운 퀴즈 그룹을 생성합니다. */
    override suspend fun createQuizGroup(quizGroup: QuizGroupData): Result<String> {
        return try {
            val userId = sessionRepositoryImpl.getUserId().first()
                ?: return Result.failure(Exception("User not logged in"))

            val quizGroupData = QuizGroupMapper.toFirestore(quizGroup)
            val documentId = firebaseQuizGroupsDb.createQuizGroup(userId, quizGroupData)
            Result.success(documentId)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * 퀴즈 그룹 목록을 페이지네이션하여 조회합니다.
     *
     * @param limit 한 번에 가져올 항목 수
     * @param lastDocId 마지막으로 가져온 문서 ID (null이면 첫 페이지)
     */
    override fun getQuizGroupList(limit: Int, lastDocId: String?): Flow<List<QuizGroupData>> = flow {
        val userId = sessionRepositoryImpl.getUserId().first()
            ?: throw Exception("User not logged in")

        val quizGroups = firebaseQuizGroupsDb.getQuizGroups(userId, limit, lastDocId)
        val mappedQuizGroups = quizGroups.map { quizGroupData ->
            QuizGroupMapper.fromFirestore(quizGroupData["id"] as String, quizGroupData)
        }
        emit(mappedQuizGroups)
    }.catch { e ->
        emit(emptyList())
    }

    /** 여러 퀴즈를 ID 목록으로 조회합니다. */
    override suspend fun getQuizListByIdList(quizIdList: List<String>): Result<List<QuizData>> {
        return try {
            val quizDocs = firebaseQuizDb.getQuizzesByIdList(quizIdList)

            val quizzes = quizDocs.map { quizData ->
                QuizMapper.fromFirestore(quizData["id"] as String, quizData)
            }

            Result.success(quizzes)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /** 퀴즈 그룹의 좋아요를 토글합니다. */
    override suspend fun toggleLike(quizGroupId: String): Result<Unit> {
        return try {
            val userId = sessionRepositoryImpl.getUserId().first()
                ?: return Result.failure(Exception("User not logged in"))

            val quizGroup = firebaseQuizGroupsDb.getQuizGroup(userId, quizGroupId)
                ?: return Result.failure(Exception("Quiz group not found"))

            val likedUserIdList = (quizGroup["likedUserIdList"] as? List<*>)
                ?.filterIsInstance<String>()
                ?.toMutableList()
                ?: mutableListOf()

            val isCurrentlyLiked = likedUserIdList.contains(userId)
            val newLikeCount = if (isCurrentlyLiked) {
                likedUserIdList.remove(userId)
                (quizGroup["likeCount"] as Number).toInt() - 1
            } else {
                likedUserIdList.add(userId)
                (quizGroup["likeCount"] as Number).toInt() + 1
            }

            firebaseQuizGroupsDb.updateQuizGroup(userId, quizGroupId, mapOf(
                "likeCount" to newLikeCount,
                "likedUserIdList" to likedUserIdList
            ))

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /** 퀴즈 그룹 ID를 통해 퀴즈 ID 목록을 조회합니다. */
    override suspend fun getQuizIdListByQuizGroup(quizGroupId: String): Result<List<String>> {
        return try {
            val userId = sessionRepositoryImpl.getUserId().first()
                ?: return Result.failure(Exception("User not logged in"))

            val quizGroupRef = firebaseQuizGroupsDb.getQuizGroup(userId, quizGroupId)
            val quizIdList = (quizGroupRef?.get("quizIdList") as? List<String>) ?: emptyList()

            Result.success(quizIdList)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}