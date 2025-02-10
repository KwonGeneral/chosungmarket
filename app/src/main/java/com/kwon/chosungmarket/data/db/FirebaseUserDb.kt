package com.kwon.chosungmarket.data.db

import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

/**
 * Firebase의 사용자 데이터에 접근하는 클래스
 */
class FirebaseUserDb(
    private val firestore: FirebaseFirestore
) {
    private val usersCollection = firestore.collection("userList")

    /** 새로운 사용자 ID를 생성합니다. */
    fun getNewUserId(): String {
        return usersCollection.document().id
    }

    /** 사용자를 생성합니다. */
    suspend fun createUser(userId: String, userData: Map<String, Any>) {
        usersCollection.document(userId).set(userData).await()
    }

    /** 사용자를 조회합니다. */
    suspend fun getUser(userId: String): Map<String, Any>? {
        return usersCollection.document(userId).get().await().data?.plus("id" to userId)
    }

    /** 사용자를 업데이트합니다. */
    suspend fun updateUser(userId: String, updates: Map<String, Any>) {
        usersCollection.document(userId).update(updates).await()
    }

    /** 사용자를 삭제합니다. */
    suspend fun deleteUser(userId: String) {
        usersCollection.document(userId).delete().await()
    }

    /** 특정 필드값으로 사용자들을 조회합니다. */
    suspend fun findUserByField(field: String, value: Any): List<Map<String, Any>> {
        return usersCollection.whereEqualTo(field, value)
            .get()
            .await()
            .documents
            .mapNotNull { it.data?.plus("id" to it.id) }
    }
}