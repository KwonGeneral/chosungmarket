package com.kwon.chosungmarket.data.db

import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class FirebaseHallOfFameDb(
    private val firestore: FirebaseFirestore
) {
    private val hallOfFameCollection = firestore.collection("hallOfFame")

    suspend fun createOrReplaceDocument(documentId: String, data: Map<String, Any>) {
        hallOfFameCollection.document(documentId).set(data).await()
    }

    suspend fun getDocument(documentId: String): Map<String, Any>? {
        return hallOfFameCollection.document(documentId).get().await().data
    }

    suspend fun updateDocument(documentId: String, updates: Map<String, Any>) {
        hallOfFameCollection.document(documentId).update(updates).await()
    }

    suspend fun deleteDocument(documentId: String) {
        hallOfFameCollection.document(documentId).delete().await()
    }

    suspend fun findDocumentsByField(field: String, value: Any): List<Map<String, Any>> {
        return hallOfFameCollection.whereEqualTo(field, value)
            .get()
            .await()
            .documents
            .mapNotNull { it.data }
    }
}