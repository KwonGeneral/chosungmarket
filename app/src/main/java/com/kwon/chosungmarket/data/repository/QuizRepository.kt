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

class QuizRepository(
    private val firebaseQuizDb: FirebaseQuizDb,
    private val firebaseQuizGroupsDb: FirebaseQuizGroupsDb,
    private val sessionRepositoryImpl: SessionRepositoryImpl
) : QuizRepositoryImpl {

    override suspend fun createQuiz(quiz: QuizData): Result<String> {
        return try {
            val quizData = QuizMapper.toFirestore(quiz)
            val documentId = firebaseQuizDb.createQuiz(quizData)
            Result.success(documentId)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

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