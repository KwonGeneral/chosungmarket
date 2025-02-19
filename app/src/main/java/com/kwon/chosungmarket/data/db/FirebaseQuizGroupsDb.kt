package com.kwon.chosungmarket.data.db

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.tasks.await

/**
 * Firebase의 퀴즈 그룹 데이터에 접근하는 클래스
 * 각 사용자별로 퀴즈 그룹을 관리합니다.
 * 경로: userList/{userId}/quizGroupList/{quizGroupId}
 */
class FirebaseQuizGroupsDb(
    private val firestore: FirebaseFirestore
) {
    /** 특정 사용자의 퀴즈 그룹 컬렉션 참조를 반환합니다. */
    private fun getUserQuizGroupsCollection(userId: String) =
        firestore.collection("userList")
            .document(userId)
            .collection("quizGroupList")

    /**
     * 페이지네이션을 적용하여 퀴즈 그룹 목록을 조회합니다.
     * @param lastDocId 마지막으로 조회한 문서 ID (null이면 첫 페이지)
     */
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

    /** 퀴즈 그룹을 생성합니다. */
    suspend fun createQuizGroup(userId: String, quizGroupData: Map<String, Any>): String {
        val docRef = getUserQuizGroupsCollection(userId)
            .add(quizGroupData)
            .await()
        return docRef.id
    }

    /** 퀴즈 그룹을 조회합니다. */
    suspend fun getQuizGroup(userId: String, quizGroupId: String): Map<String, Any>? {
        return getUserQuizGroupsCollection(userId)
            .document(quizGroupId)
            .get()
            .await()
            .data
    }

    /** 퀴즈 그룹을 업데이트합니다. */
    suspend fun updateQuizGroup(userId: String, quizGroupId: String, updates: Map<String, Any>) {
        getUserQuizGroupsCollection(userId)
            .document(quizGroupId)
            .update(updates)
            .await()
    }

    /** 퀴즈 그룹을 삭제합니다. */
    suspend fun deleteQuizGroup(userId: String, quizGroupId: String) {
        getUserQuizGroupsCollection(userId)
            .document(quizGroupId)
            .delete()
            .await()
    }

    /** 추천수가 특정 수 이상인 모든 사용자의 퀴즈 그룹 목록을 조회합니다. */
    suspend fun getAllTopRatedQuizGroups(minLikes: Int): List<Map<String, Any>> {
        return try {
            val userSnapshots = firestore.collection("userList")
                .get()
                .await()

            userSnapshots.documents.flatMap { userDoc ->
                val userId = userDoc.id
                val quizGroupsCollection = getUserQuizGroupsCollection(userId)

                quizGroupsCollection
                    .whereGreaterThanOrEqualTo("likeCount", minLikes)
                    .orderBy("likeCount", Query.Direction.DESCENDING)
                    .get()
                    .await()
                    .documents
                    .mapNotNull { quizGroupDoc ->
                        quizGroupDoc.data?.plus(mapOf(
                            "id" to quizGroupDoc.id,
                            "userId" to userId
                        ))
                    }
            }
        } catch (e: Exception) {
            emptyList()
        }
    }
}