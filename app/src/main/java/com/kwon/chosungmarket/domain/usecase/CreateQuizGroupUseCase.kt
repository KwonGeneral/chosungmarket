package com.kwon.chosungmarket.domain.usecase

import com.kwon.chosungmarket.common.types.QuizDifficulty
import com.kwon.chosungmarket.common.types.QuizGroupStatus
import com.kwon.chosungmarket.domain.model.QuizData
import com.kwon.chosungmarket.domain.model.QuizGroupData
import com.kwon.chosungmarket.domain.repository.QuizRepositoryImpl
import com.kwon.chosungmarket.domain.repository.SessionRepositoryImpl
import com.kwon.chosungmarket.domain.repository.UserRepositoryImpl
import kotlinx.coroutines.flow.first
import java.util.UUID

/**
 * 퀴즈 그룹 생성을 담당하는 UseCase
 * 퀴즈 문제들을 생성하고 이를 그룹으로 묶어 저장합니다.
 */
class CreateQuizGroupUseCase(
    private val quizRepositoryImpl: QuizRepositoryImpl,
    private val sessionRepositoryImpl: SessionRepositoryImpl,
    private val userRepositoryImpl: UserRepositoryImpl
) {
    /**
     * 새로운 퀴즈 그룹을 생성합니다.
     *
     * @param title 퀴즈 그룹 제목
     * @param description 퀴즈 그룹 설명
     * @param quizzes 퀴즈 문제 목록
     * @param difficulty 퀴즈 난이도
     * @param image 퀴즈 그룹 이미지 URL
     * @param tags 퀴즈 그룹 태그 목록
     *
     * @return 생성된 퀴즈 그룹 ID
     */
    suspend fun invoke(
        title: String,
        description: String,
        quizzes: List<QuizData>,
        difficulty: QuizDifficulty,
        image: String = "",
        tags: List<String> = emptyList()
    ): Result<String> {
        // 입력값 검증
        if (title.isBlank()) return Result.failure(Exception("제목은 필수입니다."))
        if (description.isBlank()) return Result.failure(Exception("설명은 필수입니다."))
        if (quizzes.isEmpty()) return Result.failure(Exception("최소 1개 이상의 퀴즈가 필요합니다."))

        val userId = sessionRepositoryImpl.getUserId().first()
            ?: return Result.failure(Exception("로그인 상태가 아닙니다."))

        val userData = userRepositoryImpl.getCurrentUser().first()
            ?: return Result.failure(Exception("유저 정보를 찾을 수 없습니다."))

        return try {
            // 퀴즈 생성 - 생성 시점에 모든 데이터가 완전해야 함
            val quizIdList = quizzes.map { quiz ->
                // 퀴즈 데이터 검증
                validateQuiz(quiz)

                quizRepositoryImpl.createQuiz(quiz.copy(difficulty = difficulty))
                    .getOrElse { return Result.failure(it) }
            }

            // 퀴즈 그룹 생성
            val quizGroup = QuizGroupData(
                id = UUID.randomUUID().toString(),
                userId = userId,
                title = title,
                description = description,
                image = image,
                quizIdList = quizIdList,
                tagList = tags,
                userNickname = userData.nickname,
                status = QuizGroupStatus.ACTIVE
            )

            quizRepositoryImpl.createQuizGroup(quizGroup)
                .onSuccess { quizGroupId ->
                    userRepositoryImpl.addQuizGroupToUser(userId, quizGroupId)
                }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /** 퀴즈 데이터 검증 */
    private fun validateQuiz(quiz: QuizData) {
        require(quiz.consonant.isNotBlank()) { "초성은 필수입니다." }
        require(quiz.answer.isNotBlank()) { "정답은 필수입니다." }
        require(quiz.description.isNotBlank()) { "설명은 필수입니다." }
    }
}