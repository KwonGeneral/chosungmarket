package com.kwon.chosungmarket.data.db

import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

/**
 * Firebase의 퀴즈 데이터에 접근하는 클래스
 */
class FirebaseQuizDb(
    private val firestore: FirebaseFirestore
) {
    private val quizsCollection = firestore.collection("quizList")

    /** 여러 퀴즈 ID로 퀴즈 데이터를 일괄 조회합니다. */
    suspend fun getQuizzesByIdList(quizIdList: List<String>): List<Map<String, Any>> {
        if (quizIdList.isEmpty()) return emptyList()

        return try {
            val documents = quizsCollection
                .whereIn(FieldPath.documentId(), quizIdList)
                .get()
                .await()
                .documents
                .map { document ->
                    val data = document.data ?: mapOf()
                    data.toMutableMap().apply {
                        put("id", document.id)
                        put("documentId", document.id)
                    }
                }

            documents
        } catch (e: Exception) {
            emptyList()
        }
    }

    /** 퀴즈 데이터를 생성합니다. */
    suspend fun createQuiz(quizData: Map<String, Any>): String {
        val docRef = quizsCollection.add(quizData).await()
        return docRef.id
    }

    /** 퀴즈 데이터를 조회합니다. */
    suspend fun getQuiz(quizId: String): Map<String, Any>? {
        return quizsCollection.document(quizId).get().await().data
    }

    /** 여러 퀴즈를 한 번에 삭제합니다. */
    suspend fun deleteQuizzes(quizIdList: List<String>) {
        if (quizIdList.isEmpty()) return

        // 배치 작업 생성
        val batch = firestore.batch()

        // 각 퀴즈에 대한 삭제 작업을 배치에 추가
        quizIdList.forEach { quizId ->
            val docRef = quizsCollection.document(quizId)
            batch.delete(docRef)
        }

        // 배치 작업 실행
        batch.commit().await()
    }
}