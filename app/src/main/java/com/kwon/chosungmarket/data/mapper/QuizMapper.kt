package com.kwon.chosungmarket.data.mapper

import com.kwon.chosungmarket.common.types.QuizDifficulty
import com.kwon.chosungmarket.domain.model.QuizData

/**
 * 퀴즈 데이터를 Firestore 데이터로 변환하는 클래스
 */
object QuizMapper {
    fun toFirestore(quizData: QuizData): Map<String, Any> = mapOf(
        "consonant" to quizData.consonant,
        "answer" to quizData.answer,
        "description" to quizData.description,
        "tagList" to quizData.tagList,
        "difficulty" to quizData.difficulty.name
    )

    fun fromFirestore(id: String, data: Map<String, Any>): QuizData = QuizData(
        id = (data["id"] as? String) ?: (data["documentId"] as? String) ?: id,
        consonant = data["consonant"] as? String ?: "",
        answer = data["answer"] as? String ?: "",
        description = data["description"] as? String ?: "",
        tagList = (data["tagList"] as? List<*>)?.filterIsInstance<String>() ?: emptyList(),
        difficulty = try {
            QuizDifficulty.valueOf(data["difficulty"] as? String ?: QuizDifficulty.MEDIUM.name)
        } catch (e: Exception) {
            QuizDifficulty.MEDIUM
        }
    )
}