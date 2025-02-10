package com.kwon.chosungmarket.data.db

import com.google.firebase.firestore.FirebaseFirestore
import com.kwon.chosungmarket.common.utils.KLog
import kotlinx.coroutines.tasks.await

class FirebaseQuizResultsDb(
    private val firestore: FirebaseFirestore
) {
    private fun getUserQuizResultsCollection(userId: String) =
        firestore.collection("users")
            .document(userId)
            .collection("quizResults")

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