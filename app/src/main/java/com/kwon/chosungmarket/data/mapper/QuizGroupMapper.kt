package com.kwon.chosungmarket.data.mapper

import com.kwon.chosungmarket.common.types.QuizGroupStatus
import com.kwon.chosungmarket.domain.model.QuizGroupData

/**
 * 퀴즈 그룹 데이터를 Firestore 데이터로 변환하는 클래스
 */
object QuizGroupMapper {
    fun toFirestore(quizGroupData: QuizGroupData): Map<String, Any> = mapOf(
        "userId" to quizGroupData.userId,
        "title" to quizGroupData.title,
        "description" to quizGroupData.description,
        "image" to quizGroupData.image,
        "quizIdList" to quizGroupData.quizIdList,
        "tagList" to quizGroupData.tagList,
        "likeCount" to quizGroupData.likeCount,
        "likedUserIdList" to quizGroupData.likedUserIdList,
        "createdAt" to quizGroupData.createdAt,
        "updatedAt" to quizGroupData.updatedAt,
        "userNickname" to quizGroupData.userNickname,
        "status" to quizGroupData.status.name,
        "quizResultCount" to quizGroupData.quizResultCount
    )

    fun fromFirestore(id: String, data: Map<String, Any>): QuizGroupData = QuizGroupData(
        id = id,
        userId = data["userId"] as? String ?: "",
        title = data["title"] as? String ?: "",
        description = data["description"] as? String ?: "",
        image = data["image"] as? String ?: "",
        quizIdList = (data["quizIdList"] as? List<String>) ?: emptyList(),
        tagList = (data["tagList"] as? List<String>) ?: emptyList(),
        likeCount = (data["likeCount"] as? Number?)?.toInt() ?: 0,
        likedUserIdList = (data["likedUserIdList"] as? List<String>) ?: emptyList(),
        createdAt = (data["createdAt"] as? Number?)?.toLong() ?: System.currentTimeMillis(),
        updatedAt = (data["updatedAt"] as? Number?)?.toLong() ?: System.currentTimeMillis(),
        userNickname = data["userNickname"] as? String ?: "알 수 없는 사용자",
        status = try {
            QuizGroupStatus.valueOf(data["status"] as? String ?: QuizGroupStatus.ACTIVE.name)
        } catch (e: Exception) {
            QuizGroupStatus.ACTIVE
        },
        quizResultCount = (data["quizResultCount"] as? Number?)?.toInt() ?: 0
    )
}