package com.kwon.chosungmarket.domain.repository

import com.kwon.chosungmarket.domain.model.QuizData
import com.kwon.chosungmarket.domain.model.QuizGroupData
import kotlinx.coroutines.flow.Flow

/**
 * 퀴즈 관련 데이터 액세스를 정의하는 인터페이스
 */
interface QuizRepositoryImpl {
    /** 새로운 퀴즈를 생성합니다. */
    suspend fun createQuiz(quiz: QuizData): Result<String>

    /** 새로운 퀴즈 그룹을 생성합니다. */
    suspend fun createQuizGroup(quizGroup: QuizGroupData): Result<String>

    /**
     * 퀴즈 그룹 목록을 페이지네이션하여 조회합니다.
     *
     * @param limit 한 번에 가져올 항목 수
     * @param lastDocId 마지막으로 가져온 문서 ID (null이면 첫 페이지)
     */
    fun getQuizGroupList(
        limit: Int = 10,
        lastDocId: String? = null
    ): Flow<List<QuizGroupData>>

    /** 여러 퀴즈를 ID 목록으로 조회합니다. */
    suspend fun getQuizListByIdList(quizIdList: List<String>): Result<List<QuizData>>

    /** 퀴즈 그룹의 좋아요를 토글합니다. */
    suspend fun toggleLike(quizGroupId: String): Result<Unit>

    /** 퀴즈 그룹 ID를 통해 퀴즈 ID 목록을 조회합니다. */
    suspend fun getQuizIdListByQuizGroup(quizGroupId: String): Result<List<String>>
}