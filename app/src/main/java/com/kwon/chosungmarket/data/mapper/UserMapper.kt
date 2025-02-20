package com.kwon.chosungmarket.data.mapper

import com.kwon.chosungmarket.common.types.MedalType
import com.kwon.chosungmarket.domain.model.MedalData
import com.kwon.chosungmarket.domain.model.UserData

/**
 * 사용자 데이터를 Firestore 데이터로 변환하는 클래스
 */
object UserMapper {
    fun toFirestore(userData: UserData): Map<String, Any> = mapOf(
        "kakaoId" to userData.kakaoId,
        "nickname" to userData.nickname,
        "image" to userData.image,
        "profileImageId" to userData.profileImageId,
        "point" to userData.point,
        "quizGroupIdList" to userData.quizGroupIdList,
        "quizResultIdList" to userData.quizResultIdList,
        "createdAt" to userData.createdAt,
        "updatedAt" to userData.updatedAt,
        "lastLoginAt" to userData.lastLoginAt,
        "medalList" to userData.medalList.map { medalToFirestore(it) }
    )

    fun fromFirestore(id: String, data: Map<String, Any>): UserData = UserData(
        id = id,
        kakaoId = data["kakaoId"] as String,
        nickname = data["nickname"] as String,
        image = data["image"] as? String ?: "",
        profileImageId = (data["profileImageId"] as Number).toInt(),
        point = (data["point"] as? Number?)?.toInt() ?: 0,
        quizGroupIdList = (data["quizGroupIdList"] as? List<*>)?.filterIsInstance<String>() ?: emptyList(),
        quizResultIdList = (data["quizResultIdList"] as? List<*>)?.filterIsInstance<String>() ?: emptyList(),
        createdAt = (data["createdAt"] as? Number?)?.toLong() ?: System.currentTimeMillis(),
        updatedAt = (data["updatedAt"] as? Number?)?.toLong() ?: System.currentTimeMillis(),
        lastLoginAt = (data["lastLoginAt"] as? Number?)?.toLong() ?: System.currentTimeMillis(),
        medalList = (data["medalList"] as? List<*>)
            ?.filterIsInstance<Map<String, Any>>()
            ?.mapNotNull { medalFromFirestore(it) }
            ?: emptyList()
    )

    private fun medalToFirestore(medalData: MedalData): Map<String, Any> = mapOf(
        "type" to medalData.type.name,
        "quizGroupId" to medalData.quizGroupId,
        "acquiredAt" to medalData.acquiredAt
    )

    private fun medalFromFirestore(data: Map<String, Any>): MedalData? {
        return try {
            MedalData(
                id = data["id"] as? String ?: "",
                type = MedalType.valueOf(data["type"] as String),
                quizGroupId = data["quizGroupId"] as String,
                acquiredAt = (data["acquiredAt"] as Number?)?.toLong() ?: System.currentTimeMillis()
            )
        } catch (e: Exception) {
            null
        }
    }
}