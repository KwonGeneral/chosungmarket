package com.kwon.chosungmarket.data.mapper

import com.kwon.chosungmarket.common.types.ResultStatus
import com.kwon.chosungmarket.domain.model.QuizResultData

object QuizResultMapper {
    fun toFirestore(quizResultData: QuizResultData): Map<String, Any> = mapOf(
        "userId" to quizResultData.userId,
        "quizGroupId" to quizResultData.quizGroupId,
        "score" to quizResultData.score,
        "answerList" to quizResultData.answerList,
        "completedAt" to quizResultData.completedAt,
        "status" to quizResultData.status.name
    )

    fun fromFirestore(id: String, data: Map<String, Any>): QuizResultData = QuizResultData(
        id = id,
        userId = data["userId"] as String,
        quizGroupId = data["quizGroupId"] as String,
        score = (data["score"] as Number).toInt(),
        answerList = (data["answerList"] as List<*>).map { it.toString() },
        completedAt = (data["completedAt"] as Number?)?.toLong() ?: System.currentTimeMillis(),
        status = ResultStatus.valueOf(data["status"] as? String ?: ResultStatus.PENDING.name)
    )
}