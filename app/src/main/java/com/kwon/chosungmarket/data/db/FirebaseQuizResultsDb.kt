package com.kwon.chosungmarket.data.db

import com.google.firebase.firestore.AggregateSource
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.tasks.await

/**
 * Firebase의 퀴즈 그룹 데이터에 접근하는 클래스
 * 각 사용자별로 퀴즈 그룹을 관리합니다.
 * 경로: userList/{userId}/quizResultList/{quizResultId}
 */
class FirebaseQuizResultsDb(
    private val firestore: FirebaseFirestore
) {
    private val quizResultsCollection = firestore.collection("quizResultList")

    suspend fun createQuizResult(resultData: Map<String, Any>): String {
        val docRef = quizResultsCollection
            .add(resultData)
            .await()
        return docRef.id
    }

    suspend fun getQuizResult(resultId: String): Map<String, Any>? {
        return quizResultsCollection
            .document(resultId)
            .get()
            .await()
            .data
    }

    suspend fun getUserQuizResults(userId: String, limit: Int = 10): List<Map<String, Any>> {
        return quizResultsCollection
            .whereEqualTo("userId", userId)
            .orderBy("completedAt", Query.Direction.DESCENDING)
            .limit(limit.toLong())
            .get()
            .await()
            .documents
            .mapNotNull { it.data }
    }

    suspend fun deleteQuizResult(resultId: String) {
        quizResultsCollection
            .document(resultId)
            .delete()
            .await()
    }

    suspend fun getQuizGroupResultCount(quizGroupId: String): Int {
        return quizResultsCollection
            .whereEqualTo("quizGroupId", quizGroupId)
            .count()
            // SERVER: 서버에서 직접 카운트를 계산
            // CLIENT: 로컬 캐시에서 카운트를 계산
            .get(AggregateSource.SERVER)
            .await()
            .count.toInt()
    }
}