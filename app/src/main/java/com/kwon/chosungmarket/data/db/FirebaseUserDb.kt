package com.kwon.chosungmarket.data.db

import com.google.firebase.firestore.FirebaseFirestore
import com.kwon.chosungmarket.common.utils.KLog
import kotlinx.coroutines.tasks.await

class FirebaseUserDb(
    private val firestore: FirebaseFirestore
) {
    private val usersCollection = firestore.collection("users")

    fun getNewUserId(): String {
        return usersCollection.document().id
    }

    suspend fun createUser(userId: String, userData: Map<String, Any>) {
        usersCollection.document(userId).set(userData).await()
    }

    suspend fun getUser(userId: String): Map<String, Any>? {
        return usersCollection.document(userId).get().await().data?.plus("id" to userId)
    }

    suspend fun updateUser(userId: String, updates: Map<String, Any>) {
        usersCollection.document(userId).update(updates).await()
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

    suspend fun getUserCount(): Int {
        return try {
            val snapshot = usersCollection.get().await()
            snapshot.size()
        } catch (e: Exception) {
            KLog.e("사용자 수 조회 실패", e)
            0
        }
    }
}