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
     * @return 생성된 퀴즈 그룹 ID
     */
    suspend fun invoke(
        title: String,
        description: String,
        quizzes: List<QuizData>,
        difficulty: QuizDifficulty
    ): Result<String> {
        val userId = sessionRepositoryImpl.getUserId().first()
            ?: return Result.failure(Exception("로그인 상태가 아닙니다."))

        val userData = userRepositoryImpl.getCurrentUser().first()
            ?: return Result.failure(Exception("유저 정보를 찾을 수 없습니다."))

        val quizIdList = quizzes.map { quiz ->
            quizRepositoryImpl.createQuiz(quiz.copy(difficulty = difficulty))
                .getOrElse { return Result.failure(it) }
        }

        val quizGroup = QuizGroupData(
            id = UUID.randomUUID().toString(),
            userId = userId,
            title = title,
            description = description,
            quizIdList = quizIdList,
            userNickname = userData.nickname,
            status = QuizGroupStatus.ACTIVE
        )

        return quizRepositoryImpl.createQuizGroup(quizGroup)
    }
}