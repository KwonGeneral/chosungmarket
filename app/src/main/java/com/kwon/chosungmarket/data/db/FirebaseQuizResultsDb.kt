package com.kwon.chosungmarket.data.db

import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

/**
 * Firebase의 퀴즈 그룹 데이터에 접근하는 클래스
 * 각 사용자별로 퀴즈 그룹을 관리합니다.
 * 경로: userList/{userId}/quizResultList/{quizResultId}
 */
class FirebaseQuizResultsDb(
    private val firestore: FirebaseFirestore
) {
    private fun getUserQuizResultsCollection(userId: String) =
        firestore.collection("userList")
            .document(userId)
            .collection("quizResultList")

    suspend fun createQuizResult(userId: String, resultData: Map<String, Any>): String {
        val docRef = getUserQuizResultsCollection(userId)
            .add(resultData)
            .await()
        return docRef.id
    }

    suspend fun getQuizResult(userId: String, resultId: String): Map<String, Any>? {
        return getUserQuizResultsCollection(userId)
            .document(resultId)
            .get()
            .await()
            .data
    }

    suspend fun getUserQuizResults(userId: String, limit: Int = 10): List<Map<String, Any>> {
        return getUserQuizResultsCollection(userId)
            .orderBy("completedAt", com.google.firebase.firestore.Query.Direction.DESCENDING)
            .limit(limit.toLong())
            .get()
            .await()
            .documents
            .mapNotNull { it.data }
    }

    suspend fun deleteQuizResult(userId: String, resultId: String) {
        getUserQuizResultsCollection(userId)
            .document(resultId)
            .delete()
            .await()
    }
}