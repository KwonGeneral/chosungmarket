package com.kwon.chosungmarket.data.db

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.tasks.await

class FirebaseQuizGroupsDb(
    private val firestore: FirebaseFirestore
) {
    private fun getUserQuizGroupsCollection(userId: String) =
        firestore.collection("users")
            .document(userId)
            .collection("quizGroups")

    suspend fun createQuizGroup(userId: String, quizGroupData: Map<String, Any>): String {
        val docRef = getUserQuizGroupsCollection(userId)
            .add(quizGroupData)
            .await()
        return docRef.id
    }

    suspend fun getQuizGroup(userId: String, quizGroupId: String): Map<String, Any>? {
        return getUserQuizGroupsCollection(userId)
            .document(quizGroupId)
            .get()
            .await()
            .data
    }

    suspend fun updateQuizGroup(userId: String, quizGroupId: String, updates: Map<String, Any>) {
        getUserQuizGroupsCollection(userId)
            .document(quizGroupId)
            .update(updates)
            .await()
    }

    suspend fun deleteQuizGroup(userId: String, quizGroupId: String) {
        getUserQuizGroupsCollection(userId)
            .document(quizGroupId)
            .delete()
            .await()
    }

    suspend fun getQuizGroups(
        userId: String,
        limit: Int = 10,
        lastDocId: String? = null
    ): List<Map<String, Any>> {
        var query = getUserQuizGroupsCollection(userId)
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .limit(limit.toLong())

        if (lastDocId != null) {
            val lastDoc = getUserQuizGroupsCollection(userId)
                .document(lastDocId)
                .get()
                .await()
            query = query.startAfter(lastDoc)
        }

        return query.get()
            .await()
            .documents
            .mapNotNull {
                it.data?.plus("id" to it.id)
            }
    }

    suspend fun getUserQuizGroupsByStatus(
        userId: String,
        status: String,
        limit: Int = 10
    ): List<Map<String, Any>> {
        return getUserQuizGroupsCollection(userId)
            .whereEqualTo("status", status)
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .limit(limit.toLong())
            .get()
            .await()
            .documents
            .mapNotNull {
                it.data?.plus("id" to it.id)
            }
    }
}