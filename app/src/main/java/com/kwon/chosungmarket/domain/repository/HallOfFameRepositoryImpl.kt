package com.kwon.chosungmarket.domain.repository

import com.kwon.chosungmarket.common.types.MedalType
import com.kwon.chosungmarket.domain.model.QuizGroupData
import kotlinx.coroutines.flow.Flow

interface HallOfFameRepositoryImpl {
    fun getTopQuizGroupList(): Flow<List<QuizGroupData>>
    suspend fun awardMedal(userId: String, medalType: MedalType): Result<Unit>
}