package com.kwon.chosungmarket.data.db

import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.Locale

/**
 * Firebase의 명예의 전당 데이터에 접근하는 클래스
 */
class FirebaseHallOfFameDb(
    private val firestore: FirebaseFirestore
) {
    private val hallOfFameCollection = firestore.collection("hallOfFame")
    private val timestampDoc = hallOfFameCollection.document("timestamp")

    /** 오늘 날짜의 명예의 전당 데이터가 있는지 확인합니다. */
    suspend fun checkTodayRankings(): Boolean {
        val today = getServerDate()
        val snapshot = hallOfFameCollection.document(today).get().await()
        return snapshot.exists()
    }

    /** 명예의 전당 데이터를 저장합니다. */
    suspend fun saveRankings(rankings: List<Map<String, Any>>) {
        val today = getServerDate()
        val rankingData = mapOf(
            "rankings" to rankings,
            "updatedAt" to com.google.firebase.firestore.FieldValue.serverTimestamp()
        )
        hallOfFameCollection.document(today).set(rankingData).await()
    }

    /** 오늘 날짜의 명예의 전당 데이터를 조회합니다. */
    suspend fun getTodayRankings(): List<Map<String, Any>>? {
        val today = getServerDate()
        val snapshot = hallOfFameCollection.document(today).get().await()
        return snapshot.data?.get("rankings") as? List<Map<String, Any>>
    }

    /** 특정 날짜의 명예의 전당 데이터에서 퀴즈 그룹을 삭제합니다. */
    suspend fun deleteFromRankings(quizGroupId: String, date: String) {
        val document = hallOfFameCollection.document(date).get().await()

        if (document.exists()) {
            val rankings = document.data?.get("rankings") as? List<Map<String, Any>> ?: return

            // 해당 퀴즈 그룹을 제외한 새로운 랭킹 리스트 생성
            val updatedRankings = rankings.filter {
                it["id"] != quizGroupId
            }.mapIndexed { index, ranking ->
                // 순위 재계산
                ranking + mapOf("rank" to (index + 1))
            }

            // 업데이트된 랭킹 저장
            hallOfFameCollection.document(date).update(
                mapOf(
                    "rankings" to updatedRankings,
                    "updatedAt" to com.google.firebase.firestore.FieldValue.serverTimestamp()
                )
            ).await()
        }
    }

    /** 서버 날짜를 가져옵니다. */
    private suspend fun getServerDate(): String {
        // 먼저 timestamp 문서 생성
        timestampDoc
            .set(mapOf("timestamp" to com.google.firebase.firestore.FieldValue.serverTimestamp()))
            .await()

        // 생성된 문서에서 timestamp 조회
        val snapshot = timestampDoc.get().await()
        val timestamp = snapshot.getTimestamp("timestamp") ?: Timestamp.now()

        val date = timestamp.toDate()
        val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return formatter.format(date)
    }
}