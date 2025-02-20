package com.kwon.chosungmarket.data.db

import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

/**
 * Firebase의 사용자 데이터에 접근하는 클래스
 */
class FirebaseUserDb(
    private val firestore: FirebaseFirestore
) {
    private val usersCollection = firestore.collection("userList")

    suspend fun createUser(userId: String, userData: Map<String, Any>) {
        usersCollection.document(userId).set(userData).await()
    }

    suspend fun getUser(userId: String): Map<String, Any>? {
        return usersCollection.document(userId).get().await().data?.plus("id" to userId)
    }

    suspend fun updateUser(userId: String, updates: Map<String, Any>) {
        usersCollection.document(userId).update(updates).await()
    }

    suspend fun updateUserQuizGroups(userId: String, quizGroupId: String, isAdd: Boolean) {
        val operation = if (isAdd) FieldValue.arrayUnion(quizGroupId) else FieldValue.arrayRemove(quizGroupId)
        usersCollection.document(userId).update("quizGroupIdList", operation).await()
    }

    suspend fun updateUserQuizResults(userId: String, resultId: String, isAdd: Boolean) {
        val operation = if (isAdd) FieldValue.arrayUnion(resultId) else FieldValue.arrayRemove(resultId)
        usersCollection.document(userId).update("quizResultIdList", operation).await()
    }

    suspend fun deleteUser(userId: String) {
        usersCollection.document(userId).delete().await()
    }

    suspend fun findUserByField(field: String, value: Any): List<Map<String, Any>> {
        return usersCollection.whereEqualTo(field, value)
            .get()
            .await()
            .documents
            .mapNotNull { it.data?.plus("id" to it.id) }
    }

    fun getNewUserId(): String {
        return usersCollection.document().id
    }
}