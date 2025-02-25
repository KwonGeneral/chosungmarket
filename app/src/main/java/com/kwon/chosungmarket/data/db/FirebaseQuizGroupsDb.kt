package com.kwon.chosungmarket.data.db

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.kwon.chosungmarket.common.types.QuizSortOption
import com.kwon.chosungmarket.common.types.QuizTags
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
    private val quizGroupsCollection = firestore.collection("quizGroupList")

    /**
     * 페이지네이션을 적용하여 퀴즈 그룹 목록을 조회합니다.
     * @param lastDocId 마지막으로 조회한 문서 ID (null이면 첫 페이지)
     * @param tag 퀴즈 그룹의 태그 (null이면 전체)
     * @param sortOption 정렬 옵션
     */
    suspend fun getQuizGroups(
        limit: Int = 10,
        lastDocId: String? = null,
        tag: String? = null,
        sortOption: QuizSortOption = QuizSortOption.RECOMMENDED
    ): List<Map<String, Any>> {
        var query: Query = quizGroupsCollection

        // 태그 필터링
        if (tag != null && tag != QuizTags.ALL) {
            query = query.whereArrayContains("tagList", tag)
        }

        // 정렬 옵션
        when (sortOption) {
            QuizSortOption.RECOMMENDED -> {
                query = query.orderBy("likeCount", Query.Direction.DESCENDING)
            }
            QuizSortOption.NEWEST -> {
                query = query.orderBy("createdAt", Query.Direction.DESCENDING)
            }
            QuizSortOption.OLDEST -> {
                query = query.orderBy("createdAt", Query.Direction.ASCENDING)
            }
        }

        // 페이지네이션
        if (lastDocId != null) {
            val lastDoc = quizGroupsCollection.document(lastDocId).get().await()
            query = query.startAfter(lastDoc)
        }

        val result = query.limit(limit.toLong())
            .get()
            .await()
            .documents
            .map { document ->
                document.data?.toMutableMap()?.apply {
                    this["id"] = document.id
                } ?: mapOf()
            }

        return result
    }

    /** 퀴즈 그룹을 생성합니다. */
    suspend fun createQuizGroup(quizGroupData: Map<String, Any>): String {
        val docRef = quizGroupsCollection
            .add(quizGroupData)
            .await()
        return docRef.id
    }

    /** 퀴즈 그룹을 조회합니다. */
    suspend fun getQuizGroup(quizGroupId: String): Map<String, Any>? {
        return quizGroupsCollection
            .document(quizGroupId)
            .get()
            .await()
            .data
    }

    /** 퀴즈 그룹을 업데이트합니다. */
    suspend fun updateQuizGroup(quizGroupId: String, updates: Map<String, Any>) {
        quizGroupsCollection
            .document(quizGroupId)
            .update(updates)
            .await()
    }

    /** 퀴즈 그룹을 삭제합니다. */
    suspend fun deleteQuizGroup(quizGroupId: String) {
        quizGroupsCollection
            .document(quizGroupId)
            .delete()
            .await()
    }

    /** 추천수가 특정 수 이상인 모든 사용자의 퀴즈 그룹 목록을 조회합니다. */
    suspend fun getAllTopRatedQuizGroups(minLikes: Int): List<Map<String, Any>> {
        return try {
            quizGroupsCollection
                .whereGreaterThanOrEqualTo("likeCount", minLikes)
                .orderBy("likeCount", Query.Direction.DESCENDING)
                .get()
                .await()
                .documents
                .mapNotNull { document ->
                    document.data?.plus("id" to document.id)
                }
        } catch (e: Exception) {
            emptyList()
        }
    }
}