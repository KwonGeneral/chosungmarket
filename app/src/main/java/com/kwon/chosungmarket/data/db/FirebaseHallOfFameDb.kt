package com.kwon.chosungmarket.data.db

import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

/**
 * Firebase의 명예의 전당 데이터에 접근하는 클래스
 */
class FirebaseHallOfFameDb(
    private val firestore: FirebaseFirestore
) {
    private val hallOfFameCollection = firestore.collection("hallOfFame")

    /** 문서를 생성하거나 덮어씁니다. */
    suspend fun createOrReplaceDocument(documentId: String, data: Map<String, Any>) {
        hallOfFameCollection.document(documentId).set(data).await()
    }

    /** 문서를 조회합니다. */
    suspend fun getDocument(documentId: String): Map<String, Any>? {
        return hallOfFameCollection.document(documentId).get().await().data
    }

    /** 문서를 업데이트합니다. */
    suspend fun updateDocument(documentId: String, updates: Map<String, Any>) {
        hallOfFameCollection.document(documentId).update(updates).await()
    }

    /** 문서를 삭제합니다. */
    suspend fun deleteDocument(documentId: String) {
        hallOfFameCollection.document(documentId).delete().await()
    }

    /** 특정 필드값으로 문서들을 조회합니다. */
    suspend fun findDocumentsByField(field: String, value: Any): List<Map<String, Any>> {
        return hallOfFameCollection.whereEqualTo(field, value)
            .get()
            .await()
            .documents
            .mapNotNull { it.data }
    }
}