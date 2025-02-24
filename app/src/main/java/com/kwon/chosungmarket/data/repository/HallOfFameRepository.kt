package com.kwon.chosungmarket.data.repository

import com.kwon.chosungmarket.data.db.FirebaseHallOfFameDb
import com.kwon.chosungmarket.data.db.FirebaseQuizGroupsDb
import com.kwon.chosungmarket.data.db.FirebaseUserDb
import com.kwon.chosungmarket.data.mapper.QuizGroupMapper
import com.kwon.chosungmarket.data.mapper.UserMapper
import com.kwon.chosungmarket.domain.model.QuizGroupData
import com.kwon.chosungmarket.domain.model.UserData
import com.kwon.chosungmarket.domain.repository.HallOfFameRepositoryImpl
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

/**
 * 명예의 전당 관련 데이터 처리를 담당하는 Repository 구현체
 */
class HallOfFameRepository(
    private val firebaseHallOfFameDb: FirebaseHallOfFameDb,
    private val firebaseQuizGroupsDb: FirebaseQuizGroupsDb,
    private val firebaseUserDb: FirebaseUserDb
) : HallOfFameRepositoryImpl {

    companion object {
        private const val MIN_LIKES_FOR_RANKING = 0  // 순위에 들어가기 위한 최소 추천 수
        private const val RANKING_LIMIT = 100  // 상위 랭킹 조회 제한
    }

    /** 상위 랭킹 퀴즈 그룹 목록을 조회합니다. */
    override fun getTopQuizGroupList(): Flow<List<QuizGroupData>> = flow {
        try {
            // 오늘 날짜 순위가 없으면 새로 계산
            if (!firebaseHallOfFameDb.checkTodayRankings()) {
                updateTodayRankings()
            }

            // 오늘 날짜 순위 조회
            val rankings = firebaseHallOfFameDb.getTodayRankings()

            // 순위 데이터 변환
            val quizGroups = rankings?.map { rankData ->
                QuizGroupMapper.fromFirestore(
                    id = rankData["id"] as String,
                    data = rankData
                )
            } ?: emptyList()

            emit(quizGroups)
        } catch (e: Exception) {
            emit(emptyList())
        }
    }

    /** 상위 랭킹 사용자 목록을 조회합니다. */
    override fun getTopUserList(): Flow<List<UserData>> = flow {
        try {
            val topUsers = firebaseUserDb.getTopUsersByPoint(RANKING_LIMIT)
                .map { UserMapper.fromFirestore(it["id"] as String, it) }

            emit(topUsers)
        } catch (e: Exception) {
            emit(emptyList())
        }
    }

    /** 오늘 날짜의 순위를 새로 계산하여 저장합니다. */
    private suspend fun updateTodayRankings() {
        val topQuizGroups = firebaseQuizGroupsDb.getAllTopRatedQuizGroups(MIN_LIKES_FOR_RANKING)
        val sortedQuizGroups = topQuizGroups
            .sortedByDescending { it["likeCount"] as Long }
            .take(100)
            .mapIndexed { index, quizGroup ->
                quizGroup + mapOf(
                    "rank" to (index + 1),
                    "updatedAt" to System.currentTimeMillis()
                )
            }

        firebaseHallOfFameDb.saveRankings(sortedQuizGroups)
    }
}