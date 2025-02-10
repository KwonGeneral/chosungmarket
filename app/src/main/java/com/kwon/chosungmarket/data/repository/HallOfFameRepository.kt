package com.kwon.chosungmarket.data.repository

import com.kwon.chosungmarket.data.db.FirebaseHallOfFameDb
import com.kwon.chosungmarket.data.db.FirebaseUserDb
import com.kwon.chosungmarket.data.mapper.QuizGroupMapper
import com.kwon.chosungmarket.data.mapper.UserMapper
import com.kwon.chosungmarket.domain.model.QuizGroupData
import com.kwon.chosungmarket.domain.repository.HallOfFameRepositoryImpl
import com.kwon.chosungmarket.common.types.MedalType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

class HallOfFameRepository(
    private val firebaseHallOfFameDb: FirebaseHallOfFameDb,
    private val firebaseUserDb: FirebaseUserDb
) : HallOfFameRepositoryImpl {
    override fun getTopQuizGroupList(): Flow<List<QuizGroupData>> = flow {
        val hallOfFameDoc = firebaseHallOfFameDb.getDocument("rankings")
        val rankings = (hallOfFameDoc?.get("rankings") as? List<*>)
            ?.filterIsInstance<Map<String, Any>>()
            ?: emptyList()

        val quizGroups = rankings.mapNotNull { ranking ->
            val quizGroupId = ranking["quizGroupId"] as? String ?: return@mapNotNull null

            val quizGroupData = firebaseUserDb.getUser(quizGroupId)
            quizGroupData?.let {
                QuizGroupMapper.fromFirestore(quizGroupId, it)
            }
        }

        emit(quizGroups)
    }

    override suspend fun awardMedal(userId: String, medalType: MedalType): Result<Unit> {
        return try {
            val userData = firebaseUserDb.getUser(userId)
                ?: return Result.failure(Exception("사용자를 찾을 수 없습니다."))

            val existingMedalList = (userData["medalList"] as? List<*>)
                ?.filterIsInstance<Map<String, Any>>()
                ?: emptyList()

            val newMedal = mapOf(
                "type" to medalType.name,
                "quizGroupId" to "",
                "acquiredAt" to System.currentTimeMillis()
            )

            val updatedMedalList = existingMedalList + newMedal

            firebaseUserDb.updateUser(userId, mapOf(
                "medalList" to updatedMedalList
            ))

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}